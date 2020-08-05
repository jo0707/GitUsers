package com.example.gitusers.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.gitusers.database.DatabaseContract.NoteColums.Companion.IMAGE_URL
import com.example.gitusers.database.DatabaseContract.NoteColums.Companion.TABLE_NAME
import com.example.gitusers.database.DatabaseContract.NoteColums.Companion.USERNAME
import com.example.gitusers.database.DatabaseContract.NoteColums.Companion.USER_ID
import com.example.gitusers.database.DatabaseContract.NoteColums.Companion.USER_URL
import com.example.gitusers.database.DatabaseContract.NoteColums.Companion.ID

class DatabaseHelper(context: Context): SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VER) {

    companion object {
        const val DATABASE_NAME = "favorite_user.db"
        const val DATABASE_VER  = 1

        private const val CREATE_DB_SYNTAX  = "create table $TABLE_NAME " +
                "($ID integer primary key autoincrement, " +
                "$USERNAME text not null, "     +
                "$USER_ID integer not null, "   +
                "$IMAGE_URL text not null, "    +
                "$USER_URL text not null)"

        private const val DELETE_DB_SYNTAX  = "drop table if exists $TABLE_NAME"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(CREATE_DB_SYNTAX)
    }


    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL(DELETE_DB_SYNTAX)
        onCreate(db)
    }


}