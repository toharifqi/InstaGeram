package com.toharifqi.instageram.core

import android.content.Context
import kotlinx.coroutines.flow.Flow

interface SessionManager {
    fun getToken(): String?
    fun saveToken(name: String, token: String)
    fun clearSession()
}

class SessionManagerImpl(context: Context) : SessionManager {
    companion object{
        const val PREF_NAME = "instageram_pref"
        const val KEY_TOKEN = "token"
        const val KEY_USER_NAME = "user_name"
    }

    private var pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    private var editor = pref.edit()

    override fun getToken(): String? {
        return pref.getString(KEY_TOKEN, null)
    }

    override fun saveToken(name: String, token: String) {
        with(editor) {
            putString(KEY_TOKEN, token)
            putString(KEY_USER_NAME, name)
            commit()
        }
    }

    override fun clearSession() {
        with(editor) {
            clear()
            commit()
        }
    }
}
