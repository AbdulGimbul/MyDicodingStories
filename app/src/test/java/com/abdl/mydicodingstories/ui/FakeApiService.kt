package com.abdl.mydicodingstories.ui

import com.abdl.mydicodingstories.data.remote.response.AddStoryResponse
import com.abdl.mydicodingstories.data.remote.response.LoginResponse
import com.abdl.mydicodingstories.data.remote.response.RegisterResponse
import com.abdl.mydicodingstories.data.remote.response.StoriesResponse
import com.abdl.mydicodingstories.data.remote.service.ApiService
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response

class FakeApiService : ApiService {

    private val dummyRegister = DataDummy.generateRegisterResponse()
    override suspend fun register(
        name: String,
        email: String,
        password: String
    ): Response<RegisterResponse> {
        return dummyRegister
    }

    private val dummyLogin = DataDummy.generateLoginResponse()
    override suspend fun login(email: String, password: String): Response<LoginResponse> {
        return dummyLogin
    }

    private val dummyAddStory = DataDummy.generateDummyPostStory()
    override suspend fun addStory(
        file: MultipartBody.Part,
        description: RequestBody
    ): Response<AddStoryResponse> {
        return dummyAddStory
    }

    private val dummyStories = DataDummy.generateDummyStoriesEntity()
    override suspend fun getAllStories(): Response<StoriesResponse> {
        return dummyStories
    }

    private val dummyPagination = DataDummy.generateDummyStoriesPagination()
    override suspend fun getStoriesWithPage(page: Int, size: Int): StoriesResponse {
        return dummyPagination
    }
}