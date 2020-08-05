package com.example.gitusers.helper

import android.content.Context
import android.net.Uri
import androidx.core.content.contentValuesOf
import com.example.gitusers.database.DatabaseContract

object SetDatabaseHelper {

    fun setToDatabase(context: Context, username: String, userId: Int, imageUrl: String, userUrl: String) {
        val userValues = contentValuesOf(
            DatabaseContract.NoteColums.USERNAME    to username,
            DatabaseContract.NoteColums.USER_ID     to userId,
            DatabaseContract.NoteColums.IMAGE_URL   to imageUrl,
            DatabaseContract.NoteColums.USER_URL    to userUrl
        )
        context.contentResolver.insert(DatabaseContract.NoteColums.CONTENT_URI, userValues)
    }

    fun deleteFromDatabase(context: Context, username: String) {
        val usernameUri = Uri.parse("${DatabaseContract.NoteColums.CONTENT_URI}/${username}")
        context.contentResolver.delete(usernameUri, null, null)
    }

}