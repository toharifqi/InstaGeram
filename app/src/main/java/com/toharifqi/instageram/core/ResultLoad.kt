package com.toharifqi.instageram.core

sealed class ResultLoad<T>(val data: T? = null, val message: String? = null) {
    class Success<T>(data: T) : ResultLoad<T>(data)
    class Error<T>(message: String?, data: T? = null) : ResultLoad<T>(data, message)
}
