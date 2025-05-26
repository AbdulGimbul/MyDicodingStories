package com.abdl.mydicodingstories.ui.widget

import android.content.Intent
import android.widget.RemoteViewsService
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StoryWidgetService : RemoteViewsService() {

    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        return StoryWidgetItemFactory(this.applicationContext, intent)
    }
}