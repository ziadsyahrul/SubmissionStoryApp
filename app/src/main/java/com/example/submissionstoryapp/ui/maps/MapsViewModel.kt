package com.example.submissionstoryapp.ui.maps

import androidx.lifecycle.ViewModel
import com.example.submissionstoryapp.data.DataStoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MapsViewModel @Inject constructor(private val repository: DataStoryRepository): ViewModel(){
    suspend fun getLocation(token: String) = repository.getLocation(token)
}