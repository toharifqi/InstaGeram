package com.toharifqi.instageram.storylist

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.paging.map
import androidx.recyclerview.widget.ListUpdateCallback
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.toharifqi.instageram.DataDummy
import com.toharifqi.instageram.MainDispatcherRule
import com.toharifqi.instageram.StoryPagingSource
import com.toharifqi.instageram.common.notNull
import com.toharifqi.instageram.common.returns
import com.toharifqi.instageram.common.shouldBe
import com.toharifqi.instageram.common.verify
import com.toharifqi.instageram.core.local.StoryEntity
import com.toharifqi.instageram.getOrAwaitValue
import kotlinx.coroutines.Dispatchers
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
class StoryListViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRules = MainDispatcherRule()

    @Mock
    private lateinit var repository: StoryListRepository

    private lateinit var viewModel: StoryListViewModel

    @Before
    fun setUp() {
        viewModel = StoryListViewModel(repository)
    }

    @After
    fun tearDown() {
        verifyNoMoreInteractions(repository)
    }

    @Test
    fun `loadAllStories, when invoked, should return correct values and not null`() {
        val token = "azFDAS432FDSA423oisdw"
        val dummyStories = DataDummy.generateDummyListStoryEntity()
        val expectedResult = flowOf(StoryPagingSource.snapshot(dummyStories))

        runTest {
            repository.getAllStories(token) returns expectedResult

            viewModel.loadAllStories(token)
            viewModel.stories.getOrAwaitValue().also {
                val differ = AsyncPagingDataDiffer(
                    diffCallback = StoryAdapter.DIFF_CALLBACK,
                    updateCallback = noopListUpdateCallback,
                    workerDispatcher = Dispatchers.Main
                )

                differ.submitData(it)

                differ.run {
                    snapshot().notNull()
                    snapshot().size shouldBe dummyStories.size
                    snapshot().forEachIndexed { index, storyDomainData ->
                        storyDomainData?.id shouldBe dummyStories[index].id
                    }
                }
            }
            repository.verify().getAllStories(token)
        }
    }

    @Test
    fun `getToken, when invoked, should invoke repository and return correct value`() {
        val token = "azFDAS432FDSA423oisdw"

        repository.getToken() returns token

        viewModel.getToken()
        viewModel.token.getOrAwaitValue() shouldBe token
        repository.verify().getToken()
    }

    @Test
    fun `logOut, when invoked, should invoke repository and clear token`() {
        val token = "azFDAS432FDSA423oisdw"

        repository.getToken() returns token

        viewModel.getToken()
        viewModel.token.getOrAwaitValue() shouldBe token
        repository.verify().getToken()

        viewModel.logOut()
        viewModel.token.getOrAwaitValue() shouldBe null
        repository.verify().logOut()
    }

    val noopListUpdateCallback = object : ListUpdateCallback {
        override fun onInserted(position: Int, count: Int) {}

        override fun onRemoved(position: Int, count: Int) {}

        override fun onMoved(fromPosition: Int, toPosition: Int) {}

        override fun onChanged(position: Int, count: Int, payload: Any?) {}
    }

    private fun convertPagingDataEntityToDomain(
        entity: PagingData<StoryEntity>
    ): PagingData<StoryDomainData> = entity.map { StoryDomainData(it) }
}