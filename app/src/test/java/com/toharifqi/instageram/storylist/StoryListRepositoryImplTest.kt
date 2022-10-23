package com.toharifqi.instageram.storylist

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
import com.toharifqi.instageram.core.SessionManager
import com.toharifqi.instageram.core.local.StoryDatabase
import com.toharifqi.instageram.core.local.StoryEntity
import com.toharifqi.instageram.core.remote.ApiService
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
class StoryListRepositoryImplTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var database: StoryDatabase

    @Mock
    private lateinit var apiService: ApiService

    @Mock
    private lateinit var sessionManager: SessionManager

    @Mock
    private lateinit var repositoryMock: StoryListRepository

    private lateinit var repository: StoryListRepository

    @Before
    fun setUp() {
        repository = StoryListRepositoryImpl(database, apiService, sessionManager)
    }

    @After
    fun tearDown() {
        verifyNoMoreInteractions(database, sessionManager, apiService)
    }

    @Test
    fun `getToken, when invoked, should invoke sessionManager`() {
        val token = "azFDAS432FDSA423oisdw"

        sessionManager.getToken() returns token

        repository.getToken() shouldBe token
        sessionManager.verify().getToken()
    }

    @Test
    fun `logOut, when invoked, should invoke sessionManager`() {
        repository.logOut()
        sessionManager.verify().clearSession()
    }

    @Test
    fun `getAllStories, when API successfully fetch data, should return correct values`() {
        val token = "azFDAS432FDSA423oisdw"
        val dummyStories = DataDummy.generateDummyListStoryEntity()
        val expectedResult = flowOf(StoryPagingSource.snapshot(dummyStories))

        runTest {
            repositoryMock.getAllStories(token) returns expectedResult

            repositoryMock.getAllStories(token).collect {
                val differ = AsyncPagingDataDiffer(
                    diffCallback = StoryAdapter.DIFF_CALLBACK,
                    updateCallback = noopListUpdateCallback,
                    workerDispatcher = Dispatchers.Main
                )

                differ.submitData(convertPagingDataEntityToDomain(it))

                differ.run {
                    snapshot().notNull()
                    snapshot().size shouldBe dummyStories.size
                }
            }
        }
    }

    private val noopListUpdateCallback = object : ListUpdateCallback {
        override fun onInserted(position: Int, count: Int) {}

        override fun onRemoved(position: Int, count: Int) {}

        override fun onMoved(fromPosition: Int, toPosition: Int) {}

        override fun onChanged(position: Int, count: Int, payload: Any?) {}
    }

    private fun convertPagingDataEntityToDomain(
        entity: PagingData<StoryEntity>
    ): PagingData<StoryDomainData> = entity.map { StoryDomainData(it) }

}
