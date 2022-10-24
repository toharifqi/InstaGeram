package com.toharifqi.instageram.login

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.toharifqi.instageram.MainDispatcherRule
import com.toharifqi.instageram.common.returns
import com.toharifqi.instageram.common.shouldBe
import com.toharifqi.instageram.common.verify
import com.toharifqi.instageram.core.ResultLoad
import com.toharifqi.instageram.core.remote.LoginResponse
import com.toharifqi.instageram.core.remote.LoginResult
import com.toharifqi.instageram.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
class LoginViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var authenticationRepository: AuthenticationRepository

    private lateinit var viewModel: LoginViewModel

    @Before
    fun setUp() {
        viewModel = LoginViewModel(authenticationRepository)
    }

    @After
    fun tearDown() {
        verifyNoMoreInteractions(authenticationRepository)
    }

    @Test
    fun `loginUser, when login is successful, should return Success ResultLoad with correct login result`() {
        val name = "Joni"
        val email = "joni34@googoo.com"
        val password = "passwordrumit123"
        val userId = "u34213"
        val token = "azFDAS432FDSA423oisdw"
        val expectedLoginResult = LoginResult(userId, name, token)
        val expectedResult = flowOf(ResultLoad.Success(LoginResponse(false, "berhasil login!", expectedLoginResult)))

        runTest {
            authenticationRepository.loginUser(email, password) returns expectedResult

            viewModel.loginUser( email, password)

            viewModel.loginResult.getOrAwaitValue().also {
                (it is ResultLoad.Success) shouldBe true
                it.data?.error shouldBe false
                it.data?.message shouldBe "berhasil login!"
                it.data?.loginResult.also { loginResult ->
                    loginResult?.userId shouldBe expectedLoginResult.userId
                    loginResult?.name shouldBe expectedLoginResult.name
                    loginResult?.token shouldBe expectedLoginResult.token
                }
            }
            authenticationRepository.verify().loginUser(email, password)
        }
    }

    @Test
    fun `loginUser, when login is unsuccessful, should return Error ResultLoad with correct error message`() {
        val email = "joni34324@gugel.com"
        val password = "passwordrumit123"
        val expectedResult = flowOf(ResultLoad.Error<LoginResponse>("email belum terdaftar!"))

        runTest {
            authenticationRepository.loginUser(email, password) returns expectedResult

            viewModel.loginUser( email, password)

            viewModel.loginResult.getOrAwaitValue().also {
                (it is ResultLoad.Error) shouldBe true
                it.message shouldBe "email belum terdaftar!"
            }
            authenticationRepository.verify().loginUser(email, password)
        }
    }

    @Test
    fun `saveUser, when invoked, should invoke repository`() {
        val name = "Joni"
        val token = "azFDAS432FDSA423oisdw"

        viewModel.saveUser(name, token)

        authenticationRepository.verify().saveUser(name, token)
    }

    @Test
    fun `checkLoginSession, when invoked, should invoke repository and return correct value`() {
        authenticationRepository.isLoggedIn().run {
            this returns true
            viewModel.checkLoginSession()
            viewModel.isLoggedIn.getOrAwaitValue() shouldBe true

            this returns false
            viewModel.checkLoginSession()
            viewModel.isLoggedIn.getOrAwaitValue() shouldBe false

        }

        authenticationRepository.verify().isLoggedIn()
    }
}
