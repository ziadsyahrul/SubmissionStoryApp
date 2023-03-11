package com.example.submissionstoryapp.utils

sealed class NetworkResource<T>(
    val data: T? = null,
    val message: String? = null
) {
    class SUCCESS<T>(data: T) : NetworkResource<T>(data)
    class ERROR<T>(message: String, data: T? = null) : NetworkResource<T>(data, message)
    class LOADING<T> : NetworkResource<T>()
}