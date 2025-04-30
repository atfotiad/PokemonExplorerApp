package com.atfotiad.pokemonexplorerapp.utils.repository

import retrofit2.Response
import java.io.IOException

/**
 * [RepoUtils] is an object that contains utility functions for handling API responses.
 */
object RepoUtils {
    /**
     * Extension function on a suspend function that performs an API call and converts the result to a [Result].
     * Catches [IOException] for network-related errors.
     */
    suspend fun <T : Any> (suspend () -> Response<T>).toResult(): Result<T> {
        return try {
            val response = invoke()
            response.toResult()
        } catch (e: IOException) {
            Result.NetworkError(e)
        } catch (t: Throwable) {
            Result.Error(Exception(t))
        }
    }

    /**
     * Extension function on [Response] to convert it to a [Result].
     * Handles successful responses, 404 errors, and other unsuccessful responses.
     */
    fun <T : Any> Response<T>.toResult(): Result<T> {
        return if (isSuccessful) {
            val body = body()
            if (body != null) {
                Result.Success(body)
            } else {
                Result.Error(Exception("Response body is null"))
            }
        } else {
            when (code()) {
                404 -> Result.NotFoundError(Exception("Pokemon not found"))
                // Handle other specific error codes if needed
                else -> Result.Error(
                    Exception(
                        "Network request failed with code: ${code()} and message: ${errorBody()?.string()}"
                    )
                )
            }
        }
    }

    /**
     * Extension function on [Result] to get the data if it's a [Result.Success], otherwise returns null.
     * It no longer returns an [Result.Error]. This function is for safely accessing the data.
     */
    fun <T : Any> Result<T>.getDataOrNull(): T? {
        return when (this) {
            is Result.Success -> data
            is Result.Error,
            is Result.NetworkError,
            is Result.NotFoundError,
            is Result.Loading -> null
        }
    }
}

/**
 * Sealed class representing the result of an operation, typically an API call.
 * It can be either a successful [Success] with data, an [Error] with an exception,
 * or a [Loading] state.
 */
sealed class Result<out T : Any> {
    /** Represents a successful operation with data. */
    data class Success<out T : Any>(val data: T?) : Result<T>()

    /** Represents a generic error. */
    data class Error(val exception: Exception) : Result<Nothing>()

    /** Represents a network-related error (e.g., no internet). */
    data class NetworkError(val exception: Exception) : Result<Nothing>()

    /** Represents a "Not Found" error (e.g., HTTP 404). */
    data class NotFoundError(val exception: Exception) : Result<Nothing>()

    /** Represents a loading state. */
    object Loading : Result<Nothing>()
}