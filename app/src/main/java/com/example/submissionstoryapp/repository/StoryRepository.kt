package com.example.submissionstoryapp.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.submissionstoryapp.api.ApiService
import com.example.submissionstoryapp.data.StoryRemoteMediator
import com.example.submissionstoryapp.database.StoryDatabase
import com.example.submissionstoryapp.database.StoryEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@OptIn(ExperimentalPagingApi::class)
class StoryRepository @Inject constructor(
    private val api: ApiService,
    private val database: StoryDatabase
) {

    fun getStory(
        token: String
    ): Flow<PagingData<StoryEntity>> = Pager(
        config = PagingConfig(pageSize = 10),
        remoteMediator = StoryRemoteMediator(
            api,
            database,
            generateToken(token)
        ),
        pagingSourceFactory = {
            database.storyDAO().getAllStory()
        }
    ).flow

    private fun generateToken(token: String): String {
        return "Bearer $token"
    }

}