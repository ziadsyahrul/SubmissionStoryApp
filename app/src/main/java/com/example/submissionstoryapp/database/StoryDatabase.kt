package com.example.submissionstoryapp.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.submissionstoryapp.model.RemoteKey

@Database(
    entities = [StoryEntity::class, RemoteKey::class],
    version = 1,
    exportSchema = false
)
abstract class StoryDatabase: RoomDatabase() {
    abstract fun storyDAO(): StoryDao
    abstract fun remoteKeyDAO(): RemoteKeysDao
}