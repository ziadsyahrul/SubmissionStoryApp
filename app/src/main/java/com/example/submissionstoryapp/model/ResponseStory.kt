package com.example.submissionstoryapp.model

import com.google.gson.annotations.SerializedName

data class ResponseStory(

    @field:SerializedName("error")
    val error: Boolean,

    @field:SerializedName("message")
    val message: String,

    @field:SerializedName("listStory")
    val listStory: List<ListStory>,
)