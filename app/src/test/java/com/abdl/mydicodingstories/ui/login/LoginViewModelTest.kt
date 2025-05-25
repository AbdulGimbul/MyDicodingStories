package com.abdl.mydicodingstories.ui.login

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import com.abdl.mydicodingstories.data.remote.response.LoginResponse
import com.abdl.mydicodingstories.data.remote.response.LoginResult
import com.abdl.mydicodingstories.data.remote.response.RegisterResponse
import com.abdl.mydicodingstories.data.remote.service.ApiService
import com.abdl.mydicodingstories.ui.getOrAwaitValue
import com.abdl.mydicodingstories.utils.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import retrofit2.Response
import java.util.concurrent.TimeoutException

@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
class LoginViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var sessionManager: SessionManager

    private lateinit var context: Context

    private lateinit var apiService: ApiService

    private lateinit var loginViewModel: LoginViewModel

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        apiService = mock(ApiService::class.java)
        sessionManager = SessionManager(context)
        loginViewModel = LoginViewModel(sessionManager, apiService)
    }

    private val testDispatcher: TestDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUpDispatcher() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDownDispatcher() {
        Dispatchers.resetMain()
    }

    @Test
    fun `when Failed Login Because User Not Found`() = runTest {
        `when`(apiService.login("invalid@email.com", "invalid")).thenReturn(
            Response.error(413, "{'message': 'username tidak ditemukan'}".toResponseBody())
        )

        loginViewModel.getLogin("invalid@email.com", "invalid")

        assertEquals(
            "Error : username tidak ditemukan",
            loginViewModel.errorMessage.getOrAwaitValue()
        )

        assertThrows(TimeoutException::class.java) {
            loginViewModel.loginResponse.getOrAwaitValue()
        }
    }

    @Test
    fun `when Success Login and Should Not Null`() = runTest {
        val sessionMgr = mock(SessionManager::class.java)
        val apiService = mock(ApiService::class.java)
        val viewModel = LoginViewModel(sessionMgr, apiService)

        val loginResult = LoginResult("user", "user-123", "a-user-token")
        val loginResponse = LoginResponse(loginResult, false, "")

        `when`(apiService.login("valid@email.com", "correct password")).thenReturn(
            Response.success(loginResponse)
        )

        viewModel.getLogin("valid@email.com", "correct password")

        verify(sessionMgr).saveAuthToken(
            "a-user-token",
            "user",
            "user-123"
        )

        assertEquals(
            loginResponse,
            viewModel.loginResponse.getOrAwaitValue()
        )
        assertThrows(TimeoutException::class.java) {
            viewModel.errorMessage.getOrAwaitValue()
        }
    }

    @Test
    fun `when Failed Register because email is already taken`() = runTest {
        `when`(apiService.register("invalid", "invalid@email.com", "1234567")).thenReturn(
            Response.error(413, "{'message': 'email sudah pernah digunakan'}".toResponseBody())
        )

        loginViewModel.getRegister("invalid", "invalid@email.com", "1234567")

        assertEquals(
            "Error : email sudah pernah digunakan",
            loginViewModel.errorMessage.getOrAwaitValue()
        )

        assertThrows(TimeoutException::class.java) {
            loginViewModel.registerResponse.getOrAwaitValue()
        }
    }

    @Test
    fun `when Success Register and Should Not Null`() = runTest {
        val registerResponse = RegisterResponse(false, "")

        `when`(apiService.register("valid", "valid@email.com", "1234567")).thenReturn(
            Response.success(registerResponse)
        )

        loginViewModel.getRegister("valid", "valid@email.com", "1234567")

        assertEquals(
            registerResponse,
            loginViewModel.registerResponse.getOrAwaitValue()
        )
        assertThrows(TimeoutException::class.java) {
            loginViewModel.errorMessage.getOrAwaitValue()
        }
    }

    @Test
    fun onErrorTest() {
        val error = "Error : Email is already taken"

        loginViewModel.onError(error)

        assertEquals(error, loginViewModel.errorMessage.getOrAwaitValue())
    }

    @Test
    fun getErrorMessageTest() {
        val expectedError = "Email is already taken"
        val actualError = loginViewModel.getErrorMessage(
            """
            {
                "error": true,
                "message": "Email is already taken"
            }
        """.trimIndent()
        )

        assertEquals(expectedError, actualError)
    }
}