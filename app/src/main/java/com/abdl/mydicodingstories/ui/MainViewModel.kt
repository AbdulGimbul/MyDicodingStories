package com.abdl.mydicodingstories.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.abdl.mydicodingstories.data.StoryPagingSource
import com.abdl.mydicodingstories.data.remote.response.AddStoryResponse
import com.abdl.mydicodingstories.data.remote.response.ListStoryItem
import com.abdl.mydicodingstories.data.remote.service.ApiService
import kotlinx.coroutines.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject

class MainViewModel(private val apiService: ApiService) : ViewModel() {

    var job: Job? = null
    val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        onError("Exception handled: ${throwable.localizedMessage}")
    }

    val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    val _listStory = MutableLiveData<List<ListStoryItem>>()
    val listStory: LiveData<List<ListStoryItem>> = _listStory

    val _postStoryResponse = MutableLiveData<AddStoryResponse>()
    val postStoryResponse: LiveData<AddStoryResponse> = _postStoryResponse

    init {
        fetchStories()
    }

    fun fetchStories() {
        _isLoading.value = true
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val response = apiService.getAllStories()
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    _isLoading.value = false
                    val storyList = response.body()?.listStory
                    _listStory.postValue(storyList)
                } else {
                    _isLoading.value = false
                    onError("Error : ${response.message()}")
                }
            }
        }
    }

    fun postStory(file: MultipartBody.Part, description: RequestBody) {
        _isLoading.value = true
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val response = apiService.addStory(file, description)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    _isLoading.value = false
                    val responseBody = response.body()
                    _postStoryResponse.postValue(responseBody)
                } else {
                    _isLoading.value = false
                    val error = response.errorBody()?.string()
                    onError("Error : ${error?.let { getErrorMessage(it) }}")
                }
            }
        }
    }

    val getStoryWithPage: LiveData<PagingData<ListStoryItem>> =
        Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            pagingSourceFactory = {
                StoryPagingSource(apiService)
            }
        ).liveData.cachedIn(viewModelScope)

    private fun onError(message: String) {
        _errorMessage.value = message
        _isLoading.value = false
    }

    private fun getErrorMessage(raw: String): String {
        val obj = JSONObject(raw)
        return obj.getString("message")
    }
}