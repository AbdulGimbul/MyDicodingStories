package com.abdl.mydicodingstories.ui

import com.abdl.mydicodingstories.data.remote.response.ListStoryItem

object DataDummy {
    fun generateDummyStoriesEntity(): List<ListStoryItem> {
        val storyList = ArrayList<ListStoryItem>()
        for (i in 0..10){
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
}