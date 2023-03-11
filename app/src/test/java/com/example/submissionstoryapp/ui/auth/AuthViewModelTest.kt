package com.example.submissionstoryapp.ui.auth

import com.example.submissionstoryapp.DummyData
import com.example.submissionstoryapp.MainCoroutineRule
import com.example.submissionstoryapp.data.DataStoryRepository
import com.example.submissionstoryapp.model.LoginResponse
import com.example.submissionstoryapp.model.RegisterResponse
import com.example.submissionstoryapp.utils.NetworkResource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOf
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
@RunWith(MockitoJUnitRunner::class)
class AuthViewModelTest {

    @get:Rule
    var testRule = MainCoroutineRule()

    @Mock
    private lateinit var dataStory: DataStoryRepository
    private lateinit var viewModel: AuthViewModel
    private val dummyDataLogin = DummyData.generateLogin()
    private val dummyDataRegis = DummyData.generateRegister()
    private val name = "Zaky Raihan"
    private val email = "zakyy@gmail.com"
    private val pass = "ppppppppp"

    @Before
    fun setup() {
        viewModel = AuthViewModel(dataStory)
    }

    @Test
    fun `when login is successfull - SUCCESS`(): Unit = runTest {
        val expectedResponse = flowOf(NetworkResource.SUCCESS(dummyDataLogin))
        `when`(viewModel.loginUser(email, pass)).thenReturn(expectedResponse)

        viewModel.loginUser(email, pass).collectLatest {
            when (it) {
                is NetworkResource.SUCCESS -> {
                    assertTrue(true)
                    assertNotNull(it.data)
                    assertSame(it.data, dummyDataLogin)
                }

                is NetworkResource.LOADING -> {
                }

                is NetworkResource.ERROR -> {
                    it.data?.let { it1 -> assertFalse(it1.error) }
                }
            }
        }

        verify(dataStory).login(email, pass)

    }


    @Test
    fun `when register is successfull - SUCCESS`(): Unit = runTest {
        val expectedResponse = flowOf(NetworkResource.SUCCESS(dummyDataRegis))
        `when`(viewModel.registerUser(name, email, pass)).thenReturn(expectedResponse)

        viewModel.registerUser(name, email, pass).collectLatest {
            when (it) {
                is NetworkResource.SUCCESS -> {
                    assertTrue(true)
                    assertNotNull(it.data)
                    assertSame(it.data, dummyDataRegis)
                }
                is NetworkResource.LOADING -> {
                }
                is NetworkResource.ERROR -> {
                    it.data?.let { it1 -> assertFalse(it1.error) }
                }
            }
        }
        verify(dataStory).register(name, email, pass)
    }

    @Test
    fun `when login is failed - ERROR`(): Unit = runTest {
        val expectedResponse: Flow<NetworkResource<LoginResponse>> =
            flowOf(NetworkResource.ERROR("Error"))

        `when`(viewModel.loginUser(email, pass)).thenReturn(expectedResponse)

        viewModel.loginUser(email, pass).collectLatest {
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
        verify(dataStory).login(email, pass)
    }

    @Test
    fun `when register is fail - ERROR`(): Unit = runTest {
        val expectedResponse: Flow<NetworkResource<RegisterResponse>> =
            flowOf(NetworkResource.ERROR("Error"))

        `when`(viewModel.registerUser(name, email, pass)).thenReturn(expectedResponse)

        viewModel.registerUser(name, email, pass).collectLatest {
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
        verify(dataStory).register(name, email, pass)
    }


}