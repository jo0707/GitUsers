package com.example.gitusers.follow

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
import org.json.JSONArray
import java.net.MalformedURLException

class FollowFragmentViewModel: ViewModel() {
    val followList          = MutableLiveData<ArrayList<SimpleUser>>()
    val apiErrorInfo        = MutableLiveData(ApiErrorEvent.NONE)

    private val apiToken    = BuildConfig.API_KEY

    fun getError(): MutableLiveData<ApiErrorEvent> = apiErrorInfo

    fun getUsers(): MutableLiveData<ArrayList<SimpleUser>> = followList

    fun setUser(context: Context, followUrl: String) {
        val tempList= arrayListOf<SimpleUser>()
        val client  = AsyncHttpClient().apply {
            addHeader("Authorization", "token $apiToken")
            addHeader("User-agent", "joSng")
        }

        client.get(followUrl, object : AsyncHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?) {
                val result  = responseBody?.let { String(it) }

                try {
                    val followArray = JSONArray(result)

                    if (followArray.length() == 0) {
                        apiErrorInfo.postValue(ApiErrorEvent.NOUSERS)
                        return
                    }

                    for (i in 0 until followArray.length()) {
                        val followObject    = followArray.getJSONObject(i)
                        val username        = followObject.getString("login")
                        val userId          = followObject.getInt("id")
                        val imageUrl        = followObject.getString("avatar_url")
                        val userUrl         = followObject.getString("url")

                        tempList.add(
                            SimpleUser(
                                username    = username,
                                userId      = userId,
                                imageUrl    = imageUrl,
                                userUrl     = userUrl,
                                isFavorite  = isFavorite(context, username)
                            )
                        )
                        followList.postValue(tempList)
                    }
                    apiErrorInfo.postValue(ApiErrorEvent.NONE)

                } catch (err: Exception) {
                    apiErrorInfo.postValue(ApiErrorEvent.OTHER)
                    err.printStackTrace()
                }
            }

            override fun onFailure(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?, error: Throwable?) {
                if (error == MalformedURLException())
                    apiErrorInfo.postValue(ApiErrorEvent.NONE)
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
        val cursor      = context.contentResolver
            .query(usernameUri, null, null, null, null, null)
        val isFav       = (cursor as Cursor).count > 0

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