package com.abdl.mydicodingstories.data

import androidx.paging.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.abdl.mydicodingstories.data.remote.service.ApiService
import com.abdl.mydicodingstories.ui.FakeApiService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(ExperimentalCoroutinesApi::class)
@ExperimentalPagingApi
@RunWith(AndroidJUnit4::class)
class StoryPagingSourceTest {
    private var mockApi: ApiService = FakeApiService()

    @Test
    fun loadReturnsPageWhenOnSuccessfulLoadOfPageKeyedData() = runTest {
        val pagingSource = StoryPagingSource(
            mockApi
        )

        val expected = PagingSource.LoadResult.Page(
            data = mockApi.getStoriesWithPage(1, 10).listStory,
            prevKey = null,
            nextKey = 2,
        )
        val actual = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 10,
                placeholdersEnabled = false
            )
        )

        assertNotNull(actual)
        assertEquals(expected, actual)
    }
}