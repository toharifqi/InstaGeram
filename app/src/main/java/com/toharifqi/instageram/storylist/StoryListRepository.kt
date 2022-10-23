package com.toharifqi.instageram.storylist

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.toharifqi.instageram.core.SessionManager
import com.toharifqi.instageram.core.StoryMediator
import com.toharifqi.instageram.core.local.StoryDatabase
import com.toharifqi.instageram.core.local.StoryEntity
import com.toharifqi.instageram.core.remote.ApiService
import kotlinx.coroutines.flow.Flow

interface StoryListRepository {
    fun getAllStories(token: String): Flow<PagingData<StoryEntity>>
    fun getToken(): String?
    fun logOut()
}

class StoryListRepositoryImpl(
    private val database: StoryDatabase,
    private val apiService: ApiService,
    private val sessionManager: SessionManager
) : StoryListRepository {
    @OptIn(ExperimentalPagingApi::class)
    override fun getAllStories(token: String): Flow<PagingData<StoryEntity>> = Pager(
        config = PagingConfig(
            pageSize = 10,
        ),
        remoteMediator = StoryMediator(
            database,
            apiService,
            token
        ),
        pagingSourceFactory = {
            database.storyDao().getAllStories()
        }
    ).flow

    override fun getToken() = sessionManager.getToken()

    override fun logOut() = sessionManager.clearSession()
}
