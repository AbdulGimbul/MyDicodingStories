package com.abdl.mydicodingstories.ui

import com.abdl.mydicodingstories.data.remote.response.*
import retrofit2.Response

object DataDummy {
    fun generateLoginResponse(): Response<LoginResponse> {
        return Response.success(
            LoginResponse(
                LoginResult(
                    "user-v5jeoL3liBy2FelL",
                    "abdl",
                    "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiJ1c2VyLXY1amVvTDNsaUJ5MkZlbEwiLCJpYXQiOjE2NjUxNjU0MTJ9.lNbnONMQIgZrdqpRpmI6WXR4Bo-lq7P18x953tYQPl8"
                ),
                false,
                "success"
            )
        )
    }

    fun generateRegisterResponse(): Response<RegisterResponse> {
        return Response.success(
            RegisterResponse(
                error = false,
                message = "User created"
            )
        )
    }

    fun generateFailedLogin(): String {
        return "Error : User not found"
    }

    fun generateFailedRegister(): String {
        return "Email is already taken"
    }

    fun generateDummyStoriesEntity(): Response<StoriesResponse> {
        val storyList = ArrayList<ListStoryItem>()
        for (i in 0..10) {
            val story = ListStoryItem(
                "https://dicoding-web-img.sgp1.cdn.digitaloceanspaces.com/original/commons/feature-1-kurikulum-global-3.png",
                "2022-02-22T22:22:22Z",
                "Name $i",
                "Deskripsi",
                0.0,
                "$i",
                0.0,
            )
            storyList.add(story)
        }
        return Response.success(
            StoriesResponse(
                storyList,
                false,
                "Stories fetched successfully"
            )
        )
    }

    fun generateDummyStoriesList(): List<ListStoryItem> {
        val storyList = ArrayList<ListStoryItem>()
        for (i in 0..10) {
            val story = ListStoryItem(
                "https://dicoding-web-img.sgp1.cdn.digitaloceanspaces.com/original/commons/feature-1-kurikulum-global-3.png",
                "2022-02-22T22:22:22Z",
                "Name $i",
                "Deskripsi",
                0.0,
                "$i",
                0.0,
            )
            storyList.add(story)
        }
        return storyList
    }

    fun generateDummyStoriesFailed(): Response<StoriesResponse> {
        val storyList = ArrayList<ListStoryItem>()
        return Response.success(
            StoriesResponse(
                storyList,
                true,
                "Stories cant fetched"
            )
        )
    }

    fun generateDummyPostStory(): Response<AddStoryResponse> {
        return Response.success(
            AddStoryResponse(
                false,
                "success"
            )
        )
    }

    fun generateDummyPostStoryFailed(): Response<AddStoryResponse> {
        return Response.success(
            AddStoryResponse(
                true,
                "description is not allowed to be empty"
            )
        )
    }

    fun generateStoriesWithPage(): List<ListStoryItem> {
        val items: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0..10) {
            val story = ListStoryItem(
                "https://dicoding-web-img.sgp1.cdn.digitaloceanspaces.com/original/commons/feature-1-kurikulum-global-3.png",
                "2022-02-22T22:22:22Z",
                "Name $i",
                "Deskripsi",
                0.0,
                "$i",
                0.0,
            )
            items.add(story)
        }
        return items
    }

    fun generateDummyStoriesPagination(): StoriesResponse {
        val storyList = ArrayList<ListStoryItem>()
        for (i in 0..10) {
            val story = ListStoryItem(
                "https://dicoding-web-img.sgp1.cdn.digitaloceanspaces.com/original/commons/feature-1-kurikulum-global-3.png",
                "2022-02-22T22:22:22Z",
                "Name $i",
                "Deskripsi",
                0.0,
                "$i",
                0.0,
            )
            storyList.add(story)
        }
        return StoriesResponse(
            storyList,
            false,
            "Stories fetched successfully"

        )
    }
}