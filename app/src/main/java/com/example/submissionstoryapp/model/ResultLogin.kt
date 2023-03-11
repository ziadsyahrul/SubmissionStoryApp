package com.example.submissionstoryapp.model

import com.google.gson.annotations.SerializedName

data class ResultLogin(
    @field:SerializedName("userId")
    val userId: String,
    @field:SerializedName("name")
    val name: String,
    @field:SerializedName("token")
    var token: String
)