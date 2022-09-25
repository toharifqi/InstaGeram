package com.toharifqi.instageram.login

import com.toharifqi.instageram.core.ResultLoad
import com.toharifqi.instageram.core.remote.ApiService
import com.toharifqi.instageram.core.SessionManager
import com.toharifqi.instageram.core.remote.LoginResponse
import com.toharifqi.instageram.core.remote.RegisterResponse
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.lang.Exception

interface AuthenticationRepository {
    suspend fun registerUser(name: String, email: String, pass: String): Flow<ResultLoad<RegisterResponse>>
    suspend fun loginUser(email: String, pass: String): Flow<ResultLoad<LoginResponse>>
    fun saveUser(name: String, token: String)
    fun getToken(): String?
    fun logout()
}

class AuthenticationRepositoryImpl(
    private val sessionManager: SessionManager,
    private val apiService: ApiService,
    private val dispatcher: CoroutineDispatcher
) : AuthenticationRepository {
    override suspend fun registerUser(
        name: String,
        email: String,
        pass: String
    ): Flow<ResultLoad<RegisterResponse>> = flow {
        try {
            val response = apiService.register(name, email, pass)
            if (response.error) {
                emit(ResultLoad.Error(response.message))
            } else emit(ResultLoad.Success(response))
        } catch (e: Exception) {
            emit(ResultLoad.Error(e.message.toString()))
        }
    }.flowOn(dispatcher)

    override suspend fun loginUser(email: String, pass: String): Flow<ResultLoad<LoginResponse>> = flow {
        try {
            val response = apiService.login(email, pass)
            if (response.error) {
                emit(ResultLoad.Error(response.message))
            } else emit(ResultLoad.Success(response))
        } catch (e: Exception) {
            emit(ResultLoad.Error(e.message))
        }
    }.flowOn(dispatcher)

    override fun saveUser(name: String, token: String) {
        sessionManager.saveToken(name, token)
    }

    override fun getToken(): String? = sessionManager.getToken()
    override fun logout() = sessionManager.clearSession()
}
