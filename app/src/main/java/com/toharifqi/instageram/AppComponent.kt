package com.toharifqi.instageram

import android.content.Context
import com.toharifqi.instageram.login.LoginModule
import com.toharifqi.instageram.core.local.LocalModule
import com.toharifqi.instageram.core.remote.RemoteModule
import com.toharifqi.instageram.createstory.CreateStoryActivity
import com.toharifqi.instageram.createstory.CreateStoryModule
import com.toharifqi.instageram.login.LoginActivity
import com.toharifqi.instageram.register.RegisterActivity
import com.toharifqi.instageram.register.RegisterModule
import com.toharifqi.instageram.storylist.StoryListActivity
import com.toharifqi.instageram.storylist.StoryListModule
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        CreateStoryModule::class,
        LoginModule::class,
        LocalModule::class,
        RegisterModule::class,
        RemoteModule::class,
        StoryListModule::class
    ]
)
interface AppComponent {
    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): AppComponent
    }

    fun inject(loginActivity: LoginActivity)
    fun inject(registerActivity: RegisterActivity)
    fun inject(storyListActivity: StoryListActivity)
    fun inject(createStoryActivity: CreateStoryActivity)
}
