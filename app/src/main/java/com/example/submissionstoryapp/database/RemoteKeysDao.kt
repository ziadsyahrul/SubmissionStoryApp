package com.example.submissionstoryapp.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.submissionstoryapp.model.RemoteKey

@Dao
interface RemoteKeysDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKeys: List<RemoteKey>)

    @Query("select * from remote_key WHERE id = :id")
    suspend fun getRemoteKeyId(id: String): RemoteKey?

    @Query("delete from remote_key")
    suspend fun deleteRemoteKey()

}