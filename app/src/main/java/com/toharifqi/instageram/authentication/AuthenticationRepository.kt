package com.toharifqi.instageram.authentication

import com.toharifqi.instageram.core.ResultLoad
import com.toharifqi.instageram.core.remote.ApiService
import com.toharifqi.instageram.core.SessionManager
import com.toharifqi.instageram.core.remote.LoginResponse
import com.toharifqi.instageram.core.remote.RegisterResponse
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.lang.Exception

interface AuthenticationRepository {
    fun registerUser(name: String, email: String, pass: String): Flow<ResultLoad<RegisterResponse>>
    fun loginUser(email: String, pass: String): Flow<ResultLoad<LoginResponse>>
    fun saveToken(token: String)
    fun getToken(): String?
    fun logout()
}

class AuthenticationRepositoryImpl(
    private val sessionManager: SessionManager,
    private val apiService: ApiService,
    private val dispatcher: CoroutineDispatcher
) : AuthenticationRepository {
    override fun registerUser(
        name: String,
        email: String,
        pass: String
    ): Flow<ResultLoad<RegisterResponse>> = flow {
        try {
            val response = apiService.register(name, email, pass)
            emit(ResultLoad.Success(response))
        } catch (e: Exception) {
            emit(ResultLoad.Error(e.message.toString()))
        }
    }.flowOn(dispatcher)

    override fun loginUser(email: String, pass: String): Flow<ResultLoad<LoginResponse>> = flow {
        try {
            val response = apiService.login(email, pass)
            emit(ResultLoad.Success(response))
        } catch (e: Exception) {
            emit(ResultLoad.Error(e.message))
        }
    }

    override fun saveToken(token: String) {
        sessionManager.saveToken(token)
    }

    override fun getToken(): String? = sessionManager.getToken()
    override fun logout() = sessionManager.clearSession()
}
