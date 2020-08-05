package com.example.gitusers.utility

import androidx.annotation.StringRes

interface ErrorEvent {
    @StringRes
    fun getErrorResource(): Int
}