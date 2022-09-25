package com.toharifqi.instageram.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.toharifqi.instageram.core.ResultLoad
import com.toharifqi.instageram.core.remote.LoginResponse
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: AuthenticationRepository) : ViewModel() {
    val loginResult: LiveData<ResultLoad<LoginResponse>>
        get() = mutableLoginResult
    private val mutableLoginResult = MutableLiveData<ResultLoad<LoginResponse>>()

    val token: LiveData<String?>
        get() = mutableToken
    private val mutableToken = MutableLiveData<String?>()

    fun loginUser(email: String, pass: String) {
        viewModelScope.launch {
            repository.loginUser(email, pass).collect {
                mutableLoginResult.value = it
            }
        }
    }

    fun saveUser(name: String, token: String) {
        viewModelScope.launch {
            repository.saveUser(name, token)
        }
    }

    fun checkLoginSession() {
        viewModelScope.launch {
            mutableToken.value = repository.getToken()
        }
    }
}
