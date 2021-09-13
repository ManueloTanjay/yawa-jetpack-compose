package com.example.yawa_anime_list

import GetMediaListQuery
import android.content.SharedPreferences
import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.rounded.Add
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberImagePainter
import com.google.accompanist.pager.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import type.MediaListStatus
import type.MediaType


/**
 *  Display session token and when it expires for now
 *  Will display MediaList once Apollo3 is installed and configured
 */
@ExperimentalMaterialApi
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
    //anime
    viewModel.getMediaList(
        sessionToken.toString(),
        username.toString(),
        Constants.CURRENT,
        Constants.ANIME
    )
    viewModel.getMediaList(
        sessionToken.toString(),
        username.toString(),
        Constants.COMPLETED,
        Constants.ANIME
    )
    viewModel.getMediaList(
        sessionToken.toString(),
        username.toString(),
        Constants.PLANNING,
        Constants.ANIME
    )
    viewModel.getMediaList(
        sessionToken.toString(),
        username.toString(),
        Constants.PAUSED,
        Constants.ANIME
    )
    viewModel.getMediaList(
        sessionToken.toString(),
        username.toString(),
        Constants.DROPPED,
        Constants.ANIME
    )
    //manga
    viewModel.getMediaList(
        sessionToken.toString(),
        username.toString(),
        Constants.CURRENT,
        Constants.MANGA
    )
    viewModel.getMediaList(
        sessionToken.toString(),
        username.toString(),
        Constants.COMPLETED,
        Constants.MANGA
    )
    viewModel.getMediaList(
        sessionToken.toString(),
        username.toString(),
        Constants.PLANNING,
        Constants.MANGA
    )
    viewModel.getMediaList(
        sessionToken.toString(),
        username.toString(),
        Constants.PAUSED,
        Constants.MANGA
    )
    viewModel.getMediaList(
        sessionToken.toString(),
        username.toString(),
        Constants.DROPPED,
        Constants.MANGA
    )

    val navController = rememberNavController()
    Scaffold(
        topBar = {
            TopAppBar(title = { Text(mediaType) })
        },
        modifier = Modifier
            .fillMaxSize(),
        bottomBar = {
            BottomNavigationBar(
                items = listOf(
                    BottomNavBarOption(
                        name = "ANIME",
                        route = "anime",
                        icon = Icons.Default.Home
                    ),
                    BottomNavBarOption(
                        name = "MANGA",
                        route = "manga",
                        icon = Icons.Default.Notifications
                    ),
                    BottomNavBarOption(
                        name = "SETTINGS",
                        route = "settings",
                        icon = Icons.Default.Settings
                    ),
                ),
                navController = navController,
                onItemClick = {
                    navController.navigate(it.route)
                })
        }

    ) {
        Column(modifier = Modifier.background(Constants.BGCOLOR)) {
            Spacer(modifier = Modifier.height(4.dp))
            Navigation(
                navController = navController,
                viewModel = viewModel,
                sessionToken = sessionToken.toString(),
                username = username.toString(),
            )
        }
    }
}

/**
 *  Main navigation composable
 */
@Composable
fun Navigation(
    navController: NavHostController,
    viewModel: ListsScreenViewModel,
    sessionToken: String,
    username: String,
) {
    NavHost(navController = navController, startDestination = "anime") {
        composable("anime") {
            MediaListWrapper(
                viewModel = viewModel,
                sessionToken = sessionToken,
                username = username,
                mediaType = Constants.ANIME,
                tabs = listOf("WATCHING", "COMPLETED", "PLANNING", "PAUSED", "DROPPED")
            )
        }
        composable("manga") {
            MediaListWrapper(
                viewModel = viewModel,
                sessionToken = sessionToken,
                username = username,
                mediaType = Constants.MANGA,
                tabs = listOf("READING", "COMPLETED", "PLANNING", "PAUSED", "DROPPED")
            )
        }
        composable("settings") {
            Text(text = "SETTINGS HERE")
        }
    }
}

