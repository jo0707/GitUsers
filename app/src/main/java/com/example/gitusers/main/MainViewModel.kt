package com.example.gitusers.main

import android.content.Context
import android.database.Cursor
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.gitusers.BuildConfig
import com.example.gitusers.database.DatabaseContract.NoteColums.Companion.CONTENT_URI
import com.example.gitusers.utility.ApiErrorEvent
import com.example.gitusers.utility.SimpleUser
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import cz.msebera.android.httpclient.Header
import org.json.JSONObject
import kotlin.math.ceil


class MainViewModel: ViewModel() {
    val userList            = MutableLiveData<ArrayList<SimpleUser>>()
    val apiErrorInfo        = MutableLiveData(ApiErrorEvent.NONE)

    private val apiToken    = BuildConfig.API_KEY
    private val itemCount   = 50
    private val maxPage     = 20
    private var currentPage = 1
    private var pageCount   = 1
    var prevQuery: String   = ""

    fun getPageInfo()   = arrayOf(currentPage, pageCount)

    fun getUsers(): MutableLiveData<ArrayList<SimpleUser>>  = userList

    fun getError(): MutableLiveData<ApiErrorEvent>          = apiErrorInfo

    fun setPageUp(next: Boolean) {
        if (next) currentPage++
        else currentPage--
    }

    fun searchUsers(context: Context, query: String) {
        val tempList    = arrayListOf<SimpleUser>()
        val url         = "https://api.github.com/search/users?q=$query&page=$currentPage&per_page=$itemCount"

        val client      = AsyncHttpClient().apply {
            addHeader("Authorization", "token $apiToken")
            addHeader("User-agent", "joSng")
        }

        if (prevQuery != query) currentPage = 1

        client.get(url, object : AsyncHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray) {
                val result  = String(responseBody)

                try {
                    val responseObject  = JSONObject(result)
                    val totalItemCount  = responseObject.getInt("total_count")
                    val userArray       = responseObject.getJSONArray("items")

                    if (totalItemCount == 0) {
                        apiErrorInfo.postValue(ApiErrorEvent.NOUSERS)
                        return
                    }

                    for (i in 0 until userArray.length()) {
                        val jsonItem    = userArray.getJSONObject(i)
                        val username    = jsonItem.getString("login")
                        val userId      = jsonItem.getInt("id")
                        val imageUrl    = jsonItem.getString("avatar_url")
                        val userUrl     = jsonItem.getString("url")

                        tempList.add(
                            SimpleUser(
                                username    = username,
                                userId      = userId,
                                imageUrl    = imageUrl,
                                userUrl     = userUrl,
                                isFavorite  = isFavorite(context, username)
                            )
                        )
                        userList.postValue(tempList)
                    }
                    apiErrorInfo.postValue(ApiErrorEvent.NONE)

                    pageCount   = if (totalItemCount.toDouble() / itemCount > maxPage) maxPage
                                    else ceil(totalItemCount.toDouble() / itemCount).toInt()
                } catch (err: Exception) {
                    apiErrorInfo.postValue(ApiErrorEvent.OTHER)
                    err.printStackTrace()
                } finally { prevQuery = query }
            }

            override fun onFailure(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?, error: Throwable?) {
                when (statusCode) {
                    0       -> apiErrorInfo.postValue(ApiErrorEvent.NOINET)
                    401     -> apiErrorInfo.postValue(ApiErrorEvent.ERROR401)
                    403     -> apiErrorInfo.postValue(ApiErrorEvent.ERROR403)
                    404     -> apiErrorInfo.postValue(ApiErrorEvent.ERROR404)
                    else    -> apiErrorInfo.postValue(ApiErrorEvent.OTHER)
                }
                error?.printStackTrace()
            }
        })
    }

    fun isFavorite(context: Context, username: String): Boolean {
        val usernameUri = Uri.parse("$CONTENT_URI/$username")
        val cursor  = context.contentResolver
            .query(usernameUri, null, null, null, null, null)

        val isFav   = (cursor as Cursor).count > 0
        cursor.close()

        return isFav
    }

    fun refreshFavorite(context: Context, userList: ArrayList<SimpleUser>): ArrayList<SimpleUser> {
        for (i in userList.indices) {
            val usernameUri = Uri.parse("$CONTENT_URI/${userList[i].username}")
            val cursor      = context.contentResolver
                .query(usernameUri, null, null, null, null, null)

            userList[i].isFavorite   = (cursor as Cursor).count > 0
            cursor.close()
        }

        return userList
    }

}

