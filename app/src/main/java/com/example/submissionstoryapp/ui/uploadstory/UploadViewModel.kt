package com.example.submissionstoryapp.ui.uploadstory

import androidx.lifecycle.ViewModel
import com.example.submissionstoryapp.data.DataStoryRepository
import com.example.submissionstoryapp.repository.StoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import okhttp3.MultipartBody
import retrofit2.http.Multipart
import java.io.File
import javax.inject.Inject

@HiltViewModel
class UploadViewModel @Inject constructor(private val repository: DataStoryRepository) : ViewModel() {
    suspend fun uploadStory(
        token: String,
        desc: String,
        lat: String?,
        lon: String?,
        file: MultipartBody.Part
    ) =
        repository.uploadStories(token, desc, lat, lon, file)

}