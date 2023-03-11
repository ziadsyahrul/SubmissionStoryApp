package com.example.submissionstoryapp.ui.maps

import androidx.paging.ExperimentalPagingApi
import com.example.submissionstoryapp.DummyData
import com.example.submissionstoryapp.data.DataStoryRepository
import com.example.submissionstoryapp.model.ResponseStory
import com.example.submissionstoryapp.utils.NetworkResource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before

import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@ExperimentalPagingApi
@RunWith(MockitoJUnitRunner::class)
class MapsViewModelTest {

    @Mock
    private lateinit var repository: DataStoryRepository
    private lateinit var viewModel: MapsViewModel

    private val storyResponse = DummyData.generateStoryResponse()
    private val token = DummyData.generateToken()

    @Before
    fun setup() {
        viewModel = MapsViewModel(repository)
    }

    @Test
    fun `get location story successfully - SUCCESS`() = runTest {
        val expectedResponse = flowOf(NetworkResource.SUCCESS(storyResponse))

        `when`(viewModel.getLocation(token)).thenReturn(expectedResponse)

        viewModel.getLocation(token).collectLatest {
            when (it) {
                is NetworkResource.SUCCESS -> {
                    assertTrue(true)
                    assertNotNull(it.data)
                    assertSame(it.data, storyResponse)
                }
                is NetworkResource.LOADING -> {
                }
                is NetworkResource.ERROR -> {
                    assertFalse(it.data!!.error)
                }
            }
        }
        verify(repository).getLocation(token)
    }

    @Test
    fun `get location story failed - Error`() = runTest {
        val expectedResponse: Flow<NetworkResource<ResponseStory>> =
            flowOf(NetworkResource.ERROR("Error"))

        `when`(viewModel.getLocation(token)).thenReturn(expectedResponse)
        viewModel.getLocation(token).collectLatest {
            when (it) {
                is NetworkResource.SUCCESS -> {
                    assertTrue(false)
                    assertFalse(it.data!!.error)
                }
                is NetworkResource.LOADING -> {
                }
                is NetworkResource.ERROR -> {
                    assertNotNull(it.message)
                }

            }
        }
        verify(repository).getLocation(token)
    }
}