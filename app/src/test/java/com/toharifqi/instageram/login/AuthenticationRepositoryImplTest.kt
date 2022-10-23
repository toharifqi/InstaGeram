package com.toharifqi.instageram.login

import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.toharifqi.instageram.MainDispatcherRule
import com.toharifqi.instageram.common.returns
import com.toharifqi.instageram.common.shouldBe
import com.toharifqi.instageram.common.verify
import com.toharifqi.instageram.core.ResultLoad
import com.toharifqi.instageram.core.SessionManager
import com.toharifqi.instageram.core.remote.ApiService
import com.toharifqi.instageram.core.remote.LoginResponse
import com.toharifqi.instageram.core.remote.LoginResult
import com.toharifqi.instageram.core.remote.RegisterResponse
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.UnconfinedTestDispatcher
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
class AuthenticationRepositoryImplTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var sessionManager: SessionManager

    @Mock
    private lateinit var apiService: ApiService

    private val testCoroutineDispatcher = UnconfinedTestDispatcher()
    private lateinit var repository: AuthenticationRepository

    @Before
    fun setUp() {
        repository =
            AuthenticationRepositoryImpl(sessionManager, apiService, testCoroutineDispatcher)
    }

    @After
    fun tearDown() {
        verifyNoMoreInteractions(sessionManager, apiService)
    }

    @Test
    fun `registerUser, when API response is not error, should return Success ResultLoad with correct response`() {
        val name = "Joni"
        val email = "joni34@googoo.com"
        val password = "passwordrumit123"
        val registerResponse = RegisterResponse(false, "berhasil daftar!")

        runTest {
            apiService.register(
                name,
                email,
                password,
            ) returns registerResponse
            repository.registerUser(name, email, password).collect {
                (it is ResultLoad.Success) shouldBe true
                it.data?.error shouldBe false
                it.data?.message shouldBe "berhasil daftar!"
            }

            apiService.verify().register(name, email, password)
        }
    }

    @Test
    fun `registerUser, when API response is error, should return Error ResultLoad with correct error message`() {
        val name = "Joni"
        val email = "joni34@googoo.com"
        val password = "passw"
        val registerResponse = RegisterResponse(true, "password kurang dari 6 karakter!")

        runTest {
            apiService.register(
                name,
                email,
                password,
            ) returns registerResponse

            repository.registerUser(name, email, password).collect {
                (it is ResultLoad.Error) shouldBe true
                it.message shouldBe "password kurang dari 6 karakter!"
            }

            apiService.verify().register(name, email, password)
        }
    }

    @Test
    fun `loginUser, when API response is not error, should return Success ResultLoad with correct response`() {
        val email = "joni34@googoo.com"
        val password = "passwordrumit123"
        val loginResult = LoginResult("u34213", "Joni", "azFDAS432FDSA423oisdw")
        val loginResponse = LoginResponse(false, "berhasil login!", loginResult)

        runTest {
            apiService.login(email, password) returns loginResponse

            repository.loginUser(email, password).collect {
                (it is ResultLoad.Success) shouldBe true
                it.data?.error shouldBe false
                it.data?.message shouldBe "berhasil login!"
                it.data?.loginResult?.userId shouldBe loginResult.userId
                it.data?.loginResult?.name shouldBe loginResult.name
                it.data?.loginResult?.token shouldBe loginResult.token
            }

            apiService.verify().login(email, password)
        }
    }

    @Test
    fun `loginUser, when API response is error, should return Error ResultLoad with correct error message`() {
        val email = "joni34@googoo.com"
        val password = "passwordrumit123"
        val loginResult = LoginResult("u34213", "Joni", "azFDAS432FDSA423oisdw")
        val loginResponse = LoginResponse(true, "gagal login!", loginResult)

        runTest {
            apiService.login(email, password) returns loginResponse

            repository.loginUser(email, password).collect {
                (it is ResultLoad.Error) shouldBe true
                it.message shouldBe "gagal login!"
            }

            apiService.verify().login(email, password)
        }
    }

    @Test
    fun `saveUser, when invoked, should save user and invoke sessionManager`() {
        val name = "Joni"
        val token = "azFDAS432FDSA423oisdw"

        repository.saveUser(name, token)

        sessionManager.verify().saveToken(name, "Bearer $token")
    }

    @Test
    fun `isLogin, when token exists, should return true`() {
        val token = "azFDAS432FDSA423oisdw"

        sessionManager.getToken() returns token

        repository.isLoggedIn() shouldBe true
        sessionManager.verify().getToken()
    }

    @Test
    fun `isLogin, when token does not exist, should return false`() {
        sessionManager.getToken() returns null

        repository.isLoggedIn() shouldBe false
        sessionManager.verify().getToken()
    }
}
