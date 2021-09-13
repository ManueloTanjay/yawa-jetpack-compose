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

    //progress lists for anime episodes and manga chapters and volumes
    var currAnimeProg = mutableMapOf<Int, Int>()
    var comAnimeProg = mutableMapOf<Int, Int>()
    var planAnimeProg = mutableMapOf<Int, Int>()
    var pauseAnimeProg = mutableMapOf<Int, Int>()
    var dropAnimeProg = mutableMapOf<Int, Int>()

    var currMangaProg = mutableMapOf<Int, Int>()
    var comMangaProg = mutableMapOf<Int, Int>()
    var planMangaProg = mutableMapOf<Int, Int>()
    var pauseMangaProg = mutableMapOf<Int, Int>()
    var dropMangaProg = mutableMapOf<Int, Int>()

    var currMangaVolProg = mutableMapOf<Int, Int>()
    var comMangaVolProg = mutableMapOf<Int, Int>()
    var planMangaVolProg = mutableMapOf<Int, Int>()
    var pauseMangaVolProg = mutableMapOf<Int, Int>()
    var dropMangaVolProg = mutableMapOf<Int, Int>()



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
        var prog = mutableMapOf<Int, Int>()
        var volProg = mutableMapOf<Int, Int>()

        //anime
        if (mediaType == Constants.ANIME) {
            if (mediaListStatus == Constants.CURRENT) {
                media = liveMediaCurrentAnime.value?.toMutableList() ?: mutableListOf()
                prog = currAnimeProg
                nextPage = currentAnimeNextPage
                if (!hasCurrentAnimeNextPage) {
                    Log.d("CURRENT ANIME", "Last page reached")
                    return
                }
            }
            if (mediaListStatus == Constants.COMPLETED) {
                media = liveMediaCompletedAnime.value?.toMutableList() ?: mutableListOf()
                prog = comAnimeProg
                nextPage = completedAnimeNextPage
                if (!hasCompletedAnimeNextPage) {
                    Log.d("COMPLETED ANIME", "Last page reached")
                    return
                }
            }
            if (mediaListStatus == Constants.PLANNING) {
                media = liveMediaPlanningAnime.value?.toMutableList() ?: mutableListOf()
                prog = planAnimeProg
                nextPage = planningAnimeNextPage
                if (!hasPlanningAnimeNextPage) {
                    Log.d("PLANNING ANIME", "Last page reached")
                    return
                }
            }
            if (mediaListStatus == Constants.PAUSED) {
                media = liveMediaPausedAnime.value?.toMutableList() ?: mutableListOf()
                prog = pauseAnimeProg
                nextPage = pausedAnimeNextPage
                if (!hasPausedAnimeNextPage) {
                    Log.d("PAUSED ANIME", "Last page reached")
                    return
                }
            }
            if (mediaListStatus == Constants.DROPPED) {
                media = liveMediaDroppedAnime.value?.toMutableList() ?: mutableListOf()
                prog = dropAnimeProg
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
                prog = currMangaProg
                volProg = currMangaVolProg
                nextPage = currentMangaNextPage
                if (!hasCurrentMangaNextPage) {
                    Log.d("CURRENT MANGA", "Last page reached")
                    return
                }
            }
            if (mediaListStatus == Constants.COMPLETED) {
                media = liveMediaCompletedManga.value?.toMutableList() ?: mutableListOf()
                prog = comMangaProg
                volProg = comMangaVolProg
                nextPage = completedMangaNextPage
                if (!hasCompletedMangaNextPage) {
                    Log.d("COMPLETED MANGA", "Last page reached")
                    return
                }
            }
            if (mediaListStatus == Constants.PLANNING) {
                media = liveMediaPlanningManga.value?.toMutableList() ?: mutableListOf()
                prog = planMangaProg
                volProg = planMangaVolProg
                nextPage = planningMangaNextPage
                if (!hasPlanningMangaNextPage) {
                    Log.d("PLANNING MANGA", "Last page reached")
                    return
                }
            }
            if (mediaListStatus == Constants.PAUSED) {
                media = liveMediaPausedManga.value?.toMutableList() ?: mutableListOf()
                prog = pauseMangaProg
                volProg = pauseMangaVolProg
                nextPage = pausedMangaNextPage
                if (!hasPausedMangaNextPage) {
                    Log.d("PAUSED MANGA", "Last page reached")
                    return
                }
            }
            if (mediaListStatus == Constants.DROPPED) {
                media = liveMediaDroppedManga.value?.toMutableList() ?: mutableListOf()
                prog = dropMangaProg
                volProg = dropMangaVolProg
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
            media.forEachIndexed { index, mediaList ->
                prog[mediaList?.id!!] = mediaList.progress!!.toInt()
                if(mediaList.progressVolumes != null)
                    volProg[mediaList.id] = mediaList.progressVolumes.toInt()
            }
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
                    currAnimeProg = prog
                    liveMediaCurrentAnime.value = media
                    currentAnimeNextPage++
                }
                Constants.COMPLETED -> {
                    comAnimeProg = prog
                    liveMediaCompletedAnime.value = media
                    completedAnimeNextPage++
                }
                Constants.PLANNING -> {
                    planAnimeProg = prog
                    liveMediaPlanningAnime.value = media
                    planningAnimeNextPage++
                }
                Constants.PAUSED -> {
                    pauseAnimeProg = prog
                    liveMediaPausedAnime.value = media
                    pausedAnimeNextPage++
                }
                Constants.DROPPED -> {
                    dropAnimeProg = prog
                    liveMediaDroppedAnime.value = media
                    droppedAnimeNextPage++
                }
            }
        }
        //manga
        if (mediaType == Constants.MANGA) {
            when (mediaListStatus) {
                Constants.CURRENT -> {
                    currMangaProg = prog
                    currMangaVolProg = volProg
                    liveMediaCurrentManga.value = media
                    currentMangaNextPage++
                }
                Constants.COMPLETED -> {
                    comMangaProg = prog
                    comMangaVolProg = volProg
                    liveMediaCompletedManga.value = media
                    completedMangaNextPage++
                }
                Constants.PLANNING -> {
                    planMangaProg = prog
                    planMangaVolProg = volProg
                    liveMediaPlanningManga.value = media
                    planningMangaNextPage++
                }
                Constants.PAUSED -> {
                    pauseMangaProg = prog
                    pauseMangaVolProg = volProg
                    liveMediaPausedManga.value = media
                    pausedMangaNextPage++
                }
                Constants.DROPPED -> {
                    dropMangaProg = prog
                    dropMangaVolProg = volProg
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