package com.example.yawa_anime_list

import GetCurrentAnimeListQuery
import android.content.SharedPreferences
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import coil.compose.rememberImagePainter


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

    val viewModel = ViewModelProvider(store).get(ListsScreenViewModel::class.java)
    viewModel.getMediaList(sessionToken.toString(), username.toString())

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("MediaList") })
        },
        modifier = Modifier
            .fillMaxSize()

    ) {
        MediaList(viewModel.liveMedia, viewModel, sessionToken.toString(), username.toString())
    }
}

@Composable
fun MediaList(
    liveMedia: LiveData<List<GetCurrentAnimeListQuery.MediaList?>?>,
    viewModel: ListsScreenViewModel,
    sessionToken: String,
    userName: String
) {
    val media by liveMedia.observeAsState(initial = emptyList())

    LazyColumn(
        modifier = Modifier
            .background(Color(0xff404040))
    ) {
        itemsIndexed(media!!.toList()) { index, item ->

            if (index == media?.lastIndex?.minus(10)) {
                Log.d("DEEZ NUTS", "last index")
                viewModel.getMediaList(sessionToken, userName)
            }
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                shape = RoundedCornerShape(4.dp),
                elevation = 2.dp
            ) {
                MediaItem(
                    modifier = Modifier
                        .padding(0.dp)
                        .background(Color(0xff252525))
                        .fillParentMaxWidth(), index = index, item = item
                )
            }
        }
    }
}

@Composable
fun MediaItem (modifier: Modifier, index: Int, item: GetCurrentAnimeListQuery.MediaList?) {
    Row(
        modifier = modifier
    ) {
        Image(
            painter = rememberImagePainter(item?.media?.coverImage?.medium),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(Constants.IMAGE_WIDTH, Constants.IMAGE_HEIGHT)
                .background(Color.Gray)
        )
        Text(
            item?.media?.title?.romaji.toString(),
            modifier = Modifier
//                        .padding(10.dp)
                .background(Color.Yellow)
        )
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