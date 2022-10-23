package com.toharifqi.instageram.storylist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.toharifqi.instageram.core.ResultLoad
import kotlinx.coroutines.launch

class StoryListViewModel(private val repository: StoryListRepository) : ViewModel() {
    val stories: LiveData<ResultLoad<List<StoryDomainData>>>
        get() = mutableStories
    private val mutableStories = MutableLiveData<ResultLoad<List<StoryDomainData>>>()

    val token: LiveData<String?>
        get() = mutableToken
    private val mutableToken = MutableLiveData<String?>()

    fun loadAllStories(token: String?) {
        viewModelScope.launch {
            token?.let { token ->
                repository.getAllStories(token).collect { stories ->
                    mutableStories.value = stories
                }
            }
        }
    }

    fun getToken() {
        mutableToken.value = repository.getToken()
    }

    fun logOut() {
        repository.logOut()
        mutableToken.value = null
    }
}
