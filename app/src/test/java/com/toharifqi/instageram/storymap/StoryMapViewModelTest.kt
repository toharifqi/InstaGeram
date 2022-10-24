package com.toharifqi.instageram.storymap

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.toharifqi.instageram.DataDummy
import com.toharifqi.instageram.MainDispatcherRule
import com.toharifqi.instageram.common.notNull
import com.toharifqi.instageram.common.returns
import com.toharifqi.instageram.common.shouldBe
import com.toharifqi.instageram.common.verify
import com.toharifqi.instageram.core.ResultLoad
import com.toharifqi.instageram.getOrAwaitValue
import com.toharifqi.instageram.storylist.StoryDomainData
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
class StoryMapViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRules = MainDispatcherRule()

    @Mock
    private lateinit var repository: StoryMapRepository

    private lateinit var viewModel: StoryMapViewModel

    @Before
    fun setUp() {
        viewModel = StoryMapViewModel(repository)
    }

    @After
    fun tearDown() {
        verifyNoMoreInteractions(repository)
    }

    @Test
    fun `loadAllStoriesWithLocation, when successfully fetched the data, should return Success ResultLoad with correct response`() {
        val token = "azFDAS432FDSA423oisdw"
        val dummyStoryDomainData = DataDummy.generateDummyListStoryDomainData()
        val expectedResult = flowOf(ResultLoad.Success(dummyStoryDomainData))

        runTest {
            repository.getAllStoriesWithLocation(token) returns expectedResult

            viewModel.loadAllStoriesWithLocation(token)
            viewModel.stories.getOrAwaitValue().also {
                (it is ResultLoad.Success) shouldBe true
                it.data.notNull()
                it.data?.size shouldBe dummyStoryDomainData.size
                it.data?.forEachIndexed { index, storyDomainData ->
                    storyDomainData.id shouldBe dummyStoryDomainData[index].id
                }
            }
            repository.verify().getAllStoriesWithLocation(token)
        }
    }

    @Test
    fun `loadAllStoriesWithLocation, when unsuccessfully fetched the data, should return Error ResultLoad with correct error message`() {
        val token = "azFDAS432FDSA423oisdw"
        val expectedResult = flowOf(ResultLoad.Error<List<StoryDomainData>>("gagal memuat stories!"))

        runTest {
            repository.getAllStoriesWithLocation(token) returns expectedResult

            viewModel.loadAllStoriesWithLocation(token)
            viewModel.stories.getOrAwaitValue().also {
                (it is ResultLoad.Error) shouldBe true
                it.data shouldBe null
                it.message shouldBe "gagal memuat stories!"
            }
            repository.verify().getAllStoriesWithLocation(token)
        }
    }
}
