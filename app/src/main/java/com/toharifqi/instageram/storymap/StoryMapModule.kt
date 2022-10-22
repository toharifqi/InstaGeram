package com.toharifqi.instageram.storymap

import com.toharifqi.instageram.common.ViewModelFactory
import com.toharifqi.instageram.common.factory
import com.toharifqi.instageram.storylist.StoryListRepository
import dagger.Module
import dagger.Provides

@Module
object StoryMapModule {
    @Provides
    fun provideStoryMapViewModelFactory(
        repository: StoryListRepository
    ): ViewModelFactory<StoryMapViewModel> = factory {
        StoryMapViewModel(repository)
    }
}
