package com.medina.juanantonio.domain.models.network

sealed class NetworkResult<out T> {

    data class Success<T>(
        val data: T,
        val code: Int? = null
    ) : NetworkResult<T>()

    data class Error(
        val message: String,
        val code: Int? = null,
        val throwable: Throwable? = null
    ) : NetworkResult<Nothing>()
}
