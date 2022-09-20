package com.toharifqi.instageram.authentication

import com.toharifqi.instageram.core.SessionManager
import com.toharifqi.instageram.core.SessionManagerImpl.Companion.KEY_NAME
import com.toharifqi.instageram.core.SessionManagerImpl.Companion.KEY_PASS

interface AuthenticationRepository {
    fun registerUser(name: String, email: String, pass: String)
    fun isUserValid(name: String, pass: String): Boolean
    fun loginUser()
    fun isUserLogin(): Boolean
}

class AuthenticationRepositoryImpl(private val sessionManager: SessionManager) : AuthenticationRepository{
    override fun registerUser(name: String, email: String, pass: String) {
        sessionManager.saveUser(name, email, pass)
    }

    override fun isUserValid(name: String, pass: String): Boolean {
        val savedName = sessionManager.getFromPreference(KEY_NAME)
        val savedPassword = sessionManager.getFromPreference(KEY_PASS)

        return (savedName == name && savedPassword == pass)
    }

    override fun loginUser() {
        TODO("Not yet implemented")
    }

    override fun isUserLogin() = sessionManager.isLogin()
}