@ExperimentalMaterialApi
@Composable
fun BottomNavigationBar(
    items: List<BottomNavBarOption>,
    navController: NavController,
    modifier: Modifier = Modifier,
    onItemClick: (BottomNavBarOption) -> Unit
) {
    val backStackEntry = navController.currentBackStackEntryAsState()
    BottomNavigation(
        modifier = modifier,
        backgroundColor = Constants.TABCOLOR,
        elevation = 16.dp
    ) {
        items.forEach { item ->
            val selected = item.route == backStackEntry.value?.destination?.route
            BottomNavigationItem(
                selected = selected,
                selectedContentColor = Constants.SELECTEDCOLOR,
                unselectedContentColor = Constants.UNSELECTEDCOLOR,
                onClick = { onItemClick(item) },
                icon = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.name
                        )
                        if (selected) {
                            Text(
                                text = item.name,
                                textAlign = TextAlign.Center,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            )
        }
    }
}

/**
 *  MediaListWrapper to have different pages for anime and manga
 */
@OptIn(ExperimentalPagerApi::class)
@Composable
fun MediaListWrapper(
    viewModel: ListsScreenViewModel,
    sessionToken: String,
    username: String,
    mediaType: MediaType,
    tabs: List<String>
) {
    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState(
        pageCount = tabs.size,
        initialOffscreenLimit = 2,
        infiniteLoop = true,
        initialPage = 0
    )

    //set which list to follow based on MediaType
    var currentMedia = MutableLiveData<List<GetMediaListQuery.MediaList?>?>()
    var completedMedia = MutableLiveData<List<GetMediaListQuery.MediaList?>?>()
    var planningMedia = MutableLiveData<List<GetMediaListQuery.MediaList?>?>()
    var pausedMedia = MutableLiveData<List<GetMediaListQuery.MediaList?>?>()
    var droppedMedia = MutableLiveData<List<GetMediaListQuery.MediaList?>?>()
    when (mediaType) {
        Constants.ANIME -> {
            currentMedia = viewModel.liveMediaCurrentAnime
            completedMedia = viewModel.liveMediaCompletedAnime
            planningMedia = viewModel.liveMediaPlanningAnime
            pausedMedia = viewModel.liveMediaPausedAnime
            droppedMedia = viewModel.liveMediaDroppedAnime
        }
        Constants.MANGA -> {
            currentMedia = viewModel.liveMediaCurrentManga
            completedMedia = viewModel.liveMediaCompletedManga
            planningMedia = viewModel.liveMediaPlanningManga
            pausedMedia = viewModel.liveMediaPausedManga
            droppedMedia = viewModel.liveMediaDroppedManga
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 60.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        MediaStatusTabRow(tabs = tabs, coroutineScope = coroutineScope, pagerState = pagerState)
        HorizontalPager(state = pagerState) { selectedTabIndex ->
            when (selectedTabIndex) {
                0 -> MediaList(
                    currentMedia,
                    viewModel,
                    sessionToken,
                    username,
                    Constants.CURRENT,
                    mediaType
                )
                1 -> MediaList(
                    completedMedia,
                    viewModel,
                    sessionToken,
                    username,
                    Constants.COMPLETED,
                    mediaType
                )
                2 -> MediaList(
                    planningMedia,
                    viewModel,
                    sessionToken,
                    username,
                    Constants.PLANNING,
                    mediaType
                )
                3 -> MediaList(
                    pausedMedia,
                    viewModel,
                    sessionToken,
                    username,
                    Constants.PAUSED,
                    mediaType
                )
                4 -> MediaList(
                    droppedMedia,
                    viewModel,
                    sessionToken,
                    username,
                    Constants.DROPPED,
                    mediaType
                )
            }
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
                    mediaType,
                    viewModel,
                    mediaListStatus
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
    mediaType: MediaType,
    viewModel: ListsScreenViewModel,
    mediaListStatus: MediaListStatus
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
//        Spacer(modifier = Modifier.width(10.dp))
        Column(
            modifier = Modifier
                .height(Constants.IMAGE_HEIGHT)
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 5.dp)
//                .background(Color.Green)
        ) {
            Text(
                item?.media?.title?.romaji.toString(),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier
//                    .background(Color.Yellow)
                    .fillMaxWidth(0.8F)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
//                    .background(Color.Red)
            ) {
                Text(
                    Constants.parseMediaFormat(item?.media?.format.toString()),
                    fontSize = 12.sp,
                    color = Color.White,
//                    modifier = Modifier.background(Color.Magenta)
                )
                if (mediaType == Constants.ANIME)
                    Text(
                        "  •  " + Constants.parseMediaSeason(item?.media?.season.toString()) + ", " + item?.media?.seasonYear.toString(),
                        fontSize = 12.sp,
                        color = Color.White
                    )
            }
            if (mediaType == Constants.ANIME) {
                AnimeProgress(item = item, viewModel, mediaListStatus)
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
//    index: Int,
    viewModel: ListsScreenViewModel,
    mediaListStatus: MediaListStatus
) {
    val (progress, setProgress) = remember {
        when (mediaListStatus) {
            Constants.CURRENT -> {
                mutableStateOf(viewModel.currAnimeProg[item?.id!!.toInt()].toString())
            }
            Constants.COMPLETED -> {
                mutableStateOf(viewModel.comAnimeProg[item?.id!!.toInt()].toString())
            }
            Constants.PLANNING -> {
                mutableStateOf(viewModel.planAnimeProg[item?.id!!.toInt()].toString())
            }
            Constants.PAUSED -> {
                mutableStateOf(viewModel.pauseAnimeProg[item?.id!!.toInt()].toString())
            }
            Constants.DROPPED -> {
                mutableStateOf(viewModel.dropAnimeProg[item?.id!!.toInt()].toString())
            }
            else -> mutableStateOf("0")
        }
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Spacer(
            modifier = Modifier
                .fillMaxHeight(0.30F)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Column(
//                modifier = Modifier.background(Color.Red),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "EPISODES",
                    fontSize = 10.sp,
                    color = Color.White,
                )
                Card(
                    modifier = Modifier
                        .background(Constants.CARDCOLOR)
//                    .width(34.dp)
                        .height(28.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = rememberRipple()
                        ) {
                            Log.d(
                                "PROGRESS_CLICKED",
                                item?.media?.title?.romaji.toString() + " should pull up TextField"
                            )
                        },
                    shape = RoundedCornerShape(0.dp),
                    elevation = 0.dp
                ) {
                    Row(
                        modifier = Modifier
                            .background(Constants.CARDCOLOR)
                    ) {
                        Text(
                            text = progress,
                            color = Color.White,
                            fontSize = 20.sp
                        )
                        Text(
                            text = "/" + item?.media?.episodes.toString(),
                            color = Color.White,
                            fontSize = 20.sp
                        )

                    }
                }
            }
            Spacer(modifier = Modifier.width(2.dp))
            Card(
                modifier = Modifier
                    .background(Color.Transparent)
                    .width(28.dp)
                    .height(28.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = rememberRipple()
                    ) {
                        if (progress.toInt() + 1 <= item?.media?.episodes!!.toInt()) {
                            setProgress((progress.toInt() + 1).toString())

                            when (mediaListStatus) {
                                Constants.CURRENT -> {
                                    viewModel.currAnimeProg[item.id] =
                                        viewModel.currAnimeProg[item.id]!!.plus(1)
                                }
                                Constants.COMPLETED -> {
                                    viewModel.comAnimeProg[item.id] =
                                        viewModel.comAnimeProg[item.id]!!.plus(1)
                                }
                                Constants.PLANNING -> {
                                    viewModel.planAnimeProg[item.id] =
                                        viewModel.planAnimeProg[item.id]!!.plus(1)
                                }
                                Constants.PAUSED -> {
                                    viewModel.pauseAnimeProg[item.id] =
                                        viewModel.pauseAnimeProg[item.id]!!.plus(1)
                                }
                                Constants.DROPPED -> {
                                    viewModel.dropAnimeProg[item.id] =
                                        viewModel.dropAnimeProg[item.id]!!.plus(1)
                                }
                            }
                        } else
                            setProgress(item.media.episodes.toString())
                        Log.d(
                            "+_CLICKED",
                            item.media.title?.romaji.toString() + " episodes incremented"
                        )
                    },
                shape = RoundedCornerShape(0.dp),
                elevation = 0.dp
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.background(Constants.CARDCOLOR)
                ) {
                    Icon(
                        Icons.Rounded.Add,
                        tint = Color.White,
                        contentDescription = "Localized description"
                    )
                }
            }
        }
    }
}

/**
 *  Layout for manga progress which tracks both chapters and volumes
 */
@Composable
fun MangaProgress(
    item: GetMediaListQuery.MediaList?,
) {
//    Text(
//        text = "Score: " + item?.score.toString() + "/10.0",
//        modifier = Modifier
//            .background(Color.Yellow)
//    )
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

/**
 *  a Composable for the topbar that supports swiping between tabs
 */
@OptIn(ExperimentalPagerApi::class)
@Composable
fun MediaStatusTabRow(
    modifier: Modifier = Modifier,
    tabs: List<String>,
    coroutineScope: CoroutineScope,
    pagerState: PagerState
) {
    val (selectedTabIndex, setSelectedTabIndex) = remember {
        mutableStateOf(pagerState.currentPage)
    }
    val inactiveColor = Color(0xFF777777)
    TabRow(
        selectedTabIndex = selectedTabIndex,
        backgroundColor = Color.Transparent,
        contentColor = Color.White,
        modifier = modifier,
        indicator = { tabPositions ->
            TabRowDefaults.Indicator(
                Modifier.pagerTabIndicatorOffset(pagerState, tabPositions)
            )
        }
    ) {
        tabs.forEachIndexed { index, item ->
            Tab(
                selected = selectedTabIndex == index,
                selectedContentColor = Color.Black,
                unselectedContentColor = inactiveColor,
                onClick = {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                }
            ) {
                Text(
                    text = item,
                    modifier = Modifier.padding(vertical = 8.dp),
                    color = Color.White,
                    maxLines = 1,
                    textAlign = TextAlign.Center,
                    fontSize = 12.sp
                )
            }
        }
    }
}