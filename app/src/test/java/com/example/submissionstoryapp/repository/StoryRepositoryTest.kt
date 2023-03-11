package com.example.submissionstoryapp.repository

import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.ExperimentalPagingApi
import androidx.recyclerview.widget.ListUpdateCallback
import com.example.submissionstoryapp.DummyData
import com.example.submissionstoryapp.MainCoroutineRule
import com.example.submissionstoryapp.PageDataSource
import com.example.submissionstoryapp.api.ApiService
import com.example.submissionstoryapp.database.StoryDatabase
import com.example.submissionstoryapp.model.ResponseStory
import com.example.submissionstoryapp.ui.adapter.StoriesAdapter
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*

import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalPagingApi
@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class StoryRepositoryTest {

    @get:Rule
    var testRule = MainCoroutineRule()

    @Mock
    private lateinit var database: StoryDatabase

    @Mock
    private lateinit var api: ApiService

    @Mock
    private lateinit var repository: StoryRepository

    @Mock
    private lateinit var repositoryMock: StoryRepository

    private val token = DummyData.generateToken()
    private val storyResponse = DummyData.generateStoryResponse()

    @Before
    fun setUp() {
        repository = StoryRepository(api, database)
    }

    @Test
    fun `get story with pager successfully`() = runBlocking {
        val story = DummyData.generateListStories()
        val pageData = PageDataSource.snapshot(story)

        val result = flowOf(pageData)

        `when`(repositoryMock.getStory(token)).thenReturn(result)

        repositoryMock.getStory(token).collectLatest {
            val diff = AsyncPagingDataDiffer(
                diffCallback = StoriesAdapter.Diffcallback,
                updateCallback = updatePageCallback,
                mainDispatcher = testRule.dispatcher,
                workerDispatcher = testRule.dispatcher
            )
            diff.submitData(it)
            assertNotNull(diff.snapshot())
            assertEquals(
                storyResponse.listStory.size,
                diff.snapshot().size
            )
        }
    }

    private val updatePageCallback = object : ListUpdateCallback {
        override fun onInserted(position: Int, count: Int) {}
        override fun onRemoved(position: Int, count: Int) {}
        override fun onMoved(fromPosition: Int, toPosition: Int) {}
        override fun onChanged(position: Int, count: Int, payload: Any?) {}

    }
}