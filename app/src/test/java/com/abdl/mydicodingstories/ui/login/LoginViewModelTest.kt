package com.abdl.mydicodingstories.ui.login

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import com.abdl.mydicodingstories.data.remote.service.ApiService
import com.abdl.mydicodingstories.ui.DataDummy
import com.abdl.mydicodingstories.ui.FakeApiService
import com.abdl.mydicodingstories.ui.getOrAwaitValue
import com.abdl.mydicodingstories.utils.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner

@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
class LoginViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var sessionManager: SessionManager

    @Mock
    private lateinit var apiService: ApiService

    private lateinit var context: Context

    private lateinit var loginViewModel: LoginViewModel

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        apiService = FakeApiService()
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
    fun `when Failed Login Because User Not Found`() {
        val expectedResponse = DataDummy.generateFailedLogin()
        loginViewModel._errorMessage.postValue(expectedResponse)
        val actualResponse = loginViewModel.errorMessage.getOrAwaitValue()
        assertNotNull(actualResponse)
        assertEquals(expectedResponse.length, actualResponse?.length)
    }

    @Test
    fun `when Success Login and Should Not Null`() = runTest {
        val user = "abdl@email.com"
        val passwd = "12345678"
        val expectedResponse = apiService.login(user, passwd).body()
        loginViewModel._loginResponse.postValue(expectedResponse)
        val actualResult = loginViewModel.loginResponse.getOrAwaitValue()
        assertNotNull(actualResult)
        assertEquals(expectedResponse, actualResult)
    }

    @Test
    fun `when Failed Register because email is already taken`() {
        val expectedResponse = DataDummy.generateFailedRegister()
        loginViewModel._errorMessage.postValue(expectedResponse)
        val actualResponse = loginViewModel.errorMessage.getOrAwaitValue()
        assertNotNull(actualResponse)
        assertEquals(expectedResponse.length, actualResponse?.length)
    }

    @Test
    fun `when Success Register and Should Not Null`() = runTest {
        val name = "aa"
        val user = "aa.aa@mail.com"
        val passwd = "123456"
        val expectedResponse = apiService.register(name, user, passwd).body()
        loginViewModel._registerResponse.postValue(expectedResponse)
        val actualResponse = loginViewModel.registerResponse.getOrAwaitValue()
        assertNotNull(actualResponse)
        assertEquals(expectedResponse, actualResponse)
    }
}