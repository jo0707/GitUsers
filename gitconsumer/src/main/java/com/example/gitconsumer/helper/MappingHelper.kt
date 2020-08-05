package com.example.gitconsumer.helper

import android.database.Cursor
import com.example.gitconsumer.database.DatabaseContract.NoteColums.Companion.IMAGE_URL
import com.example.gitconsumer.database.DatabaseContract.NoteColums.Companion.USERNAME
import com.example.gitconsumer.database.DatabaseContract.NoteColums.Companion.USER_ID
import com.example.gitconsumer.utility.SimpleUser

object MappingHelper {

    fun mapCursorToArrayList(cursor: Cursor?): ArrayList<SimpleUser> {
        val userList    = ArrayList<SimpleUser>()

        cursor?.apply {
            while (moveToNext()) {
                val username    = getString(getColumnIndexOrThrow(USERNAME))
                val userId      = getInt(getColumnIndexOrThrow(USER_ID))
                val imageUrl    = getString(getColumnIndexOrThrow(IMAGE_URL))

                userList.add(SimpleUser(
                        username,
                        userId,
                        imageUrl
                    ))
            }
        }

        return userList
    }

}