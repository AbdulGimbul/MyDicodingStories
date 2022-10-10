package com.abdl.mydicodingstories.data.remote.service

import com.abdl.mydicodingstories.data.remote.response.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @FormUrlEncoded
    @POST("/v1/register")
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): Response<RegisterResponse>

    @FormUrlEncoded
    @POST("/v1/login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): Response<LoginResponse>

    @Multipart
    @POST("/v1/stories")
    suspend fun addStory(
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody
    ): Response<AddStoryResponse>

    @GET("/v1/stories?location=1")
    suspend fun getAllStories(): Response<StoriesResponse>

    @GET("v1/stories")
    suspend fun getStoriesWithPage(
        @Query("page") page: Int,
        @Query("size") size: Int
    ): StoriesResponse
}