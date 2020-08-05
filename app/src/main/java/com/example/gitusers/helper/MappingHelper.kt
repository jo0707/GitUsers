package com.example.gitusers.helper

import android.database.Cursor
import com.example.gitusers.database.DatabaseContract.NoteColums.Companion.IMAGE_URL
import com.example.gitusers.database.DatabaseContract.NoteColums.Companion.USERNAME
import com.example.gitusers.database.DatabaseContract.NoteColums.Companion.USER_ID
import com.example.gitusers.database.DatabaseContract.NoteColums.Companion.USER_URL
import com.example.gitusers.utility.SimpleUser

object MappingHelper {

    fun mapCursorToArrayList(cursor: Cursor?): ArrayList<SimpleUser> {
        val userList    = ArrayList<SimpleUser>()

        cursor?.apply {
            while (moveToNext()) {
                val username    = getString(getColumnIndexOrThrow(USERNAME))
                val userId      = getInt(getColumnIndexOrThrow(USER_ID))
                val imageUrl    = getString(getColumnIndexOrThrow(IMAGE_URL))
                val userUrl     = getString(getColumnIndexOrThrow(USER_URL))
                userList.add(SimpleUser(username, userId, imageUrl, userUrl, true))
            }
        }

        return userList
    }

}