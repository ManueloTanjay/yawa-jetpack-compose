package com.example.yawa_anime_list

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
import com.example.yawa_anime_list.ui.theme.YawaanimelistTheme

class MainActivity : ComponentActivity() {

    private val TAG = "MainActivityTAG"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //get url with session token and bearer from intent
        val action: String? = intent?.action;
        val data: Uri? = intent?.data
        //get session token from url
        var sessionToken: String? = data?.fragment?.split("=")?.get(1)?.split("&")?.get(0)
        var sTokenExpiration = data?.fragment?.split("=")?.get(3)

        //shared preferences
        val sharedPreferences = getSharedPreferences("yawa", MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        if(hasLoggedIn(sharedPreferences)) {
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
            Log.d(TAG, "First time user log in, storing session token in sharedPrefs and going to ListsScreen")
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
    val intent = remember { Intent(Intent.ACTION_VIEW, Uri.parse("https://anilist.co/api/v2/oauth/authorize?client_id=5828&response_type=token")) }

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