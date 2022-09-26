package com.toharifqi.instageram.createstory

import com.toharifqi.instageram.common.ViewModelFactory
import com.toharifqi.instageram.common.factory
import com.toharifqi.instageram.core.SessionManager
import com.toharifqi.instageram.core.remote.ApiService
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.Dispatchers

@Module
object CreateStoryModule {
    @Provides
    fun provideCreateStoryRepository(
        apiService: ApiService,
        sessionManager: SessionManager
    ): CreateStoryRepository = CreateStoryRepositoryImpl(apiService, sessionManager, Dispatchers.IO)

    @Provides
    fun provideCreateStoryViewModelFactory(
        createStoryRepository: CreateStoryRepository
    ) : ViewModelFactory<CreateStoryViewModel> = factory {
        CreateStoryViewModel(createStoryRepository)
    }
}