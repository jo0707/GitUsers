package com.example.gitusers.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.example.gitusers.database.DatabaseContract.NoteColums.Companion.USERNAME
import com.example.gitusers.database.DatabaseContract.NoteColums.Companion.ID
import java.sql.SQLException

class UserHelper(context: Context) {

    companion object {
        const val TABLE_NAME    = DatabaseContract.NoteColums.TABLE_NAME

        private lateinit var databaseHelper: DatabaseHelper
        private lateinit var database: SQLiteDatabase

        private var INSTANCE: UserHelper?   = null

        fun getInstance(context: Context): UserHelper =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: UserHelper(context)
            }
    }

    init {
        databaseHelper  = DatabaseHelper(context)
    }


    fun insert(values: ContentValues): Long     = database.insert(TABLE_NAME, null, values)

    fun deleteByUsername(username: String): Int = database.delete(TABLE_NAME, "$USERNAME = ?", arrayOf(username))

    fun update(username: String, values: ContentValues): Int = database.update(TABLE_NAME, values, "$ID = ?", arrayOf(username))

    fun queryAll(): Cursor = database.query(
            TABLE_NAME,
            null, null, null, null, null,
            "$ID asc"
        )

    fun queryByUsername(username: String): Cursor = database.query(
            TABLE_NAME,
            null,
            "$USERNAME = ?",
            arrayOf(username),
            null, null, null, null
    )


    @Throws(SQLException::class)
    fun openDatabase() {
        database = databaseHelper.writableDatabase
    }
}