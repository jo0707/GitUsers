package com.example.gitusers.detail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.gitusers.BuildConfig
import com.example.gitusers.utility.ApiErrorEvent
import com.example.gitusers.utility.FullUser
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import cz.msebera.android.httpclient.Header
import org.json.JSONObject

class DetailViewModel: ViewModel() {
    val fullUser            = MutableLiveData(FullUser.dummy)
    val apiErrorInfo        = MutableLiveData(ApiErrorEvent.NONE)
    private val apiToken    = BuildConfig.API_KEY

    fun getError(): MutableLiveData<ApiErrorEvent>  = apiErrorInfo

    fun getUser(): MutableLiveData<FullUser>        = fullUser

    fun setUser(userUrl: String) {
        val client  = AsyncHttpClient().apply {
            addHeader("Authorization", "token $apiToken")
            addHeader("User-agent", "joSng")
        }

        client.get(userUrl, object : AsyncHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray) {
                val result  = String(responseBody).replace("null", "-")

                try {
                    val responseObject  = JSONObject(result)
                    with(responseObject) {
                        fullUser.postValue(
                            FullUser(
                                id              = getInt("id"),
                                name            = getString("name"),
                                username        = getString("login"),
                                location        = getString("location"),
                                repository      = getInt("public_repos"),
                                company         = getString("company"),
                                followersUrl    = getString("followers_url"),
                                followingUrl    = "$userUrl/following",
                                followers       = getInt("followers"),
                                following       = getInt("following"),
                                imageUrl        = getString("avatar_url")
                            )
                        )
                    }
                    apiErrorInfo.postValue(ApiErrorEvent.NONE)
                } catch (err: Exception) {
                    apiErrorInfo.postValue(ApiErrorEvent.OTHER)
                    err.printStackTrace()
                }
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

}