package com.example.submissionstoryapp.database

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface StoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllStory(vararg storyEntity: StoryEntity)

    @Query("select * from story")
    fun getAllStory(): PagingSource<Int, StoryEntity>

    @Query("delete from story")
    fun deleteAllStory()
}