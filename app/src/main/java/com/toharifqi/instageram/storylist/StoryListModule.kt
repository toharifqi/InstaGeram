package com.toharifqi.instageram.storylist

import com.toharifqi.instageram.common.ViewModelFactory
import com.toharifqi.instageram.common.factory
import com.toharifqi.instageram.core.SessionManager
import com.toharifqi.instageram.core.remote.ApiService
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.Dispatchers

@Module
object StoryListModule {
    @Provides
    fun provideStoryListRepository(
        apiService: ApiService,
        sessionManager: SessionManager
    ): StoryListRepository = StoryListRepositoryImpl(
        apiService,
        sessionManager,
        Dispatchers.IO
    )

    @Provides
    fun provideStoryListViewModelFactory(
        repository: StoryListRepository
    ): ViewModelFactory<StoryListViewModel> = factory {
        StoryListViewModel(repository)
    }
}
