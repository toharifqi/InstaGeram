package com.toharifqi.instageram.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.toharifqi.instageram.core.ResultLoad
import com.toharifqi.instageram.core.remote.RegisterResponse
import com.toharifqi.instageram.login.AuthenticationRepository
import kotlinx.coroutines.launch

class RegisterViewModel(private val repository: AuthenticationRepository) : ViewModel() {
    val registerResult: LiveData<ResultLoad<RegisterResponse>>
        get() = mutableRegisterResult
    private val mutableRegisterResult = MutableLiveData<ResultLoad<RegisterResponse>>()

    fun registerUser(name: String, email: String, pass: String) {
        viewModelScope.launch {
            repository.registerUser(name, email, pass).collect {
                mutableRegisterResult.value = it
            }
        }
    }
}
