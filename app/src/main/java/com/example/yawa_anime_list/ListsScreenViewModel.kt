package com.example.yawa_anime_list

import GetCurrentAnimeListQuery
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
    private var nextPage = 1
    private var hasNextPage = true

    fun getMediaList(sessionToken: String, userName: String) {
        val media = liveMedia.value?.toMutableList() ?: mutableListOf()

        if (!hasNextPage) {
            Log.d("GETMEDIALIST", "Does not have next page")
            return
        }

        runBlocking {
//            media.addAll(getCurrentAnimeList(sessionToken, nextPage, userName)!!.toMutableList())
            Log.d("RUNBLOCKING", media.size.toString())

            var page = getMediaList(sessionToken, nextPage, userName)
            hasNextPage = page?.pageInfo?.hasNextPage ?: true
            media.addAll(page?.mediaList!!.toMutableList())
        }

        liveMedia.value = media
        nextPage++
    }


    private suspend fun getMediaList(
        sessionToken: String,
        page: Int,
        userName: String
    ): GetMediaListQuery.Page? {

        val apolloClient = ApolloClient(
            networkTransport = HttpNetworkTransport(
                serverUrl = "https://graphql.anilist.co/",
                interceptors = listOf(AuthorizationInterceptor(sessionToken))
            )
        )

        val userCurrentAnimeList = try {
//            apolloClient.query(GetCurrentAnimeListQuery(page, userName))
            apolloClient.query(
                GetMediaListQuery(
                    page,
                    userName,
                    MediaListStatus.COMPLETED,
                    MediaType.ANIME
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