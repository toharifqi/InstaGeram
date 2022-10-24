package com.toharifqi.instageram.createstory

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.toharifqi.instageram.DataDummy
import com.toharifqi.instageram.MainDispatcherRule
import com.toharifqi.instageram.common.returns
import com.toharifqi.instageram.common.shouldBe
import com.toharifqi.instageram.common.verify
import com.toharifqi.instageram.core.ResultLoad
import com.toharifqi.instageram.core.remote.AddNewStoryResponse
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
class CreateStoryViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var repository: CreateStoryRepository

    private lateinit var viewModel: CreateStoryViewModel

    @Before
    fun setUp() {
        viewModel = CreateStoryViewModel(repository)
    }

    @After
    fun tearDown() {
        verifyNoMoreInteractions(repository)
    }

    @Test
    fun `postStory, when upload is successful, should return Success ResultLoad with correct upload result`() {
        val token = "azFDAS432FDSA423oisdw"
        val dummyMultipart = DataDummy.generateDummyMultipartFile()
        val dummyDescription = DataDummy.generateDummyRequestBody()
        val expectedResult =
            flowOf(ResultLoad.Success(AddNewStoryResponse(false, "story uploaded!")))

        runTest {
            repository.postStory(
                token,
                dummyMultipart,
                dummyDescription,
                null,
                null
            ) returns expectedResult

            viewModel.postStory(
                token,
                dummyMultipart,
                dummyDescription,
                null,
                null
            )

            viewModel.postResult.getOrAwaitValue().also {
                (it is ResultLoad.Success) shouldBe true
                it.data?.error shouldBe false
                it.data?.message shouldBe "story uploaded!"
            }

            repository.verify().postStory(
                token,
                dummyMultipart,
                dummyDescription,
                null,
                null
            )
        }
    }

    @Test
    fun `postStory, when upload is unsuccessful, should return Error ResultLoad with correct error message`() {
        val token = "azFDAS432FDSA423oisdw"
        val dummyMultipart = DataDummy.generateDummyMultipartFile()
        val dummyDescription = DataDummy.generateDummyRequestBody()
        val expectedResult =
            flowOf(ResultLoad.Error<AddNewStoryResponse>("file is too large!"))

        runTest {
            repository.postStory(
                token,
                dummyMultipart,
                dummyDescription,
                null,
                null
            ) returns expectedResult

            viewModel.postStory(
                token,
                dummyMultipart,
                dummyDescription,
                null,
                null
            )

            viewModel.postResult.getOrAwaitValue().also {
                (it is ResultLoad.Error) shouldBe true
                it.data shouldBe null
                it.message shouldBe "file is too large!"
            }

            repository.verify().postStory(
                token,
                dummyMultipart,
                dummyDescription,
                null,
                null
            )
        }
    }
}
