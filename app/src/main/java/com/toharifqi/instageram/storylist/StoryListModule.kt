package com.toharifqi.instageram.storylist

import com.toharifqi.instageram.common.ViewModelFactory
import com.toharifqi.instageram.common.factory
import com.toharifqi.instageram.core.SessionManager
import com.toharifqi.instageram.core.local.StoryDatabase
import com.toharifqi.instageram.core.remote.ApiService
import dagger.Module
import dagger.Provides

@Module
object StoryListModule {
    @Provides
    fun provideStoryListRepository(
        database: StoryDatabase,
        apiService: ApiService,
        sessionManager: SessionManager
    ): StoryListRepository = StoryListRepositoryImpl(
        database,
        apiService,
        sessionManager
    )

    @Provides
    fun provideStoryListViewModelFactory(
        repository: StoryListRepository
    ): ViewModelFactory<StoryListViewModel> = factory {
        StoryListViewModel(repository)
    }
}
