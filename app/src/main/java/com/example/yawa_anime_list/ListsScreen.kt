package com.example.yawa_anime_list

import android.content.SharedPreferences
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier


/**
 *  Display session token and when it expires for now
 *  Will display MediaList once Apollo3 is installed and configured
 */
@Composable
fun ListsScreen(sharedPreferences: SharedPreferences) {
    val sessionToken = sharedPreferences.getString("sessionToken", null)
    val sTokenExpiration = sharedPreferences.getString("sessionTokenExpiry", null)
    val username = sharedPreferences.getString("username", null)
    val userID = sharedPreferences.getString("userID", null)
    val userMediaListOptions = sharedPreferences.getString("userMediaListOptions", null)

    Scaffold(modifier = Modifier.fillMaxSize()) {
        Text("username: " + username
                + "\nuserID: " + userID
                + "\nuserMediaListOptions: " + userMediaListOptions
                + "\nSession Token expiration: " + sTokenExpiration
                + "\nSession Token: " + sessionToken
        )
    }
}