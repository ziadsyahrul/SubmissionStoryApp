package com.example.submissionstoryapp.di

import android.content.Context
import androidx.room.Room
import com.example.submissionstoryapp.database.RemoteKeysDao
import com.example.submissionstoryapp.database.StoryDao
import com.example.submissionstoryapp.database.StoryDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    fun provideStoryDao(storyDatabase: StoryDatabase): StoryDao = storyDatabase.storyDAO()

    @Provides
    fun provideRemoteKey(storyDatabase: StoryDatabase): RemoteKeysDao = storyDatabase.remoteKeyDAO()

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext ctx: Context): StoryDatabase {
        return Room.databaseBuilder(ctx, StoryDatabase::class.java, "db_story").build()
    }
}