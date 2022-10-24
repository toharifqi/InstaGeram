package com.toharifqi.instageram.storylist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.toharifqi.instageram.core.local.StoryEntity
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class StoryListViewModel(private val repository: StoryListRepository) : ViewModel() {
    val stories: LiveData<PagingData<StoryDomainData>>
        get() = mutableStories
    private val mutableStories = MutableLiveData<PagingData<StoryDomainData>>()

    val token: LiveData<String?>
        get() = mutableToken
    private val mutableToken = MutableLiveData<String?>()

    fun loadAllStories(token: String?) {
        viewModelScope.launch {
            token?.let {
                repository.getAllStories(it).map {
                    convertPagingDataEntityToDomain(it)
                }.cachedIn(viewModelScope).collect {
                    mutableStories.value = it
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

    private fun convertPagingDataEntityToDomain(
        entity: PagingData<StoryEntity>
    ): PagingData<StoryDomainData> = entity.map { StoryDomainData(it) }
}
