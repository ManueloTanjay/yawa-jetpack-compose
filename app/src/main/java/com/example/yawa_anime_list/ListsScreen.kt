package com.example.yawa_anime_list

import GetMediaListQuery
import android.content.SharedPreferences
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import coil.compose.rememberImagePainter
import type.MediaListStatus
import type.MediaType


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

    val (mediaType, setMediaType) = remember {
        mutableStateOf("Anime")
    }

    val viewModel = ViewModelProvider(store).get(ListsScreenViewModel::class.java)
    viewModel.getMediaList(
        sessionToken.toString(),
        username.toString(),
        Constants.COMPLETED,
        Constants.ANIME
    )

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(mediaType) })
        },
        modifier = Modifier
            .fillMaxSize()

    ) {
        Column(modifier = Modifier.background(Constants.BGCOLOR)) {
            Spacer(modifier = Modifier.height(4.dp))
            MediaList(
                viewModel.liveMedia,
                viewModel,
                sessionToken.toString(),
                username.toString(),
                Constants.COMPLETED,
                Constants.ANIME
            )
        }
    }
}

/**
 *  Layout for a list of MediaItems
 *  LazyColumn that represents each item with a Card that contains a MediaItem
 */
@Composable
fun MediaList(
    liveMedia: LiveData<List<GetMediaListQuery.MediaList?>?>,
    viewModel: ListsScreenViewModel,
    sessionToken: String,
    userName: String,
    mediaListStatus: MediaListStatus,
    mediaType: MediaType
) {
    val media by liveMedia.observeAsState(initial = emptyList())
    val listState = rememberLazyListState()

    LazyColumn(
        state = listState,
        modifier = Modifier
            .background(Constants.BGCOLOR)
            .fillMaxSize()
    ) {
        itemsIndexed(media!!.toList()) { index, item ->

            if (index == media?.lastIndex?.minus(10)) {
                Log.d("DEEZ NUTS", "last index")
                viewModel.getMediaList(sessionToken, userName, mediaListStatus, mediaType)
            }
            Card(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(4.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = rememberRipple()
                    ) {
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
                        .fillMaxSize(),
                    item = item,
                    mediaType
                )
            }
        }
    }
}


/**
 *   Layout representing a MediaItem which is made up of an Image, title, score, and progress
 *   works for both anime and manga (they have different ways of tracking progress)
 */
@Composable
fun MediaItem(
    modifier: Modifier,
    item: GetMediaListQuery.MediaList?,
    mediaType: MediaType
) {
    Row(
        modifier = modifier
            .fillMaxSize()
//            .background(Color.Blue)
    ) {
        Image(
            painter = rememberImagePainter(item?.media?.coverImage?.large),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(Constants.IMAGE_WIDTH, Constants.IMAGE_HEIGHT)
                .background(Color.Gray)
        )
        Column(
            modifier = Modifier
                .height(Constants.IMAGE_HEIGHT)
                .fillMaxWidth(),
//                .background(Color.Green),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceAround
        ) {
            Text(
                item?.media?.title?.romaji.toString(),
                modifier = Modifier
                    .background(Color.Yellow)
//                    .padding(15.dp)
            )
            if (mediaType == Constants.ANIME) {
                AnimeProgress(item = item)
            } else {
                MangaProgress(item = item)
            }
        }
    }
}

/**
 *  Layout for anime stats which only tracks episodes
 */
@Composable
fun AnimeProgress(
    item: GetMediaListQuery.MediaList?,
) {
    Text(
        text = "Score: " + item?.score.toString() + "/10.0",
        modifier = Modifier
//                        .padding(10.dp)
            .background(Color.Yellow)
    )
    Text(
        text = "Progress: " + item?.progress + "/" + item?.media?.episodes.toString(),
        modifier = Modifier
            .background(Color.Yellow)
    )
}

/**
 *  Layout for manga progress which tracks both chapters and volumes
 */
@Composable
fun MangaProgress(
    item: GetMediaListQuery.MediaList?,
) {
    Text(
        text = "Score: " + item?.score.toString() + "/10.0",
        modifier = Modifier
            .background(Color.Yellow)
    )
    Text(
        text = "Chapters: " + item?.progress + "/" + item?.media?.chapters,
        modifier = Modifier
//                        .padding(10.dp)
            .background(Color.Yellow)
    )
    Text(
        text = "Volumes: " + item?.progressVolumes + "/" + item?.media?.volumes,
        modifier = Modifier
            .background(Color.Yellow)
    )
}