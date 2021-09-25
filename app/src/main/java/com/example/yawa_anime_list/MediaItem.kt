package com.example.yawa_anime_list

import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import type.MediaListStatus
import type.MediaType


/**
 *   Layout representing a MediaItem which is made up of an Image, title, score, and progress
 *   works for both anime and manga (they have different ways of tracking progress)
 */
@OptIn(ExperimentalCoilApi::class)
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
//                .padding(horizontal = 10.dp, vertical = 0.dp)
//                .background(Color.Green)
        ) {
            Text(
                item?.media?.title?.romaji.toString(),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier
//                    .background(Color.Yellow)
                    .fillMaxWidth(0.8F)
                    .padding(top = 5.dp, start = 10.dp)
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
                    modifier = Modifier
                        .padding(start = 10.dp)
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
                MangaProgress(item = item, viewModel, mediaListStatus)
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
    val animatedProgress = animateFloatAsState(
        targetValue = progress.toFloat(),
        animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec
    ).value
    Box(
        modifier = Modifier
            .fillMaxSize()
//            .background(Color.Green)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
//            .background(Color.Red)
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
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = "EPISODES",
                        fontSize = 10.sp,
                        color = Color.White,
                    )
                    Row(
                        modifier = Modifier
                            .background(Constants.CARDCOLOR)
                            .height(28.dp)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = rememberRipple()
                            ) {
                                Log.d(
                                    "PROGRESS_CLICKED",
                                    item?.media?.title?.romaji.toString() + " should pull up TextField"
                                )
                            }
                    ) {
                        Text(
                            text = progress,
                            color = Color.White,
                            fontSize = 20.sp
                        )
                        Text(
                            text = "/" + if (item?.media?.episodes.toString() == "null") " - " else item?.media?.episodes.toString(),
                            color = Color.White,
                            fontSize = 20.sp
                        )

                    }
                }
                Spacer(modifier = Modifier.width(2.dp))
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .background(Constants.CARDCOLOR)
                        .padding(2.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = rememberRipple()
                        ) {
                            val episodes = item?.media?.episodes ?: Constants.MAXINT
                            if (progress.toInt() + 1 <= episodes) {
                                setProgress((progress.toInt() + 1).toString())
                                when (mediaListStatus) {
                                    Constants.CURRENT -> {
                                        viewModel.currAnimeProg[item!!.id] =
                                            viewModel.currAnimeProg[item.id]!!.plus(1)
                                    }
                                    Constants.COMPLETED -> {
                                        viewModel.comAnimeProg[item!!.id] =
                                            viewModel.comAnimeProg[item.id]!!.plus(1)
                                    }
                                    Constants.PLANNING -> {
                                        viewModel.planAnimeProg[item!!.id] =
                                            viewModel.planAnimeProg[item.id]!!.plus(1)
                                    }
                                    Constants.PAUSED -> {
                                        viewModel.pauseAnimeProg[item!!.id] =
                                            viewModel.pauseAnimeProg[item.id]!!.plus(1)
                                    }
                                    Constants.DROPPED -> {
                                        viewModel.dropAnimeProg[item!!.id] =
                                            viewModel.dropAnimeProg[item.id]!!.plus(1)
                                    }
                                }
                            } else
                                setProgress(item?.media?.episodes.toString())
                            Log.d(
                                "+_CLICKED",
                                item?.media?.title?.romaji.toString() + " episodes incremented"
                            )
                        }
                ) {
                    Icon(
                        Icons.Rounded.Add,
                        tint = Color.White,
                        contentDescription = "Localized description"
                    )
                }
            }
        }
        Box(modifier = Modifier.align(Alignment.BottomCenter)) {
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth(),
                progress = animatedProgress / (item?.media?.episodes?.toFloat()
                    ?: animatedProgress),
                color = Color(0xFF32C91E)
            )
        }
    }
}

/**
 *  Layout for manga progress which tracks both chapters and volumes
 */
