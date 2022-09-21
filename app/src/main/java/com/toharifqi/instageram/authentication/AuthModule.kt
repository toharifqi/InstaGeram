package com.toharifqi.instageram.authentication

import com.toharifqi.instageram.core.SessionManager
import com.toharifqi.instageram.core.remote.ApiService
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.Dispatchers

@Module
object AuthModule {
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
}
