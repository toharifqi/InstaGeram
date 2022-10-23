package com.toharifqi.instageram.createstory

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.toharifqi.instageram.core.ResultLoad
import com.toharifqi.instageram.core.remote.AddNewStoryResponse
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody

class CreateStoryViewModel(private val repository: CreateStoryRepository) : ViewModel() {
    val postResult: LiveData<ResultLoad<AddNewStoryResponse>>
        get() = mutablePostResult
    private val mutablePostResult = MutableLiveData<ResultLoad<AddNewStoryResponse>>()

    fun postStory(
        token: String?,
        file: MultipartBody.Part,
        description: RequestBody,
        lat: RequestBody?,
        lng: RequestBody?
    ) {
        viewModelScope.launch {
            token?.let { token ->
                repository.postStory(
                    token,
                    file,
                    description,
                    lat,
                    lng
                ).collect {
                    mutablePostResult.value = it
                }
            }
        }
    }
}
