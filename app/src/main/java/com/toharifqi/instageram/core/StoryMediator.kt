package com.toharifqi.instageram.core

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.toharifqi.instageram.core.local.RemoteKeyEntity
import com.toharifqi.instageram.core.local.StoryDatabase
import com.toharifqi.instageram.core.local.StoryEntity
import com.toharifqi.instageram.core.remote.ApiService

@OptIn(ExperimentalPagingApi::class)
class StoryMediator(
    private val database: StoryDatabase,
    private val apiService: ApiService,
    private val token: String
) : RemoteMediator<Int, StoryEntity>() {

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, StoryEntity>
    ): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH ->{
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: INITIAL_PAGE_INDEX
            }
            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                val prevKey = remoteKeys?.prevKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                prevKey
            }
            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                val nextKey = remoteKeys?.nextKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                nextKey
            }
        }

        try {
            val responseData = apiService.getAllStories(
                token = token,
                page = page,
                size = state.config.pageSize
            ).stories
            val isReachedEndOfPagination = responseData.isNullOrEmpty()

            if (responseData != null) {
                database.withTransaction {
                    if (loadType == LoadType.REFRESH) {
                        database.run {
                            remoteKeyDao().deleteAllRemoteKey()
                            storyDao().deleteAllStories()
                        }
                    }
                    val prevKey = if (page == 1) null else page - 1
                    val nextKey = if (isReachedEndOfPagination) null else page + 1
                    val keys = responseData.map {
                        RemoteKeyEntity(id = it.id, prevKey = prevKey, nextKey = nextKey)
                    }
                    database.run {
                        remoteKeyDao().insertAll(keys)
                        storyDao().insertStories(responseData.map { StoryEntity(it) })
                    }
                }
            }
            return MediatorResult.Success(endOfPaginationReached = isReachedEndOfPagination)
        } catch (exception: Exception) {
            return MediatorResult.Error(exception)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, StoryEntity>): RemoteKeyEntity? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()?.let { data ->
            database.remoteKeyDao().getRemoteKeyId(data.id)
        }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, StoryEntity>): RemoteKeyEntity? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()?.let { data ->
            database.remoteKeyDao().getRemoteKeyId(data.id)
        }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, StoryEntity>): RemoteKeyEntity? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { id ->
                database.remoteKeyDao().getRemoteKeyId(id)
            }
        }
    }

    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }
}