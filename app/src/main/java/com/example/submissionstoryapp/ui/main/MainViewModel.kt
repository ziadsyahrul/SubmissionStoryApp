package com.example.submissionstoryapp.ui.main

import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.submissionstoryapp.database.StoryEntity
import com.example.submissionstoryapp.model.ListStory
import com.example.submissionstoryapp.model.ResponseStory
import com.example.submissionstoryapp.repository.StoryRepository
import com.example.submissionstoryapp.utils.NetworkResource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val repository: StoryRepository) : ViewModel() {

    fun getStory(token: String): LiveData<PagingData<StoryEntity>> =
        repository.getStory(token).cachedIn(viewModelScope).asLiveData()
}