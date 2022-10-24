package com.toharifqi.instageram.storymap

import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.toharifqi.instageram.DataDummy
import com.toharifqi.instageram.MainDispatcherRule
import com.toharifqi.instageram.common.notNull
import com.toharifqi.instageram.common.returns
import com.toharifqi.instageram.common.shouldBe
import com.toharifqi.instageram.common.verify
import com.toharifqi.instageram.core.ResultLoad
import com.toharifqi.instageram.core.remote.ApiService
import com.toharifqi.instageram.core.remote.GetAllStoriesResponse
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
class StoryMapRepositoryImplTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var apiService: ApiService

    private val testCoroutineDispatcher = UnconfinedTestDispatcher()

    private lateinit var repository: StoryMapRepository

    @Before
    fun setUp() {
        repository = StoryMapRepositoryImpl(apiService, testCoroutineDispatcher)
    }

    @After
    fun tearDown() {
        verifyNoMoreInteractions(apiService)
    }

    @Test
    fun `getAllStoriesWithLocation, when API response is not error, should return Success ResultLoad with correct response`() {
        val token = "azFDAS432FDSA423oisdw"
        val dummyResponse = GetAllStoriesResponse(
            false,
            "berhasil memuat stories!",
            DataDummy.generateDummyListStoryResponse()
        )

        runTest {
            apiService.getAllStories(
                token = token,
                location = 1,
                size = 30
            ) returns dummyResponse

            repository.getAllStoriesWithLocation(token).collect {
                (it is ResultLoad.Success) shouldBe true
                it.data.notNull()
                it.data?.size shouldBe dummyResponse.stories?.size
                it.data?.forEachIndexed { index, storyDomainData ->
                    storyDomainData.id shouldBe dummyResponse.stories?.get(index)?.id
                }
            }

            apiService.verify().getAllStories(
                token = token,
                location = 1,
                size = 30
            )
        }
    }

    @Test
    fun `getAllStoriesWithLocation, when API response is error, should return Error ResultLoad with correct error message`() {
        val token = "azFDAS432FDSA423oisdw"
        val dummyResponse = GetAllStoriesResponse(
            true,
            "gagal memuat stories!",
            null
        )

        runTest {
            apiService.getAllStories(
                token = token,
                location = 1,
                size = 30
            ) returns dummyResponse

            repository.getAllStoriesWithLocation(token).collect {
                (it is ResultLoad.Error) shouldBe true
                it.data shouldBe null
                it.message shouldBe dummyResponse.message
            }

            apiService.verify().getAllStories(
                token = token,
                location = 1,
                size = 30
            )
        }
    }
}
