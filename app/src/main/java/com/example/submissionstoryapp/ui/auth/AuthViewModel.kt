package com.example.submissionstoryapp.ui.auth

import androidx.lifecycle.ViewModel
import com.example.submissionstoryapp.data.DataStoryRepository
import com.example.submissionstoryapp.repository.StoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(private val storyRepo: DataStoryRepository): ViewModel() {
    suspend fun registerUser(name: String, email: String, password: String) = storyRepo.register(name, email, password)
    suspend fun loginUser(email: String, password: String) = storyRepo.login(email, password)
}