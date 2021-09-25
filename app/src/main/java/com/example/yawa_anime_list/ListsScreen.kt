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
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.rounded.Book
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
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
    /*TODO: put into for loop in separate function /// use separate coroutines and join // put in init{}*/
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
                        icon = Icons.Rounded.PlayArrow
                    ),
                    BottomNavBarOption(
                        name = "MANGA",
                        route = "manga",
                        icon = Icons.Rounded.Book
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

    /*TODO: make listStates for each of the 10 lists and have them observed here so that scroll position is remembered
       through recomposition*/
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
                MediaItemCard(
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