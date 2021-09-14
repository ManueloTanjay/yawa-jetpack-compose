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
import com.example.yawa_anime_list.ui.theme.YawaanimelistTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val TAG = "MainActivityTAG"

    @ExperimentalMaterialApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //get url with session token and bearer from intent
        val action: String? = intent?.action
        val data: Uri? = intent?.data
        //get session token from url
        val sessionToken: String? = data?.fragment?.split("=")?.get(1)?.split("&")?.get(0)
        val sTokenExpiration = data?.fragment?.split("=")?.get(3)

        //shared preferences
        val sharedPreferences = getSharedPreferences("yawa", MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        /*TODO: check for session token expiration*/
        lifecycleScope.launch {
            if (hasLoggedIn(sharedPreferences)) {
                Log.d(TAG, "User has logged in previously")
                setContent {
                    ListsScreen(sharedPreferences, this@MainActivity)
                }
            } else if (!(sessionToken === null)) {
                //get userInfo
                val userInfo = getUserInfo(sessionToken.toString())

                editor.apply {
                    putString("sessionToken", sessionToken)
                    putString("sessionTokenExpiry", sTokenExpiration)
                    putString("username", userInfo?.username)
                    putString("userID", userInfo?.userID)
                    putString("userMediaListOptions", userInfo?.userMediaListOptions)

                    apply()
                }
                Log.d(
                    TAG,
                    "First time user log in, storing session token in sharedPrefs and going to ListsScreen"
                )
                setContent {
                    ListsScreen(sharedPreferences, this@MainActivity)
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

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    YawaanimelistTheme {
        LoginScreen()
    }
}

/***
 *  Gets user info by making 2 queries to the anilist API (first for username, userID; second for userMediaListOptions)
 */
suspend fun getUserInfo(sessionToken: String): User? {
    val TAG = "GetUserInfoTAG"
    val userInfo = User()
    val apolloClient = ApolloClient(
        networkTransport = HttpNetworkTransport(
            serverUrl = "https://graphql.anilist.co/",
            interceptors = listOf(AuthorizationInterceptor(sessionToken))
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