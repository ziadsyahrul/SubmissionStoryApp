package com.example.submissionstoryapp.utils

import com.example.submissionstoryapp.R
import org.json.JSONObject
import retrofit2.Response

abstract class BaseConfig {
    suspend fun <T> safeApiCall(api: suspend () -> Response<T>): NetworkResource<T> {
        try {
            val response = api()

            if (response.isSuccessful) {
                val body = response.body()
                body?.let {
                    return NetworkResource.SUCCESS(body)
                }
            }
            return error(errorMsg(msg = response.errorBody().toString()))

        } catch (e: java.lang.Exception) {
            return error(e.message.toString())
        }
    }

    private fun errorMsg(msg: String): String {
        val objectt = JSONObject(msg)
        return objectt.getString(R.string.message.toString())
    }

    private fun <T> error(errorMessage: String, data: T? = null): NetworkResource<T> =
        NetworkResource.ERROR(errorMessage, data)
}