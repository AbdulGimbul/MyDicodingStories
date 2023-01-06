package com.abdl.mydicodingstories.utils

import androidx.recyclerview.widget.DiffUtil
import com.abdl.mydicodingstories.data.remote.response.ListStoryItem

class StoryDiffCallback(
    private val mOldStoryList: List<ListStoryItem>,
    private val mNewStoryList: List<ListStoryItem>,
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return mOldStoryList.size
    }

    override fun getNewListSize(): Int {
        return mNewStoryList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return mOldStoryList[oldItemPosition].id == mNewStoryList[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldEmployee = mOldStoryList[oldItemPosition]
        val newEmployee = mNewStoryList[newItemPosition]
        return oldEmployee.id == newEmployee.id &&
                oldEmployee.name == newEmployee.name &&
                oldEmployee.description == newEmployee.description &&
                oldEmployee.photoUrl == newEmployee.photoUrl &&
                oldEmployee.createdAt == newEmployee.createdAt
    }
}