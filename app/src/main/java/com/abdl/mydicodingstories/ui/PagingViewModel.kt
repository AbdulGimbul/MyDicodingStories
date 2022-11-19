package com.abdl.mydicodingstories.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.abdl.mydicodingstories.data.PagingRepository
import com.abdl.mydicodingstories.data.remote.response.ListStoryItem

class PagingViewModel(repository: PagingRepository) : ViewModel() {

    val getStoryWithPage: LiveData<PagingData<ListStoryItem>> =
        repository.getStoryPaging().cachedIn(viewModelScope)
}