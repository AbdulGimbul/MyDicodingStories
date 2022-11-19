package com.abdl.mydicodingstories.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.abdl.mydicodingstories.data.PagingRepository
import com.abdl.mydicodingstories.data.remote.response.*
import com.abdl.mydicodingstories.data.remote.service.ApiService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.*
import org.junit.Assert.*
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.robolectric.RobolectricTestRunner
import retrofit2.Response
import java.io.File
import java.util.concurrent.TimeoutException

@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
class MainViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var apiService: ApiService

    @Mock
    private lateinit var repository: PagingRepository

    private lateinit var mainViewModel: MainViewModel

    @Before
    fun setUp() {
        repository = mock(PagingRepository::class.java)
        apiService = mock(ApiService::class.java)
        mainViewModel = MainViewModel(apiService)
    }

    @Test
    fun `when Success GetStories and Should Not Null`() = runTest {
        val listStoryDummy = DataDummy.generateDummyStoriesList()
        val storyResponse = StoriesResponse(listStoryDummy, false, "")

        `when`(apiService.getAllStories()).thenReturn(
            Response.success(storyResponse)
        )

        mainViewModel.fetchStories()

        assertEquals(
            storyResponse.listStory,
            mainViewModel.listStory.getOrAwaitValue()
        )
        assertThrows(TimeoutException::class.java) {
            mainViewModel.errorMessage.getOrAwaitValue()
        }
    }

    @Test
    fun `when Failed GetStories and Error Should True`() = runTest {
        `when`(apiService.getAllStories()).thenReturn(
            Response.error(
                413,
                "{'message': 'list story tidak berhasil ditampilkan'}".toResponseBody()
            )
        )

        mainViewModel.fetchStories()

        assertEquals(
            "Error : list story tidak berhasil ditampilkan",
            mainViewModel.errorMessage.getOrAwaitValue()
        )

        assertThrows(TimeoutException::class.java) {
            mainViewModel.listStory.getOrAwaitValue()
        }
    }

    @Test
    fun `when Success Add Story`() = runTest {
        val addStoryResponse = AddStoryResponse(false, "")

        val path = "https://www.example.com/sample.png"
        val file = File(path)
        val description = "Melakukan post story".toRequestBody("text/plain".toMediaType())

        val requestBody: RequestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
        val body: MultipartBody.Part =
            MultipartBody.Part.createFormData("photo", file.name, requestBody)

        `when`(apiService.addStory(body, description)).thenReturn(
            Response.success(addStoryResponse)
        )

        mainViewModel.postStory(body, description)

        assertEquals(
            addStoryResponse, mainViewModel.postStoryResponse.getOrAwaitValue()
        )
        assertThrows(TimeoutException::class.java) {
            mainViewModel.errorMessage.getOrAwaitValue()
        }
    }

    @Test
    fun `when Add Story Failed because description is empty`() = runTest {
        val path = "https://www.example.com/sample.png"
        val file = File(path)
        val description = "Melakukan post story".toRequestBody("text/plain".toMediaType())

        val requestBody: RequestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
        val body: MultipartBody.Part =
            MultipartBody.Part.createFormData("photo", file.name, requestBody)

        `when`(apiService.addStory(body, description)).thenReturn(
            Response.error(413, "{'message': 'deskripsi harus diisi'}".toResponseBody())
        )
        mainViewModel.postStory(body, description)

        assertEquals(
            "Error : deskripsi harus diisi",
            mainViewModel.errorMessage.getOrAwaitValue()
        )

        assertThrows(TimeoutException::class.java) {
            mainViewModel.postStoryResponse.getOrAwaitValue()
        }
    }

    @Test
    fun onErrorTest(){
        val error = "Error : Token maximum age exceeded"

        mainViewModel.onError(error)

        assertEquals(error, mainViewModel.errorMessage.getOrAwaitValue())
    }

    @Test
    fun getErrorMessageTest(){
        val expectedError = "Token maximum age exceeded"
        val actualError = mainViewModel.getErrorMessage("""
            {
                "error": true,
                "message": "Token maximum age exceeded"
            }
        """.trimIndent())

        assertEquals(expectedError, actualError)
    }
}