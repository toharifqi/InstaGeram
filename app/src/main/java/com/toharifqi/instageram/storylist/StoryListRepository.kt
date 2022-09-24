package com.toharifqi.instageram.storylist

import com.toharifqi.instageram.core.ResultLoad
import com.toharifqi.instageram.core.remote.ApiService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.lang.Exception

interface StoryListRepository {
    fun getAllStories(token: String): Flow<ResultLoad<List<StoryDomainData>>>
}

class StoryListRepositoryImpl(
    private val apiService: ApiService,
    private val dispatcher: CoroutineDispatcher
) : StoryListRepository {
    override fun getAllStories(token: String): Flow<ResultLoad<List<StoryDomainData>>> = flow {
        try {
            val response = apiService.getAllStories(token)
            if (response.stories != null) {
                emit(ResultLoad.Success(
                    response.stories.map {
                        StoryDomainData(it)
                    }
                ))
            } else emit(ResultLoad.Error(response.message))
        } catch (e: Exception) {
            emit(ResultLoad.Error(e.message))
        }
    }.flowOn(dispatcher)
}