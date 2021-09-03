package com.example.yawa_anime_list

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import type.MediaListStatus
import type.MediaType

object Constants {

    //  MediaListStatus for mediaList queries, paired with username or userID, query authenticated with sessionToken
    val CURRENT: MediaListStatus = MediaListStatus.CURRENT
    val PLANNING: MediaListStatus = MediaListStatus.PLANNING
    val COMPLETED: MediaListStatus = MediaListStatus.COMPLETED
    val DROPPED: MediaListStatus = MediaListStatus.DROPPED
    val PAUSED: MediaListStatus = MediaListStatus.PAUSED
    val REPEATING: MediaListStatus = MediaListStatus.REPEATING

    //  MediaType for specifying if query is for anime or manga
    val ANIME: MediaType = MediaType.ANIME
    val MANGA: MediaType = MediaType.MANGA

    //  constants for pagination
    val PERPAGE = 50

    //  image constants
    val IMAGE_WIDTH = 104.dp
    val IMAGE_HEIGHT = 130.dp

    // bg and card colors
    val CARDCOLOR = Color(0xff353535)
    val BGCOLOR = Color(0xff404040)
}