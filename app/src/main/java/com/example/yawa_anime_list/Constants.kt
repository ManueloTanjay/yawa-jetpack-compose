package com.example.yawa_anime_list

class Constants {

    //  MediaListStatus for mediaList queries, paired with username or userID, query authenticated with sessionToken
    val CURRENT = "CURRENT"
    val PLANNING = "PLANNING"
    val COMPLETED = "COMPLETED"
    val DROPPED = "DROPPED"
    val PAUSED = "PAUSED"
    val REPEATING = "REPEATING"

    //  MediaType for specifying if query is for anime or manga
    val ANIME = "ANIME"
    val MANGA = "MANGA"

    //  constants for pagination
    val PERPAGE = 50
}