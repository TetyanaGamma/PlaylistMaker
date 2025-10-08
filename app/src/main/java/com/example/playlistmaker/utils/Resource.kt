package com.example.playlistmaker.utils

sealed class Resource<out T> {
    class Loading<out T> : Resource<T>()
    data class Success<out T>(val data: T) : Resource<T>()
    data class Error<out T>(val exception: Throwable? = null) : Resource<T>()
}
