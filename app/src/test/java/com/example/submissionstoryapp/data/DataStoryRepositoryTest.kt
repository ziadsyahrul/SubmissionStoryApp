package com.example.submissionstoryapp.data

import com.example.submissionstoryapp.DummyData
import com.example.submissionstoryapp.MainCoroutineRule
import com.example.submissionstoryapp.utils.NetworkResource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.runBlocking
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
import retrofit2.Response


@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class DataStoryRepositoryTest {

    @get:Rule
    var testRule = MainCoroutineRule()

    @Mock
    private lateinit var storyDataSource: StoryDataSource
    private lateinit var dataStoryRepository: DataStoryRepository
    private val token = DummyData.generateToken()
    private val multipartBody = DummyData.generateMultipartFile()
    private val desc = DummyData.generateRequestBody()

    private val name = "zaky"
    private val email = "zakyraihan@gmail.com"
    private val pass = "pppppppp"

    @Before
    fun setUp() {
        dataStoryRepository = DataStoryRepository(storyDataSource)
    }

    @Test
    fun `when register success`() = runTest {
        val expectedResponse = DummyData.generateRegister()

        `when`(storyDataSource.register(name, email, pass)).thenReturn(
            Response.success(
                expectedResponse
            )
        )

        dataStoryRepository.register(name, email, pass).collectLatest {
            when (it) {
                is NetworkResource.SUCCESS -> {
                    assertTrue(true)
                    assertNotNull(it.data)
                    assertEquals(expectedResponse, it.data)
                }
                is NetworkResource.LOADING -> {
                }
                is NetworkResource.ERROR -> {
                    assertFalse(it.data!!.error)
                    assertNull(it)
                }
            }
        }
    }

    @Test
    fun `when register failed`() = runTest {
        `when`(storyDataSource.register(name, email, pass)).then { throw Exception() }

        dataStoryRepository.register(name, email, pass).collectLatest {
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
    }

    @Test
    fun `when login success`() = runTest {
        val expectedResponse = DummyData.generateLogin()

        `when`(storyDataSource.login(email, pass)).thenReturn(Response.success(expectedResponse))
        dataStoryRepository.login(email, pass).collect {
            when (it) {
                is NetworkResource.SUCCESS -> {
                    assertTrue(true)
                    assertNotNull(it.data)
                    assertEquals(expectedResponse, it.data)
                }
                is NetworkResource.LOADING -> {
                }
                is NetworkResource.ERROR -> {
                    assertFalse(it.data!!.error)
                    assertNull(it)
                }
            }
        }
    }

    @Test
    fun `when login failed`() = runTest {
        `when`(storyDataSource.login(email, pass)).then { throw Exception() }
        dataStoryRepository.login(email, pass).collect {
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

    }

    @Test
    fun `when upload stories success`() = runTest {
        val expectedResponse = DummyData.generateFileUploadResponse()

        `when`(
            storyDataSource.uploadStories(
                "Bearer $token",
                desc,
                "",
                "",
                multipartBody
            )
        ).thenReturn(Response.success(expectedResponse))

        dataStoryRepository.uploadStories(token, desc, "", "", multipartBody).collectLatest {
            when (it) {
                is NetworkResource.SUCCESS -> {
                    assertNotNull(it.data)
                    assertTrue(true)
                }
                is NetworkResource.LOADING -> {
                }
                is NetworkResource.ERROR -> {
                }
            }
            verify(storyDataSource).uploadStories("Bearer $token", desc, "", "", multipartBody)
        }

    }


    @Test
    fun `when upload stories failed`() = runTest {
        `when`(
            storyDataSource.uploadStories(
                "Bearer $token",
                desc,
                "",
                "",
                multipartBody
            )
        ).then { throw Exception() }

        dataStoryRepository.uploadStories(token, desc, "", "", multipartBody).collectLatest {
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
        verify(storyDataSource).uploadStories("Bearer $token", desc, "", "", multipartBody)
    }
}