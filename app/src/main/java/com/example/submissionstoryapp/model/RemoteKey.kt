package com.example.submissionstoryapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "remote_key")
data class RemoteKey(

    @PrimaryKey
    val id: String,

    val prevKey: Int?,

    val nextKey: Int?
)