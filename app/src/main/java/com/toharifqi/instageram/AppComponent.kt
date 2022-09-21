package com.toharifqi.instageram

import android.content.Context
import com.toharifqi.instageram.authentication.AuthModule
import com.toharifqi.instageram.core.local.LocalModule
import com.toharifqi.instageram.core.remote.RemoteModule
import com.toharifqi.instageram.storylist.StoryListModule
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AuthModule::class,
        LocalModule::class,
        RemoteModule::class,
        StoryListModule::class
    ]
)
interface AppComponent {
    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): AppComponent
    }
}
