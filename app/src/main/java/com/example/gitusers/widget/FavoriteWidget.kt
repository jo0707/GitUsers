package com.example.gitusers.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import androidx.core.net.toUri
import com.example.gitusers.R
import com.example.gitusers.detail.DetailActivity


class FavoriteWidget : AppWidgetProvider() {

    companion object {

        private const val OPEN_USER     = "com.example.gitusers.widget.OPEN_USER"
        const val EXTRA_USER_URL        = "com.example.gitusers.widget.EXTRA_USER_URL"

        private fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
            val intent  = Intent(context, StackWidgetService::class.java).apply {
                data    = this.toUri(Intent.URI_INTENT_SCHEME).toUri()
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            }

            val views = RemoteViews(context.packageName, R.layout.favorite_widget).apply {
                setRemoteAdapter(R.id.favoriteStackView, intent)
                setEmptyView(R.id.favoriteStackView, R.id.widgetEmptyText)
            }

            val toastIntent = Intent(context, FavoriteWidget::class.java).apply {
                action      = OPEN_USER
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            }

            intent.data = intent.toUri(Intent.URI_INTENT_SCHEME).toUri()

            val toastPendingIntent = PendingIntent.getBroadcast(context, 0, toastIntent, PendingIntent.FLAG_UPDATE_CURRENT)
            views.setPendingIntentTemplate(R.id.favoriteStackView, toastPendingIntent)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action != null) {
            if (intent.action == OPEN_USER) {
                val userUrl         = intent.getStringExtra(EXTRA_USER_URL)
                val goToDetail      = Intent(context, DetailActivity::class.java)
                goToDetail.flags    = Intent.FLAG_ACTIVITY_NEW_TASK
                goToDetail.putExtra(DetailActivity.USER_URL_KEY, userUrl)
                context.startActivity(goToDetail)
            }
        }
    }
}