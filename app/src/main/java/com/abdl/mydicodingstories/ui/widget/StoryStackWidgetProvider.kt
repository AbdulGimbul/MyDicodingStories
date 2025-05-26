package com.abdl.mydicodingstories.ui.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import android.widget.Toast
import androidx.core.net.toUri
import com.abdl.mydicodingstories.R
import com.abdl.mydicodingstories.ui.DetailStoryActivity
import com.abdl.mydicodingstories.ui.MainActivity

class StoryStackWidgetProvider : AppWidgetProvider() {

    companion object {
        const val ACTION_REFRESH = "com.abdl.mydicodingstories.ACTION_REFRESH_WIDGET"
        const val ACTION_ITEM_CLICK = "com.abdl.mydicodingstories.ACTION_ITEM_CLICK"
        const val EXTRA_ITEM_ID = "com.abdl.mydicodingstories.EXTRA_ITEM_ID"

        internal fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            val intent = Intent(context, StoryWidgetService::class.java).apply {
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                data = this.toUri(Intent.URI_INTENT_SCHEME).toUri()
            }

            val views = RemoteViews(context.packageName, R.layout.story_stack_widget).apply {
                setRemoteAdapter(R.id.stack_view_stories, intent)
                setEmptyView(R.id.stack_view_stories, R.id.empty_view_stories)
            }

            val launchAppIntent = Intent(context, MainActivity::class.java)
            val pendingLaunchIntent = PendingIntent.getActivity(
                context, 0, launchAppIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_title, pendingLaunchIntent)

            val itemClickIntent = Intent(context, StoryStackWidgetProvider::class.java).apply {
                action = ACTION_ITEM_CLICK
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                data = "com.abdl.mydicodingstories.ui.widget://widget/id/$appWidgetId".toUri()
            }
            val itemClickPendingIntent = PendingIntent.getBroadcast(
                context, 0, itemClickIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setPendingIntentTemplate(R.id.stack_view_stories, itemClickPendingIntent)


            appWidgetManager.updateAppWidget(appWidgetId, views)
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.stack_view_stories)
        }
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds)
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == ACTION_ITEM_CLICK) {
            val storyId = intent.getStringExtra(EXTRA_ITEM_ID)
            if (storyId != null) {
                Toast.makeText(context, "Clicked story ID: $storyId", Toast.LENGTH_SHORT).show()
                val detailIntent = Intent(context, DetailStoryActivity::class.java).apply {
                    putExtra(DetailStoryActivity.EXTRA_STORY, storyId)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }

                context.startActivity(detailIntent)
            }
        } else if (intent.action == ACTION_REFRESH) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val thisAppWidget = intent.getIntExtra(
                AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID
            )
            if (thisAppWidget != AppWidgetManager.INVALID_APPWIDGET_ID) {
                appWidgetManager.notifyAppWidgetViewDataChanged(
                    thisAppWidget,
                    R.id.stack_view_stories
                )
            }
        }
    }

    override fun onEnabled(context: Context) {
    }

    override fun onDisabled(context: Context) {
    }
}