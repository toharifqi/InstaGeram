package com.toharifqi.instageram.core

import android.content.Context

interface SessionManager {
    fun createLoginSession(token: String)
    fun saveUser(name: String, email:String, pass: String)
    fun clearSession()
    fun isLogin(): Boolean
    fun getFromPreference(key: String): String
}

class SessionManagerImpl(context: Context) : SessionManager {
    companion object{
        const val PREF_NAME = "user_pref"
        const val KEY_NAME = "name"
        const val KEY_EMAIL = "email"
        const val KEY_PASS = "password"
        const val KEY_TOKEN = "token"
        const val KEY_IS_LOGIN = "isLogin"
    }

    private var pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    private var editor = pref.edit()
    override fun createLoginSession(token: String) {
        with(editor) {
            putBoolean(KEY_IS_LOGIN, true)
            putString(KEY_TOKEN, token)
            commit()
        }
    }

    override fun saveUser(name: String, email: String, pass: String) {
        with(editor) {
            putString(KEY_NAME, name)
            putString(KEY_EMAIL, name)
            putString(KEY_PASS, pass)
        }
    }

    override fun clearSession() {
        with(editor) {
            clear()
            commit()
        }
    }

    override fun isLogin() = pref.getBoolean(KEY_IS_LOGIN, false)

    override fun getFromPreference(key: String) = pref.getString(key, "") ?: ""
}