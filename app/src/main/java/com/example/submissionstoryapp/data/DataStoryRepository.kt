package com.example.submissionstoryapp.data

import com.example.submissionstoryapp.model.LoginResponse
import com.example.submissionstoryapp.model.RegisterResponse
import com.example.submissionstoryapp.model.ResponseStory
import com.example.submissionstoryapp.model.UploadStoryResponse
import com.example.submissionstoryapp.utils.BaseConfig
import com.example.submissionstoryapp.utils.NetworkResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.MultipartBody
import retrofit2.http.Multipart
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataStoryRepository @Inject constructor(private val data: StoryDataSource) : BaseConfig() {

    suspend fun register(
        username: String,
        email: String,
        pass: String
    ): Flow<NetworkResource<RegisterResponse>> = flow {
        emit(safeApiCall {
            data.register(username, email, pass)
        })
    }.flowOn(Dispatchers.IO)

    suspend fun login(
        email: String,
        pass: String
    ): Flow<NetworkResource<LoginResponse>> = flow {
        emit(safeApiCall {
            data.login(email, pass)
        })
    }.flowOn(Dispatchers.IO)

    suspend fun uploadStories(
        token: String,
        desc: String,
        lat: String?,
        lon: String?,
        file: MultipartBody.Part
    ): Flow<NetworkResource<UploadStoryResponse>> = flow {
        emit(safeApiCall {
            val generateToken = generateAuthToken(token)
            data.uploadStories(generateToken, desc, lat, lon, file)
        })
    }.flowOn(Dispatchers.IO)

    suspend fun getLocation(
        token: String
    ): Flow<NetworkResource<ResponseStory>> = flow {
        emit(safeApiCall {
            val generateToken = generateAuthToken(token)
            data.getLocation(generateToken)
        })
    }

    private fun generateAuthToken(token: String): String {
        return "Bearer $token"
    }

}