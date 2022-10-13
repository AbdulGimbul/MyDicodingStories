package com.abdl.mydicodingstories.ui

import android.content.Context
import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.recyclerview.widget.ListUpdateCallback
import androidx.test.core.app.ApplicationProvider
import com.abdl.mydicodingstories.adapter.ItemStoryAdapter
import com.abdl.mydicodingstories.data.StoryPagingSource
import com.abdl.mydicodingstories.data.remote.response.ListStoryItem
import com.abdl.mydicodingstories.data.remote.service.ApiService
import com.abdl.mydicodingstories.utils.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
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
import java.io.File

@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
class MainViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var sessionManager: SessionManager

    @Mock
    private lateinit var apiService: ApiService

    private lateinit var context: Context

    private lateinit var mainViewModel: MainViewModel

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        apiService = FakeApiService()
        sessionManager = SessionManager(context)
        mainViewModel = MainViewModel(apiService)
    }

    @Test
    fun `when Success GetStories and Should Not Null`() = runTest {
        val expectedResponse = apiService.getAllStories().body()?.listStory
        mainViewModel._listStory.postValue(expectedResponse)
        val actualResponse = mainViewModel.listStory.getOrAwaitValue()
        assertNotNull(actualResponse)
        assertEquals(expectedResponse?.size, actualResponse.size)
    }

    @Test
    fun `when Failed GetStories and Error Should True`() = runTest {
        val expectedResponse = DataDummy.generateDummyStoriesFailed().body()?.error
        mainViewModel._errorMessage.postValue(expectedResponse.toString())
        val actualResponse = mainViewModel.errorMessage.getOrAwaitValue()
        assertNotNull(actualResponse)
        assertEquals(actualResponse, true.toString())
    }

    @Test
    fun `when Success Add Story`() = runTest {
        val path ="https://www.example.com/sample.png"

        val file = File(path)
        val description = "Melakukan post story"

        val requestBody: RequestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
        val body : MultipartBody.Part = MultipartBody.Part.createFormData("photo", file.name, requestBody)
        val expectedResponse = apiService.addStory(body, description.toRequestBody("text/plain".toMediaType())).body()
        mainViewModel._postStoryResponse.postValue(expectedResponse)
        val actualResponse = mainViewModel.postStoryResponse.getOrAwaitValue()
        assertNotNull(actualResponse)
        assertEquals(expectedResponse, actualResponse)
    }

    @Test
    fun `when Add Story Failed because description is empty`() = runTest {
        val expectedResponse = DataDummy.generateDummyPostStoryFailed().body()?.message
        mainViewModel._errorMessage.postValue(expectedResponse.toString())
        val actualResponse = mainViewModel.errorMessage.getOrAwaitValue()
        assertNotNull(actualResponse)
        assertEquals(expectedResponse, actualResponse)
    }

    @Test
    fun `when Success GetStoriesWithPagination`() = runTest {
        val dummyStory = DataDummy.generateStoriesWithPage()
        val data: PagingData<ListStoryItem> = StoryPagingSource.snapshot(dummyStory)
        val expectedStory = MutableLiveData<PagingData<ListStoryItem>>()
        expectedStory.value = data

        val actualStory: PagingData<ListStoryItem> = mainViewModel.getStoryWithPage.getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = ItemStoryAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main
        )
        differ.submitData(actualStory)

        assertNotNull(differ.snapshot())
        assertEquals(dummyStory, differ.snapshot())
        assertEquals(dummyStory.size, differ.snapshot().size)
        assertEquals(dummyStory[0].id, differ.snapshot()[0]?.id)

    }

    class StoryPagingSource : PagingSource<Int, LiveData<List<ListStoryItem>>>() {
        override fun getRefreshKey(state: PagingState<Int, LiveData<List<ListStoryItem>>>): Int {
            return 0
        }

        override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LiveData<List<ListStoryItem>>> {
            return LoadResult.Page(emptyList(), 0, 1)
        }

        companion object {
            fun snapshot(items: List<ListStoryItem>): PagingData<ListStoryItem>{
                return PagingData.from(items)
            }
        }
    }

    private val noopListUpdateCallback = object : ListUpdateCallback {
        override fun onInserted(position: Int, count: Int) {}

        override fun onRemoved(position: Int, count: Int) {}

        override fun onMoved(fromPosition: Int, toPosition: Int) {}

        override fun onChanged(position: Int, count: Int, payload: Any?) {}
    }
}