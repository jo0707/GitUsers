package com.example.gitusers.utility

import androidx.annotation.StringRes
import com.example.gitusers.R

enum class ApiErrorEvent(@StringRes private val resourceId: Int) : ErrorEvent {
    NONE(0),
    NOINET(R.string.no_internet),
    ERROR401(R.string.error_401),
    ERROR403(R.string.error_403),
    ERROR404(R.string.error_404),
    NOUSERS(R.string.no_users),
    OTHER(R.string.unknown_error);

    override fun getErrorResource() = resourceId
}