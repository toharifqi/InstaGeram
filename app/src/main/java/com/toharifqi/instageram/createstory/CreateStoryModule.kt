package com.toharifqi.instageram.createstory

import com.toharifqi.instageram.common.ViewModelFactory
import com.toharifqi.instageram.common.factory
import com.toharifqi.instageram.core.remote.ApiService
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.Dispatchers

@Module
object CreateStoryModule {
    @Provides
    fun provideCreateStoryRepository(apiService: ApiService): CreateStoryRepository =
        CreateStoryRepositoryImpl(apiService, Dispatchers.IO)

    @Provides
    fun provideCreateStoryViewModelFactory(
        createStoryRepository: CreateStoryRepository
    ) : ViewModelFactory<CreateStoryViewModel> = factory {
        CreateStoryViewModel(createStoryRepository)
    }
}
