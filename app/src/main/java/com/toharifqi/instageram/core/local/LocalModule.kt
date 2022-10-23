package com.toharifqi.instageram.core.local

import android.content.Context
import com.toharifqi.instageram.core.SessionManager
import com.toharifqi.instageram.core.SessionManagerImpl
import com.toharifqi.instageram.core.local.StoryDatabase.Companion
import dagger.Module
import dagger.Provides

@Module
object LocalModule {
    @Provides
    fun provideSessionManager(context: Context): SessionManager = SessionManagerImpl(context)

    @Provides
    fun provideStoryDatabase(context: Context): StoryDatabase = StoryDatabase.getDatabase(context)
}
