package com.example.gitusers.provider

import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import com.example.gitusers.database.DatabaseContract.AUTHORITY
import com.example.gitusers.database.DatabaseContract.NoteColums.Companion.CONTENT_URI
import com.example.gitusers.database.DatabaseContract.NoteColums.Companion.TABLE_NAME
import com.example.gitusers.database.UserHelper

class UserProvider : ContentProvider() {

    companion object {
        private const val USER          = 1
        private const val USER_USERNAME = 2
        private val sUriMatcher         = UriMatcher(UriMatcher.NO_MATCH)

        private lateinit var noteHelper: UserHelper

        init {
            // content://com.example.gitusers/favorite_user
            sUriMatcher.addURI(AUTHORITY, TABLE_NAME, USER)

            // content://com.example.gitusers/favorite_user/username
            sUriMatcher.addURI(AUTHORITY,
                "$TABLE_NAME/*",
                USER_USERNAME)
        }
    }

    override fun onCreate(): Boolean {
        noteHelper = UserHelper.getInstance(context as Context)
        noteHelper.openDatabase()
        return true
    }

    override fun query(uri: Uri, strings: Array<String>?, s: String?, strings1: Array<String>?, s1: String?): Cursor? {
        return when (sUriMatcher.match(uri)) {
            USER            -> noteHelper.queryAll()
            USER_USERNAME   -> noteHelper.queryByUsername(uri.lastPathSegment.toString())
            else            -> null
        }
    }

    override fun insert(uri: Uri, contentValues: ContentValues?): Uri? {
        val added: Long = when (USER) {
            sUriMatcher.match(uri) -> noteHelper.insert(contentValues as ContentValues)
            else -> 0
        }

        context?.contentResolver?.notifyChange(CONTENT_URI, null)
        return Uri.parse("$CONTENT_URI/$added")
    }


    override fun update(uri: Uri, contentValues: ContentValues?, s: String?, strings: Array<String>?): Int {
        val updated: Int = when (USER_USERNAME) {
            sUriMatcher.match(uri) -> noteHelper.update(uri.lastPathSegment.toString(), contentValues as ContentValues)
            else -> 0
        }

        context?.contentResolver?.notifyChange(CONTENT_URI, null)
        return updated
    }

    override fun delete(uri: Uri, s: String?, strings: Array<String>?): Int {
        val deleted: Int = when (USER_USERNAME) {
            sUriMatcher.match(uri) -> noteHelper.deleteByUsername(uri.lastPathSegment.toString())
            else -> 0
        }

        context?.contentResolver?.notifyChange(CONTENT_URI, null)
        return deleted
    }

    override fun getType(uri: Uri): String? = null
}
