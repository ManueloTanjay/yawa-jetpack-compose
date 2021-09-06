package com.example.yawa_anime_list

import GetMediaListQuery
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.exception.ApolloException
import com.apollographql.apollo3.network.http.HttpNetworkTransport
import kotlinx.coroutines.runBlocking
import type.MediaListStatus
import type.MediaType

class ListsScreenViewModel : ViewModel() {

    val liveMedia = MutableLiveData<List<GetMediaListQuery.MediaList?>?>()
    val liveMediaCompletedAnime = MutableLiveData<List<GetMediaListQuery.MediaList?>?>()
    val liveMediaCompletedManga = MutableLiveData<List<GetMediaListQuery.MediaList?>?>()
    private var nextPage = 1
    private var hasNextPage = true

    fun getMediaList(sessionToken: String, userName: String, mediaListStatus: MediaListStatus, mediaType: MediaType) {
        val media = liveMedia.value?.toMutableList() ?: mutableListOf()
        val mediaCompletedAnime = liveMediaCompletedAnime.value?.toMutableList() ?: mutableListOf()
        val mediaCompletedManga = liveMediaCompletedManga.value?.toMutableList() ?: mutableListOf()

        if (!hasNextPage) {
            Log.d("GETMEDIALIST", "Does not have next page")
            return
        }

        runBlocking {
//            media.addAll(getCurrentAnimeList(sessionToken, nextPage, userName)!!.toMutableList())
            Log.d("RUNBLOCKING", media.size.toString())

            var page = getMediaListAPI(sessionToken, nextPage, userName, mediaListStatus, mediaType)
            hasNextPage = page?.pageInfo?.hasNextPage ?: true

            media.addAll(page?.mediaList!!.toMutableList())
            if (mediaType == Constants.ANIME)
                mediaCompletedAnime.addAll(page?.mediaList!!.toMutableList())
            if (mediaType == Constants.MANGA)
                mediaCompletedManga.addAll(page?.mediaList!!.toMutableList())
        }

        liveMedia.value = media
        if(mediaType == Constants.ANIME && mediaListStatus == Constants.COMPLETED)
            liveMediaCompletedAnime.value = mediaCompletedAnime
        if(mediaType == Constants.MANGA && mediaListStatus == Constants.COMPLETED)
            liveMediaCompletedManga.value = mediaCompletedManga

        nextPage++
    }


    private suspend fun getMediaListAPI(
        sessionToken: String,
        page: Int,
        userName: String,
        mediaListStatus: MediaListStatus,
        mediaType: MediaType
    ): GetMediaListQuery.Page? {

        val apolloClient = ApolloClient(
            networkTransport = HttpNetworkTransport(
                serverUrl = "https://graphql.anilist.co/",
                interceptors = listOf(AuthorizationInterceptor(sessionToken))
            )
        )

        val userCurrentAnimeList = try {
            apolloClient.query(
                GetMediaListQuery(
                    page,
                    userName,
                    mediaListStatus,
                    mediaType
                )
            )
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
}