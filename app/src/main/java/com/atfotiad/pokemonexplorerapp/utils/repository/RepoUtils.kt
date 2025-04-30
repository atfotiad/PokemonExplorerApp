

package com.atfotiad.pokemonexplorerapp.utils.repository

import com.atfotiad.pokemonexplorerapp.utils.repository.RepoUtils.getOrErrorMessage
import com.atfotiad.pokemonexplorerapp.utils.repository.RepoUtils.toResult
import retrofit2.Response

/**
 *  [RepoUtils] is a object that contains the utils for the repository.
 *  @property toResult is an extension function that converts the response to a result.
 *  @property getOrErrorMessage is an extension function that gets the data or shows an error dialog.
 * */
object RepoUtils {
    suspend fun <T : Any> (suspend () -> Response<T>).toResult(): Result<T> {
        return try {
            val response = invoke()
            response.toResult()
        } catch (t: Throwable) {
            Result.Error(Exception(t))
        }
    }

    fun <T : Any> Response<T>.toResult(): Result<T> {
        return if (isSuccessful) {
            val body = body()
            if (body != null) {
                Result.Success(body)
            } else {
                Result.Error(Exception("Response body is null"))
            }
        } else {
            if (code() == 404) {
                Result.Error(Exception("Pokemon not found"))
            } else {
                Result.Error(
                    Exception(
                        "Network request failed with code: ${code()} and message: ${(errorBody()?.string())}"
                    )
                )
            }
        }
    }

    // Extension function to get the data or return the error message
    fun <T : Any> Result<T>.getOrErrorMessage(): T? {
        return when (this) {
            is Result.Success -> data
            is Result.Error -> null
            is Result.Loading -> null
        }
    }
}

sealed class Result<out T : Any> {
    data class Success<out T : Any>(val data: T?) : Result<T>()
    data class Error(val exception: Exception) : Result<Nothing>()
    object Loading : Result<Nothing>()
}