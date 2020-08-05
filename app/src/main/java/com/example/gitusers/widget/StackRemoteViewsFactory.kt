package com.example.gitusers.widget

import android.content.Context
import android.content.Intent
import android.os.Binder
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.bumptech.glide.Glide
import com.example.gitusers.R
import com.example.gitusers.database.DatabaseContract.NoteColums.Companion.CONTENT_URI
import com.example.gitusers.helper.MappingHelper
import com.example.gitusers.utility.SimpleUser
import com.example.gitusers.widget.FavoriteWidget.Companion.EXTRA_USER_URL

internal class StackRemoteViewsFactory(private val mContext: Context) : RemoteViewsService.RemoteViewsFactory {

    private val mWidgetItems = ArrayList<SimpleUser>()

    override fun onCreate() {

    }

    override fun onDataSetChanged() {
        mWidgetItems.clear()

        val token = Binder.clearCallingIdentity()
        val cursor = mContext.contentResolver
            .query(CONTENT_URI, null, null, null, null)

        if (cursor != null)
            mWidgetItems.addAll(MappingHelper.mapCursorToArrayList(cursor))
        cursor?.close()

        Binder.restoreCallingIdentity(token)
    }

    override fun onDestroy() {

    }

    override fun getCount(): Int = mWidgetItems.size

    override fun getViewAt(position: Int): RemoteViews {
        val rv = RemoteViews(mContext.packageName, R.layout.widget_item)

        if (mWidgetItems.isNotEmpty()) {
            val bitmap = Glide.with(mContext)
                .asBitmap()
                .load(mWidgetItems[position].imageUrl)
                .submit(128, 128)
                .get()

            rv.setImageViewBitmap(R.id.widgetUserImage, bitmap)
            rv.setTextViewText(R.id.widgetUsername, mWidgetItems[position].username)

            val fillInIntent = Intent().putExtra(EXTRA_USER_URL, mWidgetItems[position].userUrl)
            rv.setOnClickFillInIntent(R.id.stackWidgetItem, fillInIntent)
        } else
            rv.setEmptyView(R.layout.favorite_widget, R.id.widgetEmptyText)

        return rv
    }

    override fun getLoadingView(): RemoteViews? = null

    override fun getViewTypeCount(): Int = 1

    override fun getItemId(i: Int): Long = 0

    override fun hasStableIds(): Boolean = false
}
