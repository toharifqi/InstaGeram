package com.toharifqi.instageram.register

import com.toharifqi.instageram.common.ViewModelFactory
import com.toharifqi.instageram.common.factory
import com.toharifqi.instageram.login.AuthenticationRepository
import dagger.Module
import dagger.Provides

@Module
object RegisterModule {
    @Provides
    fun provideRegisterViewModelFactory(
        authenticationRepository: AuthenticationRepository
    ): ViewModelFactory<RegisterViewModel> = factory {
        RegisterViewModel(authenticationRepository)
    }
}
