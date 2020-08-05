package com.example.gitconsumer.database

import android.net.Uri
import android.provider.BaseColumns

object DatabaseContract {

    const val AUTHORITY = "com.example.gitusers"
    const val SCHEME = "content"

    internal class NoteColums: BaseColumns {
        companion object {
            private const val TABLE_NAME    = "favorite_user"

            const val USERNAME      = "username"
            const val USER_ID       = "user_id"
            const val IMAGE_URL     = "image_url"

            val CONTENT_URI: Uri = Uri.Builder().scheme(SCHEME)
                .authority(AUTHORITY)
                .appendPath(TABLE_NAME)
                .build()
        }
    }

}