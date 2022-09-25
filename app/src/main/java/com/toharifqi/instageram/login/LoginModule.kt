package com.toharifqi.instageram.login

import com.toharifqi.instageram.common.ViewModelFactory
import com.toharifqi.instageram.common.factory
import com.toharifqi.instageram.core.SessionManager
import com.toharifqi.instageram.core.remote.ApiService
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.Dispatchers

@Module
object LoginModule {
    @Provides
    fun provideAuthRepository(
        sessionManager: SessionManager,
        apiService: ApiService
    ): AuthenticationRepository =
        AuthenticationRepositoryImpl(
            sessionManager,
            apiService,
            Dispatchers.IO
        )

    @Provides
    fun provideLoginViewModelFactory(
        authenticationRepository: AuthenticationRepository
    ): ViewModelFactory<LoginViewModel> = factory {
        LoginViewModel(authenticationRepository)
    }
}
