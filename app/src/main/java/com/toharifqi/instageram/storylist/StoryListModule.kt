package com.toharifqi.instageram.storylist

import com.toharifqi.instageram.core.remote.ApiService
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.Dispatchers

@Module
object StoryListModule {
    @Provides
    fun provideStoryListRepository(
        apiService: ApiService
    ) = StoryListRepositoryImpl(
        apiService,
        Dispatchers.IO
    )
}
