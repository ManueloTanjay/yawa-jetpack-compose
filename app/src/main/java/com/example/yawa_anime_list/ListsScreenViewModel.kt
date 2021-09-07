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
    val liveMediaPlanningAnime =  MutableLiveData<List<GetMediaListQuery.MediaList?>?>()
    val liveMediaPausedAnime = MutableLiveData<List<GetMediaListQuery.MediaList?>?>()
    val liveMediaDroppedAnime = MutableLiveData<List<GetMediaListQuery.MediaList?>?>()
    //manga
    val liveMediaCurrentManga = MutableLiveData<List<GetMediaListQuery.MediaList?>?>()
    val liveMediaCompletedManga = MutableLiveData<List<GetMediaListQuery.MediaList?>?>()
    val liveMediaPlanningManga =  MutableLiveData<List<GetMediaListQuery.MediaList?>?>()
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
    private var completedMangaNextPage = 1
    private var hasCompletedMangaNextPage = true

    fun getMediaList(sessionToken: String, userName: String, mediaListStatus: MediaListStatus, mediaType: MediaType) {
        val media = liveMedia.value?.toMutableList() ?: mutableListOf()
        //anime
        val mediaCurrentAnime = liveMediaCurrentAnime.value?.toMutableList() ?: mutableListOf()
        val mediaCompletedAnime = liveMediaCompletedAnime.value?.toMutableList() ?: mutableListOf()
        val mediaPlanningAnime = liveMediaPlanningAnime.value?.toMutableList() ?: mutableListOf()
        val mediaPausedAnime = liveMediaPausedAnime.value?.toMutableList() ?: mutableListOf()
        val mediaDroppedAnime = liveMediaDroppedAnime.value?.toMutableList() ?: mutableListOf()
        //manga
        val mediaCompletedManga = liveMediaCompletedManga.value?.toMutableList() ?: mutableListOf()

        //anime
        if (mediaType == Constants.ANIME) {
            if (mediaListStatus == Constants.CURRENT) {
                nextPage = currentAnimeNextPage
                if (!hasCurrentAnimeNextPage) {
                    Log.d("CURRENT ANIME", "Last page reached")
                    return
                }
            }
            if (mediaListStatus == Constants.COMPLETED) {
                nextPage = completedAnimeNextPage
                if (!hasCompletedAnimeNextPage) {
                    Log.d("COMPLETED ANIME", "Last page reached")
                    return
                }
            }
            if (mediaListStatus == Constants.PLANNING) {
                nextPage = planningAnimeNextPage
                if (!hasPlanningAnimeNextPage) {
                    Log.d("PLANNING ANIME", "Last page reached")
                    return
                }
            }
            if (mediaListStatus == Constants.PAUSED) {
                nextPage = pausedAnimeNextPage
                if (!hasPausedAnimeNextPage) {
                    Log.d("PAUSED ANIME", "Last page reached")
                    return
                }
            }
            if (mediaListStatus == Constants.DROPPED) {
                nextPage = droppedAnimeNextPage
                if (!hasDroppedAnimeNextPage) {
                    Log.d("DROPPED ANIME", "Last page reached")
                    return
                }
            }
        }
        //manga
        if (mediaType == Constants.MANGA && mediaListStatus == Constants.COMPLETED) {
            nextPage = completedMangaNextPage
            if(!hasCompletedMangaNextPage) {
                Log.d("COMPLETED MANGA", "Last page reached")
                return
            }
        }

        runBlocking {
            Log.d("RUNBLOCKING", media.size.toString())

            var page = getMediaListAPI(sessionToken, nextPage, userName, mediaListStatus, mediaType)
            hasNextPage = page?.pageInfo?.hasNextPage ?: true

            media.addAll(page?.mediaList!!.toMutableList())
            //anime
            if (mediaType == Constants.ANIME) {
                if (mediaListStatus == Constants.CURRENT) {
                    mediaCurrentAnime.addAll(page?.mediaList!!.toMutableList())
                    hasCurrentAnimeNextPage = hasNextPage
                }
                if (mediaListStatus == Constants.COMPLETED) {
                    mediaCompletedAnime.addAll(page?.mediaList!!.toMutableList())
                    hasCompletedAnimeNextPage = hasNextPage
                }
                if (mediaListStatus == Constants.PLANNING) {
                    mediaPlanningAnime.addAll(page?.mediaList!!.toMutableList())
                    hasPlanningAnimeNextPage = hasNextPage
                }
                if (mediaListStatus == Constants.PAUSED) {
                    mediaPausedAnime.addAll(page?.mediaList!!.toMutableList())
                    hasPausedAnimeNextPage = hasNextPage
                }
                if (mediaListStatus == Constants.DROPPED) {
                    mediaDroppedAnime.addAll(page?.mediaList!!.toMutableList())
                    hasDroppedAnimeNextPage = hasNextPage
                }
            }
            //manga
            if (mediaType == Constants.MANGA) {
                mediaCompletedManga.addAll(page?.mediaList!!.toMutableList())
                hasCompletedMangaNextPage = hasNextPage
            }
        }

        liveMedia.value = media
        //anime
        if(mediaType == Constants.ANIME) {
            if(mediaListStatus == Constants.CURRENT) {
                liveMediaCurrentAnime.value = mediaCurrentAnime
                currentAnimeNextPage++
            }
            if(mediaListStatus == Constants.COMPLETED) {
                liveMediaCompletedAnime.value = mediaCompletedAnime
                completedAnimeNextPage++
            }
            if(mediaListStatus == Constants.PLANNING) {
                liveMediaPlanningAnime.value = mediaPlanningAnime
                planningAnimeNextPage++
            }
            if(mediaListStatus == Constants.PAUSED) {
                liveMediaPausedAnime.value = mediaPausedAnime
                pausedAnimeNextPage++
            }
            if(mediaListStatus == Constants.DROPPED) {
                liveMediaDroppedAnime.value = mediaDroppedAnime
                droppedAnimeNextPage++
            }
        }
        //manga
        if(mediaType == Constants.MANGA && mediaListStatus == Constants.COMPLETED) {
            liveMediaCompletedManga.value = mediaCompletedManga
            completedMangaNextPage++
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