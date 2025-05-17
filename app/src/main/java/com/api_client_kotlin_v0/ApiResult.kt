package com.api_client_kotlin_v0

sealed class ApiResult<out T> {
    data class Success<out T>(val data: T): ApiResult<T>()
    data class Error(val code: Int? = null, val exception: Throwable? = null): ApiResult<Nothing>()
}