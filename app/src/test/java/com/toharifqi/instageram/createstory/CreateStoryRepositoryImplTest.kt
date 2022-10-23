package com.toharifqi.instageram.createstory

import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.toharifqi.instageram.DataDummy
import com.toharifqi.instageram.MainDispatcherRule
import com.toharifqi.instageram.common.returns
import com.toharifqi.instageram.common.shouldBe
import com.toharifqi.instageram.common.verify
import com.toharifqi.instageram.core.ResultLoad
import com.toharifqi.instageram.core.remote.AddNewStoryResponse
import com.toharifqi.instageram.core.remote.ApiService
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
class CreateStoryRepositoryImplTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var apiService: ApiService

    private val testCoroutineDispatcher = UnconfinedTestDispatcher()
    private lateinit var repository: CreateStoryRepository

    @Before
    fun setUp() {
        repository = CreateStoryRepositoryImpl(apiService, testCoroutineDispatcher)
    }

    @After
    fun tearDown() {
        verifyNoMoreInteractions(apiService)
    }

    @Test
    fun `postStory, when API response is not error, should return Success ResultLoad with correct response`() {
        val token = "azFDAS432FDSA423oisdw"
        val dummyMultipart = DataDummy.generateDummyMultipartFile()
        val dummyDescription = DataDummy.generateDummyRequestBody()
        val addNewStoryResponse = AddNewStoryResponse(false, "story uploaded!")

        runTest {
            apiService.addNewStory(
                token,
                dummyMultipart,
                dummyDescription,
                null,
                null
            ) returns addNewStoryResponse

            repository.postStory(
                token,
                dummyMultipart,
                dummyDescription,
                null,
                null
            ).collect {
                (it is ResultLoad.Success) shouldBe true
                it.data?.error shouldBe false
                it.data?.message shouldBe "story uploaded!"
            }

            apiService.verify().addNewStory(
                token,
                dummyMultipart,
                dummyDescription,
                null,
                null
            )
        }
    }

    @Test
    fun `postStory, when API response not error, should return Error ResultLoad with correct error message`() {
        val token = "azFDAS432FDSA423oisdw"
        val dummyMultipart = DataDummy.generateDummyMultipartFile()
        val dummyDescription = DataDummy.generateDummyRequestBody()
        val addNewStoryResponse = AddNewStoryResponse(true, "uploading failed!")

        runTest {
            apiService.addNewStory(
                token,
                dummyMultipart,
                dummyDescription,
                null,
                null
            ) returns addNewStoryResponse

            repository.postStory(
                token,
                dummyMultipart,
                dummyDescription,
                null,
                null
            ).collect {
                (it is ResultLoad.Error) shouldBe true
                it.message shouldBe "uploading failed!"
            }

            apiService.verify().addNewStory(
                token,
                dummyMultipart,
                dummyDescription,
                null,
                null
            )
        }
    }
}
