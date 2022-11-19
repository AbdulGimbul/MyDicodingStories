package com.abdl.mydicodingstories.data

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.abdl.mydicodingstories.data.remote.response.ListStoryItem
import com.abdl.mydicodingstories.data.remote.service.ApiService

class PagingRepository(private val apiService: ApiService) {

    fun getStoryPaging(): LiveData<PagingData<ListStoryItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            pagingSourceFactory = {
                StoryPagingSource(apiService)
            }
        ).liveData
    }
}