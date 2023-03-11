package com.example.submissionstoryapp.ui.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import androidx.recyclerview.widget.ListUpdateCallback
import com.example.submissionstoryapp.DummyData
import com.example.submissionstoryapp.MainCoroutineRule
import com.example.submissionstoryapp.PageDataSource
import com.example.submissionstoryapp.database.StoryEntity
import com.example.submissionstoryapp.getOrAwaitValue
import com.example.submissionstoryapp.repository.StoryRepository
import com.example.submissionstoryapp.ui.adapter.StoriesAdapter
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*

import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@ExperimentalPagingApi
@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest {

    @get:Rule
    var instantExecutor = InstantTaskExecutorRule()

    @get:Rule
    var testRule = MainCoroutineRule()

    private lateinit var viewModel: MainViewModel

    @Mock
    private lateinit var repository: StoryRepository

    @Before
    fun setup() {
        viewModel = MainViewModel(repository)
    }

    private val token = DummyData.generateToken()

    @Test
    fun `get data story success`() = runTest {
        val story = DummyData.generateListStories()
        val pagingData = PageDataSource.snapshot(story)

        val expected = flowOf(pagingData)

        `when`(repository.getStory(token)).thenReturn(expected)
        val stories = viewModel.getStory(token).getOrAwaitValue()
        val diff = AsyncPagingDataDiffer(
            diffCallback = StoriesAdapter.Diffcallback,
            updateCallback = updatePageCallback,
            mainDispatcher = testRule.dispatcher,
            workerDispatcher = testRule.dispatcher
        )
        diff.submitData(stories)

        advanceUntilIdle()

        assertNotNull(diff.snapshot())
        assertEquals(story.size, diff.snapshot().size)
        assertEquals(story[0].name, diff.snapshot()[0]?.name)
    }

    @Test
    fun `get data story empty`() = runTest {
        val pagingData = PageDataSource.snapshot(listOf())
        val expectedResult = flowOf(pagingData)

        `when`(repository.getStory(token)).thenReturn(expectedResult)
        val stories = viewModel.getStory(token).getOrAwaitValue()
        val diff = AsyncPagingDataDiffer(
            diffCallback = StoriesAdapter.Diffcallback,
            updateCallback = updatePageCallback,
            mainDispatcher = testRule.dispatcher,
            workerDispatcher = testRule.dispatcher
        )

        diff.submitData(stories)
        assertEquals(0, diff.snapshot().size)
    }

    private val updatePageCallback = object : ListUpdateCallback {
        override fun onInserted(position: Int, count: Int) {}

        override fun onRemoved(position: Int, count: Int) {}

        override fun onMoved(fromPosition: Int, toPosition: Int) {}

        override fun onChanged(position: Int, count: Int, payload: Any?) {}

    }
}