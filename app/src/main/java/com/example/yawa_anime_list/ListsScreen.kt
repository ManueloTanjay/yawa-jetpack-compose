package com.example.yawa_anime_list

import GetCurrentAnimeListQuery
import android.content.SharedPreferences
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.exception.ApolloException
import com.apollographql.apollo3.network.http.HttpNetworkTransport


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

    val (page, setPage) = remember {
        mutableStateOf<GetCurrentAnimeListQuery.Page?>(null)
    }

    LaunchedEffect(page) {
        setPage(getCurrentAnimeList(sessionToken.toString(), 1, username.toString()))
        Log.d("QQQQ", page.toString())
    }


    Scaffold(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column() {
//            Text(
//                "username: " + username
//                        + "\nuserID: " + userID
//                        + "\nuserMediaListOptions: " + userMediaListOptions
//                        + "\nSession Token expiration: " + sTokenExpiration
//                        + "\nSession Token: " + sessionToken
//            )
//            Button(onClick = {setPage(page)}) {
//                Text(text = "AAA")
//            }
            Text(page?.pageInfo.toString())
            Text(page?.mediaList.toString())
        }
    }
}

suspend fun getCurrentAnimeList(
    sessionToken: String,
    page: Int,
    userName: String
): GetCurrentAnimeListQuery.Page? {

    val apolloClient = ApolloClient(
        networkTransport = HttpNetworkTransport(
            serverUrl = "https://graphql.anilist.co/",
            interceptors = listOf(AuthorizationInterceptor(sessionToken))
        )
    )

    val userCurrentAnimeList = try {
        apolloClient.query(GetCurrentAnimeListQuery(page, userName))
    } catch (e: ApolloException) {
        Log.d("GETUSERMEDISLISTOPTIONS", e.toString())
        return null
    }

    val userCurrentAnimeListData = userCurrentAnimeList.data?.page
    if (userCurrentAnimeListData == null || userCurrentAnimeList.hasErrors()) {
        return null
    }

    Log.d("USERCURRANIDATA", userCurrentAnimeListData.toString())
    return userCurrentAnimeListData
}