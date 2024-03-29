package com.suzei.minote.data

sealed class Result<out R> {
    data class Success<out T>(val data: T? = null) : Result<T>()
    data class Error(val exception: Exception) : Result<Nothing>()
}