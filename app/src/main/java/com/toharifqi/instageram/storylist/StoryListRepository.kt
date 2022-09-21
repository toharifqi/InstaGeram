package com.toharifqi.instageram.storylist

import com.toharifqi.instageram.core.ResultLoad
import com.toharifqi.instageram.core.remote.ApiService
import com.toharifqi.instageram.core.remote.StoryResponse
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.lang.Exception

interface StoryListRepository {
    fun getAllStories(token: String): Flow<ResultLoad<List<StoryResponse>>>
}

class StoryListRepositoryImpl(
    private val apiService: ApiService,
    private val dispatcher: CoroutineDispatcher
) : StoryListRepository {
    override fun getAllStories(token: String): Flow<ResultLoad<List<StoryResponse>>> = flow {
        try {
            val response = apiService.getAllStories(token)
            if (response.stories != null) {
                emit(ResultLoad.Success(response.stories))
            } else emit(ResultLoad.Error(response.message))
        } catch (e: Exception) {
            emit(ResultLoad.Error(e.message))
        }
    }.flowOn(dispatcher)
}