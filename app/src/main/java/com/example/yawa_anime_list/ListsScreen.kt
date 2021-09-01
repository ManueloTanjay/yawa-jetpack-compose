package com.example.yawa_anime_list

import GetCurrentAnimeListQuery
import android.content.SharedPreferences
import android.util.Log
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.exception.ApolloException
import com.apollographql.apollo3.network.http.HttpNetworkTransport
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData


/**
 *  Display session token and when it expires for now
 *  Will display MediaList once Apollo3 is installed and configured
 */
@Composable
fun ListsScreen(sharedPreferences: SharedPreferences, store: ViewModelStoreOwner) {
    val sessionToken = sharedPreferences.getString("sessionToken", null)
    val sTokenExpiration = sharedPreferences.getString("sessionTokenExpiry", null)
    val username = sharedPreferences.getString("username", null)
    val userID = sharedPreferences.getString("userID", null)
    val userMediaListOptions = sharedPreferences.getString("userMediaListOptions", null)

//    val (page, setPage) = remember {
//        mutableStateOf<GetCurrentAnimeListQuery.Page?>(null)
//    }
//
//    LaunchedEffect(page) {
//        setPage(getCurrentAnimeList(sessionToken.toString(), 1, username.toString()))
//        Log.d("QQQQ", page.toString())
//    }

    val viewModel = ViewModelProvider(store).get(ListsScreenViewModel::class.java)
    viewModel.getMediaList(sessionToken.toString(), username.toString())

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("MediaList") })
        },
        modifier = Modifier
            .fillMaxSize()
    ) {
//        viewModel.liveMedia
        MediaListItem(viewModel.liveMedia, viewModel, sessionToken.toString(), username.toString())
//        Column() {
////            Text(
////                "username: " + username
////                        + "\nuserID: " + userID
////                        + "\nuserMediaListOptions: " + userMediaListOptions
////                        + "\nSession Token expiration: " + sTokenExpiration
////                        + "\nSession Token: " + sessionToken
////            )
////            Button(onClick = {setPage(page)}) {
////                Text(text = "AAA")
////            }
////            Text(page?.pageInfo.toString())
////            Text(page?.mediaList.toString())
//        }
    }
}

@Composable
fun MediaListItem(
    liveMedia: LiveData<List<GetCurrentAnimeListQuery.MediaList?>?>,
    viewModel: ListsScreenViewModel,
    sessionToken: String,
    userName: String
) {

    val media by liveMedia.observeAsState(initial = emptyList())
    
    LazyColumn() {
        itemsIndexed(media!!.toList()) { index, item ->

            if (index == media?.lastIndex) {
                Log.d("DEEZ NUTS", "last index")
                viewModel.getMediaList(sessionToken, userName)
            }

            Text(
                index.toString() + ": " + item?.media?.title?.romaji.toString(),
                modifier = Modifier.padding(16.dp)
            )
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