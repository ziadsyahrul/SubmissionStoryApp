package com.example.submissionstoryapp.data

import com.example.submissionstoryapp.api.ApiService
import com.example.submissionstoryapp.model.UploadStoryResponse
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
import retrofit2.http.Multipart
import javax.inject.Inject

class StoryDataSource @Inject constructor(private val api: ApiService) {

    suspend fun register(username: String, email: String, password: String) =
        api.registerUser(username, email, password)

    suspend fun login(email: String, password: String) =
        api.loginUser(email, password)

    suspend fun uploadStories(
        token: String,
        desc: String,
        lat: String?,
        lon: String?,
        file: MultipartBody.Part
    ): Response<UploadStoryResponse> {
        val desc = desc.toRequestBody("text/plain".toMediaType())
        val lat = lat?.toRequestBody("text/plain".toMediaType())
        val lon = lon?.toRequestBody("text/plain".toMediaType())
        return api.uploadStory(token, file, desc, lat, lon)
    }

    suspend fun getLocation(token: String) =
        api.getStories(auth = token, location = 1)

}