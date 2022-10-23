package com.toharifqi.instageram.storymap

import com.toharifqi.instageram.common.ViewModelFactory
import com.toharifqi.instageram.common.factory
import com.toharifqi.instageram.core.remote.ApiService
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.Dispatchers

@Module
object StoryMapModule {
    @Provides
    fun provideStoryMapRepository(
        apiService: ApiService
    ): StoryMapRepository = StoryMapRepositoryImpl(apiService, Dispatchers.IO)

    @Provides
    fun provideStoryMapViewModelFactory(
        repository: StoryMapRepository
    ): ViewModelFactory<StoryMapViewModel> = factory {
        StoryMapViewModel(repository)
    }
}
