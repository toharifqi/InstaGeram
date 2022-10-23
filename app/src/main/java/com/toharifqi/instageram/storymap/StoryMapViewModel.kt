package com.toharifqi.instageram.storymap

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.toharifqi.instageram.core.ResultLoad
import com.toharifqi.instageram.storylist.StoryDomainData
import kotlinx.coroutines.launch

class StoryMapViewModel(private val repository: StoryMapRepository) : ViewModel()  {
    val stories: LiveData<ResultLoad<List<StoryDomainData>>>
        get() = mutableStories
    private val mutableStories = MutableLiveData<ResultLoad<List<StoryDomainData>>>()

    fun loadAllStoriesWithLocation(token: String) {
        viewModelScope.launch {
            repository.getAllStoriesWithLocation(token).collect { stories ->
                mutableStories.value = stories
            }
        }
    }
}