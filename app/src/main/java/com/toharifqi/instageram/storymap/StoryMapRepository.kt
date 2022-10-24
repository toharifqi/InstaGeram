package com.toharifqi.instageram.storymap

import com.toharifqi.instageram.core.ResultLoad
import com.toharifqi.instageram.core.ResultLoad.Error
import com.toharifqi.instageram.core.ResultLoad.Success
import com.toharifqi.instageram.core.remote.ApiService
import com.toharifqi.instageram.storylist.StoryDomainData
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

interface StoryMapRepository {
    fun getAllStoriesWithLocation(token: String): Flow<ResultLoad<List<StoryDomainData>>>
}

class StoryMapRepositoryImpl(
    private val apiService: ApiService,
    private val dispatcher: CoroutineDispatcher
) : StoryMapRepository {
    override fun getAllStoriesWithLocation(token: String): Flow<ResultLoad<List<StoryDomainData>>> = flow {
        try {
            val response = apiService.getAllStories(
                token = token,
                location = 1,
                size = 100
            )
            if (response.error || response.stories == null) {
                emit(Error(response.message))
            } else emit(Success(
                response.stories.map { StoryDomainData(it) }
            ))
        } catch (e: Exception) {
            emit(Error(e.message))
        }
    }.flowOn(dispatcher)
}
