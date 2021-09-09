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

    //lists for the different MediaLists based on MediaListStatus and MediaType
    //anime
    val liveMediaCurrentAnime = MutableLiveData<List<GetMediaListQuery.MediaList?>?>()
    val liveMediaCompletedAnime = MutableLiveData<List<GetMediaListQuery.MediaList?>?>()
    val liveMediaPlanningAnime = MutableLiveData<List<GetMediaListQuery.MediaList?>?>()
    val liveMediaPausedAnime = MutableLiveData<List<GetMediaListQuery.MediaList?>?>()
    val liveMediaDroppedAnime = MutableLiveData<List<GetMediaListQuery.MediaList?>?>()

    //manga
    val liveMediaCurrentManga = MutableLiveData<List<GetMediaListQuery.MediaList?>?>()
    val liveMediaCompletedManga = MutableLiveData<List<GetMediaListQuery.MediaList?>?>()
    val liveMediaPlanningManga = MutableLiveData<List<GetMediaListQuery.MediaList?>?>()
    val liveMediaDroppedManga = MutableLiveData<List<GetMediaListQuery.MediaList?>?>()
    val liveMediaPausedManga = MutableLiveData<List<GetMediaListQuery.MediaList?>?>()

    private var nextPage = 1
    private var hasNextPage = true

    //anime
    private var currentAnimeNextPage = 1
    private var hasCurrentAnimeNextPage = true
    private var completedAnimeNextPage = 1
    private var hasCompletedAnimeNextPage = true
    private var planningAnimeNextPage = 1
    private var hasPlanningAnimeNextPage = true
    private var pausedAnimeNextPage = 1
    private var hasPausedAnimeNextPage = true
    private var droppedAnimeNextPage = 1
    private var hasDroppedAnimeNextPage = true

    //manga
    private var currentMangaNextPage = 1
    private var hasCurrentMangaNextPage = true
    private var completedMangaNextPage = 1
    private var hasCompletedMangaNextPage = true
    private var planningMangaNextPage = 1
    private var hasPlanningMangaNextPage = true
    private var pausedMangaNextPage = 1
    private var hasPausedMangaNextPage = true
    private var droppedMangaNextPage = 1
    private var hasDroppedMangaNextPage = true

    fun getMediaList(
        sessionToken: String,
        userName: String,
        mediaListStatus: MediaListStatus,
        mediaType: MediaType
    ) {
        var media = mutableListOf<GetMediaListQuery.MediaList?>()

        //anime
        if (mediaType == Constants.ANIME) {
            if (mediaListStatus == Constants.CURRENT) {
                media = liveMediaCurrentAnime.value?.toMutableList() ?: mutableListOf()
                nextPage = currentAnimeNextPage
                if (!hasCurrentAnimeNextPage) {
                    Log.d("CURRENT ANIME", "Last page reached")
                    return
                }
            }
            if (mediaListStatus == Constants.COMPLETED) {
                media = liveMediaCompletedAnime.value?.toMutableList() ?: mutableListOf()
                nextPage = completedAnimeNextPage
                if (!hasCompletedAnimeNextPage) {
                    Log.d("COMPLETED ANIME", "Last page reached")
                    return
                }
            }
            if (mediaListStatus == Constants.PLANNING) {
                media = liveMediaPlanningAnime.value?.toMutableList() ?: mutableListOf()
                nextPage = planningAnimeNextPage
                if (!hasPlanningAnimeNextPage) {
                    Log.d("PLANNING ANIME", "Last page reached")
                    return
                }
            }
            if (mediaListStatus == Constants.PAUSED) {
                media = liveMediaPausedAnime.value?.toMutableList() ?: mutableListOf()
                nextPage = pausedAnimeNextPage
                if (!hasPausedAnimeNextPage) {
                    Log.d("PAUSED ANIME", "Last page reached")
                    return
                }
            }
            if (mediaListStatus == Constants.DROPPED) {
                media = liveMediaDroppedAnime.value?.toMutableList() ?: mutableListOf()
                nextPage = droppedAnimeNextPage
                if (!hasDroppedAnimeNextPage) {
                    Log.d("DROPPED ANIME", "Last page reached")
                    return
                }
            }
        }
        //manga
        if (mediaType == Constants.MANGA) {
            if (mediaListStatus == Constants.CURRENT) {
                media = liveMediaCurrentManga.value?.toMutableList() ?: mutableListOf()
                nextPage = currentMangaNextPage
                if (!hasCurrentMangaNextPage) {
                    Log.d("CURRENT MANGA", "Last page reached")
                    return
                }
            }
            if (mediaListStatus == Constants.COMPLETED) {
                media = liveMediaCompletedManga.value?.toMutableList() ?: mutableListOf()
                nextPage = completedMangaNextPage
                if (!hasCompletedMangaNextPage) {
                    Log.d("COMPLETED MANGA", "Last page reached")
                    return
                }
            }
            if (mediaListStatus == Constants.PLANNING) {
                media = liveMediaPlanningManga.value?.toMutableList() ?: mutableListOf()
                nextPage = planningMangaNextPage
                if (!hasPlanningMangaNextPage) {
                    Log.d("PLANNING MANGA", "Last page reached")
                    return
                }
            }
            if (mediaListStatus == Constants.PAUSED) {
                media = liveMediaPausedManga.value?.toMutableList() ?: mutableListOf()
                nextPage = pausedMangaNextPage
                if (!hasPausedMangaNextPage) {
                    Log.d("PAUSED MANGA", "Last page reached")
                    return
                }
            }
            if (mediaListStatus == Constants.DROPPED) {
                media = liveMediaDroppedManga.value?.toMutableList() ?: mutableListOf()
                nextPage = droppedMangaNextPage
                if (!hasDroppedMangaNextPage) {
                    Log.d("DROPPED MANGA", "Last page reached")
                    return
                }
            }
        }

        runBlocking {
            Log.d("RUNBLOCKING", media.size.toString())

            val page = getMediaListAPI(sessionToken, nextPage, userName, mediaListStatus, mediaType)
            hasNextPage = page?.pageInfo?.hasNextPage ?: true

            media.addAll(page?.mediaList!!.toMutableList())
            //anime
            if (mediaType == Constants.ANIME) {
                when (mediaListStatus) {
                    Constants.CURRENT -> hasCurrentAnimeNextPage = hasNextPage
                    Constants.COMPLETED -> hasCompletedAnimeNextPage = hasNextPage
                    Constants.PLANNING -> hasPlanningAnimeNextPage = hasNextPage
                    Constants.PAUSED -> hasPausedAnimeNextPage = hasNextPage
                    Constants.DROPPED -> hasDroppedAnimeNextPage = hasNextPage
                }
            }
            //manga
            if (mediaType == Constants.MANGA) {
                when (mediaListStatus) {
                    Constants.CURRENT -> hasCurrentMangaNextPage = hasNextPage
                    Constants.COMPLETED -> hasCompletedMangaNextPage = hasNextPage
                    Constants.PLANNING -> hasPlanningMangaNextPage = hasNextPage
                    Constants.PAUSED -> hasPausedMangaNextPage = hasNextPage
                    Constants.DROPPED -> hasDroppedMangaNextPage = hasNextPage
                }
            }
        }

        liveMedia.value = media
        //anime
        if (mediaType == Constants.ANIME) {
            when (mediaListStatus) {
                Constants.CURRENT -> {
                    liveMediaCurrentAnime.value = media
                    currentAnimeNextPage++
                }
                Constants.COMPLETED -> {
                    liveMediaCompletedAnime.value = media
                    completedAnimeNextPage++
                }
                Constants.PLANNING -> {
                    liveMediaPlanningAnime.value = media
                    planningAnimeNextPage++
                }
                Constants.PAUSED -> {
                    liveMediaPausedAnime.value = media
                    pausedAnimeNextPage++
                }
                Constants.DROPPED -> {
                    liveMediaDroppedAnime.value = media
                    droppedAnimeNextPage++
                }
            }
        }
        //manga
        if (mediaType == Constants.MANGA) {
            when (mediaListStatus) {
                Constants.CURRENT -> {
                    liveMediaCurrentManga.value = media
                    currentMangaNextPage++
                }
                Constants.COMPLETED -> {
                    liveMediaCompletedManga.value = media
                    completedMangaNextPage++
                }
                Constants.PLANNING -> {
                    liveMediaPlanningManga.value = media
                    planningMangaNextPage++
                }
                Constants.PAUSED -> {
                    liveMediaPausedManga.value = media
                    pausedMangaNextPage++
                }
                Constants.DROPPED -> {
                    liveMediaDroppedManga.value = media
                    droppedMangaNextPage++
                }
            }
        }
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