@Composable
fun MangaProgress(
    item: GetMediaListQuery.MediaList?,
    viewModel: ListsScreenViewModel,
    mediaListStatus: MediaListStatus,
) {
    val (progress, setProgress) = remember {
        when (mediaListStatus) {
            Constants.CURRENT -> {
                mutableStateOf(viewModel.currMangaProg[item?.id!!.toInt()].toString())
            }
            Constants.COMPLETED -> {
                mutableStateOf(viewModel.comMangaProg[item?.id!!.toInt()].toString())
            }
            Constants.PLANNING -> {
                mutableStateOf(viewModel.planMangaProg[item?.id!!.toInt()].toString())
            }
            Constants.PAUSED -> {
                mutableStateOf(viewModel.pauseMangaProg[item?.id!!.toInt()].toString())
            }
            Constants.DROPPED -> {
                mutableStateOf(viewModel.dropMangaProg[item?.id!!.toInt()].toString())
            }
            else -> mutableStateOf("0")
        }
    }
    val animatedProgress = animateFloatAsState(
        targetValue = progress.toFloat(),
        animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec
    ).value
    val (volProgress, setVolProgress) = remember {
        when (mediaListStatus) {
            Constants.CURRENT -> {
                mutableStateOf(viewModel.currMangaVolProg[item?.id!!.toInt()].toString())
            }
            Constants.COMPLETED -> {
                mutableStateOf(viewModel.comMangaVolProg[item?.id!!.toInt()].toString())
            }
            Constants.PLANNING -> {
                mutableStateOf(viewModel.planMangaVolProg[item?.id!!.toInt()].toString())
            }
            Constants.PAUSED -> {
                mutableStateOf(viewModel.pauseMangaVolProg[item?.id!!.toInt()].toString())
            }
            Constants.DROPPED -> {
                mutableStateOf(viewModel.dropMangaVolProg[item?.id!!.toInt()].toString())
            }
            else -> mutableStateOf("0")
        }
    }
    val animatedVolProgress = animateFloatAsState(
        targetValue = volProgress.toFloat(),
        animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec
    ).value

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {

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
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = "CHAPTERS",
                        fontSize = 10.sp,
                        color = Color.White,
                    )
                    Row(
                        modifier = Modifier
                            .background(Constants.CARDCOLOR)
                            .height(28.dp)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = rememberRipple()
                            ) {
                                Log.d(
                                    "PROGRESS_CLICKED",
                                    item?.media?.title?.romaji.toString() + " should pull up TextField"
                                )
                            }
                    ) {
                        Text(
                            text = progress,
                            color = Color.White,
                            fontSize = 20.sp
                        )
                        Text(
                            text = "/" + if (item?.media?.chapters.toString() == "null") " - " else item?.media?.chapters.toString(),
                            color = Color.White,
                            fontSize = 20.sp
                        )

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
                            val chapters = item?.media?.chapters ?: Constants.MAXINT
                            if (progress.toInt() + 1 <= chapters) {
                                setProgress((progress.toInt() + 1).toString())
                                when (mediaListStatus) {
                                    Constants.CURRENT -> {
                                        viewModel.currMangaProg[item!!.id] =
                                            viewModel.currMangaProg[item.id]!!.plus(1)
                                    }
                                    Constants.COMPLETED -> {
                                        viewModel.comMangaProg[item!!.id] =
                                            viewModel.comMangaProg[item.id]!!.plus(1)
                                    }
                                    Constants.PLANNING -> {
                                        viewModel.planMangaProg[item!!.id] =
                                            viewModel.planMangaProg[item.id]!!.plus(1)
                                    }
                                    Constants.PAUSED -> {
                                        viewModel.pauseMangaProg[item!!.id] =
                                            viewModel.pauseMangaProg[item.id]!!.plus(1)
                                    }
                                    Constants.DROPPED -> {
                                        viewModel.dropMangaProg[item!!.id] =
                                            viewModel.dropMangaProg[item.id]!!.plus(1)
                                    }
                                }
                            } else
                                setProgress(item?.media?.chapters.toString())
                            Log.d(
                                "+_CLICKED",
                                item?.media?.title?.romaji.toString() + " chapters incremented"
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
                Spacer(modifier = Modifier.width(20.dp))
                Column(
//                modifier = Modifier.background(Color.Red),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = "VOLUMES",
                        fontSize = 10.sp,
                        color = Color.White,
                    )
                    Card(
                        modifier = Modifier
                            .background(Constants.CARDCOLOR)
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
                                text = volProgress,
                                color = Color.White,
                                fontSize = 20.sp
                            )
                            Text(
                                text = "/" + if (item?.media?.volumes.toString() == "null") " - " else item?.media?.volumes.toString(),
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
                            val volumes = item?.media?.volumes ?: Constants.MAXINT
                            if (volProgress.toInt() + 1 <= volumes) {
                                setVolProgress((volProgress.toInt() + 1).toString())
                                when (mediaListStatus) {
                                    Constants.CURRENT -> {
                                        viewModel.currMangaVolProg[item!!.id] =
                                            viewModel.currMangaVolProg[item.id]!!.plus(1)
                                    }
                                    Constants.COMPLETED -> {
                                        viewModel.comMangaVolProg[item!!.id] =
                                            viewModel.comMangaVolProg[item.id]!!.plus(1)
                                    }
                                    Constants.PLANNING -> {
                                        viewModel.planMangaVolProg[item!!.id] =
                                            viewModel.planMangaVolProg[item.id]!!.plus(1)
                                    }
                                    Constants.PAUSED -> {
                                        viewModel.pauseMangaVolProg[item!!.id] =
                                            viewModel.pauseMangaVolProg[item.id]!!.plus(1)
                                    }
                                    Constants.DROPPED -> {
                                        viewModel.dropMangaVolProg[item!!.id] =
                                            viewModel.dropMangaVolProg[item.id]!!.plus(1)
                                    }
                                }
                            } else
                                setVolProgress(item?.media?.volumes.toString())
                            Log.d(
                                "+_CLICKED",
                                item?.media?.title?.romaji.toString() + " volumes incremented"
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
        Box(modifier = Modifier.align(Alignment.BottomCenter)) {
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth(),
                progress = animatedProgress / (item?.media?.chapters?.toFloat()
                    ?: animatedProgress),
                color = Color.Blue
            )
        }
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
//                .background(Color.Red)
                .size(64.dp)
                .padding(top = 18.dp, bottom = 18.dp, start = 18.dp, end = 14.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
//                    .background(Color.Green)
//                    .padding(10.dp)
            ) {
                CircularProgressIndicator(
//                modifier = Modifier.fillMaxWidth().size(15.dp),
                    progress = animatedVolProgress / (item?.media?.volumes?.toFloat()
                        ?: animatedVolProgress),
                    color = Color.Blue,
                    strokeWidth = 6.dp,

                    )
            }
        }
    }
}
