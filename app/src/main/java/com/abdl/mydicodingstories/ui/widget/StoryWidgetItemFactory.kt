package com.abdl.mydicodingstories.ui.widget

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import androidx.core.os.bundleOf
import com.abdl.mydicodingstories.R
import com.abdl.mydicodingstories.data.remote.response.ListStoryItem
import com.abdl.mydicodingstories.data.remote.service.ApiConfig
import com.bumptech.glide.Glide
import kotlinx.coroutines.runBlocking

class StoryWidgetItemFactory(
    private val applicationContext: Context,
    private val intent: Intent
) : RemoteViewsService.RemoteViewsFactory {

    private var storyItems = emptyList<ListStoryItem>()

    private fun fetchStoriesForWidget(): List<ListStoryItem> {
        val apiService = ApiConfig.getApiService(applicationContext)
        return try {
            runBlocking {
                val response = apiService.getAllStories()
                if (response.isSuccessful && response.body() != null) {
                    response.body()!!.listStory.take(10)
                } else {
                    emptyList()
                }
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override fun onCreate() {
    }

    override fun onDataSetChanged() {
        storyItems = fetchStoriesForWidget()
    }

    override fun onDestroy() {
    }

    override fun getCount(): Int = storyItems.size

    override fun getViewAt(position: Int): RemoteViews {
        val rv = RemoteViews(applicationContext.packageName, R.layout.widget_story_item)
        if (position < storyItems.size) {
            val story = storyItems[position]
            rv.setTextViewText(R.id.widget_item_name, story.name ?: "Unnamed Story")

            try {
                val bitmap: Bitmap = Glide.with(applicationContext)
                    .asBitmap()
                    .load(story.photoUrl)
                    .submit(512, 512)
                    .get()
                rv.setImageViewBitmap(R.id.widget_item_image, bitmap)
            } catch (e: Exception) {
                rv.setImageViewResource(R.id.widget_item_image, R.drawable.ic_place_holder)
            }

            val fillInIntent = Intent().apply {
                putExtras(bundleOf(StoryStackWidgetProvider.EXTRA_ITEM_ID to story.id))
            }
            rv.setOnClickFillInIntent(R.id.widget_item_image, fillInIntent)
        }
        return rv
    }

    override fun getLoadingView(): RemoteViews? {
        return RemoteViews(applicationContext.packageName, R.layout.item_loading).apply {
            setTextViewText(R.id.error_msg, "Loading stories...")
        }
    }


    override fun getViewTypeCount(): Int = 1

    override fun getItemId(position: Int): Long = position.toLong()

    override fun hasStableIds(): Boolean = false
}