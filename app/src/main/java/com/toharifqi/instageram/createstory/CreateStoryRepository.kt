package com.toharifqi.instageram.createstory

import com.toharifqi.instageram.core.ResultLoad
import com.toharifqi.instageram.core.SessionManager
import com.toharifqi.instageram.core.remote.AddNewStoryResponse
import com.toharifqi.instageram.core.remote.ApiService
import kotlinx.coroutines.CoroutineDispatcher
import okhttp3.MultipartBody
import okhttp3.RequestBody
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

interface CreateStoryRepository {
    fun postStory(
        token: String,
        file: MultipartBody.Part,
        description: RequestBody,
        lat: RequestBody?,
        lng: RequestBody?
    ): Flow<ResultLoad<AddNewStoryResponse>>
}

class CreateStoryRepositoryImpl(
    private val apiService: ApiService,
    private val sessionManager: SessionManager,
    private val dispatcher: CoroutineDispatcher
) : CreateStoryRepository {
    override fun postStory(
        token: String,
        file: MultipartBody.Part,
        description: RequestBody,
        lat: RequestBody?,
        lng: RequestBody?
    ): Flow<ResultLoad<AddNewStoryResponse>> = flow {
        try {
            val response = apiService.addNewStory(
                token = token,
                file = file,
                description = description,
                lat = lat,
                lon = lng
            )
            if (response.error) {
                emit(ResultLoad.Error(response.message))
            } else emit(ResultLoad.Success(response))
        } catch (e: Exception) {
            emit(ResultLoad.Error(e.message))
        }
    }.flowOn(dispatcher)
}
