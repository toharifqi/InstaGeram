package com.toharifqi.instageram.register

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.toharifqi.instageram.MainDispatcherRule
import com.toharifqi.instageram.common.returns
import com.toharifqi.instageram.common.shouldBe
import com.toharifqi.instageram.common.verify
import com.toharifqi.instageram.core.ResultLoad
import com.toharifqi.instageram.core.remote.RegisterResponse
import com.toharifqi.instageram.getOrAwaitValue
import com.toharifqi.instageram.login.AuthenticationRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class RegisterViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var authenticationRepository: AuthenticationRepository

    private lateinit var viewModel: RegisterViewModel

    @Before
    fun setUp() {
        viewModel = RegisterViewModel(authenticationRepository)
    }

    @After
    fun tearDown() {
        verifyNoMoreInteractions(authenticationRepository)
    }

    @Test
    fun `registerUser, when register is successful, should return Success ResultLoad with correct register result`() {
        val name = "Joni"
        val email = "joni34@googoo.com"
        val password = "passwordrumit123"
        val expectedResult = flowOf(ResultLoad.Success(RegisterResponse(false, "berhasil register!")))

        runTest {
            authenticationRepository.registerUser(name, email, password) returns expectedResult

            viewModel.registerUser(name, email, password)

            viewModel.registerResult.getOrAwaitValue().also {
                (it is ResultLoad.Success) shouldBe true
                it.data?.error shouldBe false
                it.data?.message shouldBe "berhasil register!"
            }
            authenticationRepository.verify().registerUser(name, email, password)
        }
    }

    @Test
    fun `registerUser, when register is unsuccessful, should return Error ResultLoad with correct error message`() {
        val name = "Joni4324"
        val email = "joni34@googoo.com"
        val password = "password1234"
        val expectedResult = flowOf(ResultLoad.Error<RegisterResponse>("email sudah terlanjur terdaftar!"))

        runTest {
            authenticationRepository.registerUser(name, email, password) returns expectedResult

            viewModel.registerUser(name, email, password)

            viewModel.registerResult.getOrAwaitValue().also {
                (it is ResultLoad.Error) shouldBe true
                it.message shouldBe "email sudah terlanjur terdaftar!"
            }
            authenticationRepository.verify().registerUser(name, email, password)
        }
    }
}
