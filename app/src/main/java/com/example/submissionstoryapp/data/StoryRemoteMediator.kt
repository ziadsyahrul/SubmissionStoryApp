package com.example.submissionstoryapp.data

import android.provider.MediaStore.Audio.Media
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.submissionstoryapp.api.ApiService
import com.example.submissionstoryapp.database.StoryDatabase
import com.example.submissionstoryapp.database.StoryEntity
import com.example.submissionstoryapp.model.RemoteKey
import com.example.submissionstoryapp.utils.wrapEspressoIdlingResource

@OptIn(ExperimentalPagingApi::class)
class StoryRemoteMediator(
    private val api: ApiService,
    private val database: StoryDatabase,
    private val token: String
) : RemoteMediator<Int, StoryEntity>() {

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, StoryEntity>
    ): MediatorResult {

        val paging = when (loadType) {
            LoadType.REFRESH -> {
                val remoteKey = getRemoteKeyClosestToCurrent(state)
                remoteKey?.nextKey?.minus(1) ?: PAGE_INDEX
            }

            LoadType.PREPEND -> {
                val remoteKey = getRemoteKeyForFirst(state)
                val prevKeys = remoteKey?.prevKey ?: return MediatorResult.Success(
                    endOfPaginationReached = remoteKey != null
                )
                prevKeys
            }

            LoadType.APPEND -> {
                val remoteKey = getRemoteKeyForLast(state)
                val nextKey = remoteKey?.nextKey ?: return MediatorResult.Success(
                    endOfPaginationReached = remoteKey != null
                )
                nextKey
            }
        }

        wrapEspressoIdlingResource {
            try {
                val response = api.getStories(token, paging, state.config.pageSize)
                val endOfPagingReached = response.body()!!.listStory.isEmpty()

                database.withTransaction {
                    if (loadType == LoadType.REFRESH) {
                        database.remoteKeyDAO().deleteRemoteKey()
                        database.storyDAO().deleteAllStory()
                    }

                    val prev = if (paging == 1) null else paging - 1
                    val next = if (endOfPagingReached) null else paging + 1
                    val key = response.body()!!.listStory.map {
                        RemoteKey(id = it.id, prevKey = prev, nextKey = next)
                    }
                    database.remoteKeyDAO().insertAll(key)
                    response.body()!!.listStory.forEach {
                        val storyList = response.body()!!.listStory.map {
                            StoryEntity(
                                it.id,
                                it.name,
                                it.description,
                                it.createdAt,
                                it.photoUrl,
                                it.longitude,
                                it.latitude
                            )
                        }
                        database.storyDAO().insertAllStory(storyList)
                    }
                }
                return MediatorResult.Success(endOfPaginationReached = endOfPagingReached)

            } catch (e: java.lang.Exception) {
                return MediatorResult.Error(e)
            }
        }
    }


    companion object {
        private const val PAGE_INDEX = 1
    }

    private suspend fun getRemoteKeyForLast(state: PagingState<Int, StoryEntity>): RemoteKey? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()?.let { data ->
            database.remoteKeyDAO().getRemoteKeyId(data.id)
        }
    }

    private suspend fun getRemoteKeyForFirst(state: PagingState<Int, StoryEntity>): RemoteKey? {
        return state.pages.firstOrNull() {
            it.data.isNotEmpty()
        }?.data?.firstOrNull()?.let {
            database.remoteKeyDAO().getRemoteKeyId(it.id)
        }
    }

    private suspend fun getRemoteKeyClosestToCurrent(state: PagingState<Int, StoryEntity>): RemoteKey? {
        return state.anchorPosition?.let {
            state.closestItemToPosition(it)?.id?.let { id ->
                database.remoteKeyDAO().getRemoteKeyId(id)
            }
        }
    }

}