package com.toharifqi.instageram.storylist

import com.toharifqi.instageram.core.ResultLoad
import com.toharifqi.instageram.core.SessionManager
import com.toharifqi.instageram.core.remote.ApiService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.lang.Exception

interface StoryListRepository {
    fun getAllStories(token: String): Flow<ResultLoad<List<StoryDomainData>>>
    fun getToken(): String?
    fun logOut()
}

class StoryListRepositoryImpl(
    private val apiService: ApiService,
    private val sessionManager: SessionManager,
    private val dispatcher: CoroutineDispatcher
) : StoryListRepository {
    override fun getAllStories(token: String): Flow<ResultLoad<List<StoryDomainData>>> = flow {
        try {
            val response = apiService.getAllStories(token)
            if (response.error || response.stories == null) {
                emit(ResultLoad.Error(response.message))
            } else emit(ResultLoad.Success(
                response.stories.map { StoryDomainData(it) }
            ))
        } catch (e: Exception) {
            emit(ResultLoad.Error(e.message))
        }
    }.flowOn(dispatcher)

    override fun getToken() = sessionManager.getToken()

    override fun logOut() = sessionManager.clearSession()
}
