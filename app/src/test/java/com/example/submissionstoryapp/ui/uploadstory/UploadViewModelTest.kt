package com.example.submissionstoryapp.ui.uploadstory

import androidx.paging.ExperimentalPagingApi
import com.example.submissionstoryapp.DummyData
import com.example.submissionstoryapp.data.DataStoryRepository
import com.example.submissionstoryapp.model.UploadStoryResponse
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
class UploadViewModelTest {

    @Mock
    private lateinit var repository: DataStoryRepository
    private lateinit var viewModel: UploadViewModel

    private val token = DummyData.generateToken()
    private val responseUpload = DummyData.generateFileUploadResponse()
    private val desc = DummyData.generateRequestBody()
    private val multipartbody = DummyData.generateMultipartFile()

    @Before
    fun setUp() {
        viewModel = UploadViewModel(repository)
    }

    @Test
    fun `success when upload stories`() = runTest {
        val expectedResponse = flowOf(NetworkResource.SUCCESS(responseUpload))

        `when`(
            viewModel.uploadStory(
                token, desc, "", "", multipartbody
            )
        ).thenReturn(expectedResponse)

        repository.uploadStories(token, desc, "", "", multipartbody).collectLatest {
            when (it) {
                is NetworkResource.SUCCESS -> {
                    assertTrue(true)
                    assertNotNull(it.data)
                    assertSame(responseUpload, it.data)
                }
                is NetworkResource.LOADING -> {
                }
                is NetworkResource.ERROR -> {
                    it.data?.let { it1 -> assertFalse(it1.error) }
                }
            }
        }
        verify(repository).uploadStories(token, desc, "", "", multipartbody)
    }


    @Test
    fun `failed when upload stories`() = runTest {
        val expectedResponse: Flow<NetworkResource<UploadStoryResponse>> =
            flowOf(NetworkResource.ERROR("Error"))

        `when`(
            viewModel.uploadStory(
                token, desc, "", "", multipartbody
            )
        ).thenReturn(expectedResponse)

        repository.uploadStories(token, desc, "", "", multipartbody).collectLatest {
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
        verify(repository).uploadStories(token, desc, "", "", multipartbody)
    }
}