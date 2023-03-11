package com.example.submissionstoryapp

import androidx.lifecycle.LiveData
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.submissionstoryapp.database.StoryEntity

class PageDataSource : PagingSource<Int, LiveData<List<StoryEntity>>>() {

    companion object {
        fun snapshot(data: List<StoryEntity>): PagingData<StoryEntity> {
            return PagingData.from(data)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, LiveData<List<StoryEntity>>>): Int? {
        return 0
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LiveData<List<StoryEntity>>> {
        return LoadResult.Page(emptyList(), 0, 1)
    }
}