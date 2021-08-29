package com.example.yawa_anime_list

import android.content.Intent
import android.net.Uri
import android.os.Bundle
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (true) {
            setContent {
                LoginScreen()
            }
        } else {
            setContent {
                ListsScreen()
            }
        }
    }
}

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
            Text(text = "Login with Anilist")
        }
    }
}

@Composable
fun ListsScreen() {
    Scaffold(modifier = Modifier.fillMaxSize()) {
        Text("Android")
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    YawaanimelistTheme {
        LoginScreen()
    }
}