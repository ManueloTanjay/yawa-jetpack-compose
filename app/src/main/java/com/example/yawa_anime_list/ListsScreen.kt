package com.example.yawa_anime_list

import GetCurrentAnimeListQuery
import GetMediaListQuery
import android.content.SharedPreferences
import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
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
    userName: String,
) {
    val media by liveMedia.observeAsState(initial = emptyList())
    val listState = rememberLazyListState()

    LazyColumn(
        state = listState,
        modifier = Modifier
            .background(Constants.BGCOLOR)
    ) {
        itemsIndexed(media!!.toList()) { index, item ->

            if (index == media?.lastIndex?.minus(10)) {
                Log.d("DEEZ NUTS", "last index")
                viewModel.getMediaList(sessionToken, userName)
            }
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp)
                    .clickable {
                        Log.d(
                            "CARD_CLICKED",
                            item?.media?.title?.romaji.toString() + " clicked"
                        )
                    },
                shape = RoundedCornerShape(4.dp),
                elevation = 8.dp
            ) {
                MediaItem(
                    modifier = Modifier
                        .padding(0.dp)
                        .background(Constants.CARDCOLOR)
                        .fillParentMaxWidth(), index = index, item = item
                )
            }
        }
    }
}

@Composable
fun MediaItem(modifier: Modifier, index: Int, item: GetCurrentAnimeListQuery.MediaList?) {
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