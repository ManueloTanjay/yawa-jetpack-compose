package com.example.yawa_anime_list

import GetUserMediaListOptionsQuery
import GetViewerQuery
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.http.HttpRequest
import com.apollographql.apollo3.api.http.HttpResponse
import com.apollographql.apollo3.api.http.withHeader
import com.apollographql.apollo3.exception.ApolloException
import com.apollographql.apollo3.network.http.HttpInterceptor
import com.apollographql.apollo3.network.http.HttpInterceptorChain
import com.apollographql.apollo3.network.http.HttpNetworkTransport
import com.example.yawa_anime_list.ui.theme.User
import com.example.yawa_anime_list.ui.theme.YawaanimelistTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val TAG = "MainActivityTAG"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //get url with session token and bearer from intent
        val action: String? = intent?.action
        val data: Uri? = intent?.data
        //get session token from url
        var sessionToken: String? = data?.fragment?.split("=")?.get(1)?.split("&")?.get(0)
        var sTokenExpiration = data?.fragment?.split("=")?.get(3)

        //shared preferences
        val sharedPreferences = getSharedPreferences("yawa", MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        lifecycleScope.launch {
            if (hasLoggedIn(sharedPreferences)) {
                sessionToken = sharedPreferences.getString("sessionToken", null)
                sTokenExpiration = sharedPreferences.getString("sessionTokenExpiry", null)
                Log.d(TAG, "User has logged in previously")
                setContent {
                    ListsScreen(sessionToken.toString(), sTokenExpiration.toString())
                }
            } else if (!(sessionToken === null)) {
                editor.apply {
                    putString("sessionToken", sessionToken)
                    putString("sessionTokenExpiry", sTokenExpiration)

                    apply()
                }
                Log.d(
                    TAG,
                    "First time user log in, storing session token in sharedPrefs and going to ListsScreen"
                )
                setContent {
                    ListsScreen(sessionToken.toString(), sTokenExpiration.toString())
                }
            } else {
                Log.d(TAG, "No user logged in, redirecting to LoginScreen")
                setContent {
                    LoginScreen()
                }
            }
        }
    }
}



/**
 *  check if session token for user already exists
 */
fun hasLoggedIn(sharedPreferences: SharedPreferences): Boolean {
    return sharedPreferences.contains("sessionToken")
}

/**
 *  contains button to go to AniList OAuth2 Login page
 */
@Composable
fun LoginScreen() {
    val context = LocalContext.current
    val intent = remember {
        Intent(
            Intent.ACTION_VIEW,
            Uri.parse("https://anilist.co/api/v2/oauth/authorize?client_id=5828&response_type=token")
        )
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = { context.startActivity(intent) }, modifier = Modifier) {
            Text(text = "Login with AniList")
        }
    }
}

/**
 *  Display session token and when it expires for now
 *  Will display MediaList once Apollo3 is installed and configured
 */
@Composable
fun ListsScreen(sessionToken: String, sTokenExpiration: String) {
    Scaffold(modifier = Modifier.fillMaxSize()) {
        Text("Session Token expiration: " + sTokenExpiration + "\nSession Token: " + sessionToken)
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    YawaanimelistTheme {
        LoginScreen()
    }
}

suspend fun getUserInfo(session_token: String): User? {
    val TAG = "GetUserInfoTAG"
    var userInfo = User()
    val apolloClient = ApolloClient(
        networkTransport = HttpNetworkTransport(
            serverUrl = "https://graphql.anilist.co/",
            interceptors = listOf(AuthorizationInterceptor(session_token))
        )
    )
    //get username and userID
    val viewerQueryResponse = try {
        apolloClient.query(GetViewerQuery())
    } catch (e: ApolloException) {
        Log.d("GETUSERINFO", e.toString())
        return null
    }

    val viewer = viewerQueryResponse.data?.viewer
    if (viewer == null || viewerQueryResponse.hasErrors())
        return null

    userInfo.username = viewer.name
    userInfo.userID = viewer.id.toString()
    Log.d(TAG, "${viewer.id} ${viewer.name}")
    //get userMediaListOptions
    val userMediaListOptionsResponse = try {
        apolloClient.query(GetUserMediaListOptionsQuery(userInfo.userID.toInt()))
    } catch (e: ApolloException) {
        Log.d("GETUSERMEDISLISTOPTIONS", e.toString())
        return null
    }

    val user = userMediaListOptionsResponse.data?.user
    if (user == null || userMediaListOptionsResponse.hasErrors())
        return null

    userInfo.userMediaListOptions = user.mediaListOptions?.scoreFormat?.rawValue.toString()
    return userInfo
}

class AuthorizationInterceptor(val token: String) : HttpInterceptor {
    override suspend fun intercept(request: HttpRequest, chain: HttpInterceptorChain): HttpResponse {
        return chain.proceed(request.withHeader("Authorization", "Bearer $token"))
    }
}