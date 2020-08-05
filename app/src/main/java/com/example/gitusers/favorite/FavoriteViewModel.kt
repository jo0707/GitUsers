package com.example.gitusers.favorite

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.gitusers.database.DatabaseContract.NoteColums.Companion.CONTENT_URI
import com.example.gitusers.helper.MappingHelper
import com.example.gitusers.utility.SimpleUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class FavoriteViewModel: ViewModel() {
    private val favList     = MutableLiveData<ArrayList<SimpleUser>>()

    fun getFavoriteList(): MutableLiveData<ArrayList<SimpleUser>>   = favList

    fun searchFavoriteList(context: Context) {
        val tempList    = arrayListOf<SimpleUser>()

        GlobalScope.launch(Dispatchers.Default) {
            val cursor  = context.contentResolver
                .query(CONTENT_URI, null, null, null, null)

            tempList.addAll(MappingHelper.mapCursorToArrayList(cursor))
            favList.postValue(tempList)
        }
    }

}