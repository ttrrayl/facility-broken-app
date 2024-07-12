package com.example.kumandra.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.recyclerview.widget.ListUpdateCallback
import com.example.kumandra.adapter.ReportAdapter
import com.example.kumandra.data.ReportRepository
import com.example.kumandra.data.local.UserSession
import com.example.kumandra.data.remote.response.Report
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest{
    private lateinit var pref: UserSession

    @Mock
    private lateinit var reportRepository: ReportRepository
    private val dummyStory = DataDummy.generateDummyStory()
    private val dummyToken = "yJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiJ1c2VyLU56enVBZGJkRVFiU0QiLCJpY"

    @Before
    fun setUp() {
        pref = mock(UserSession::class.java)

    }

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRules = MainDispatcherRule()

    @Test
    fun `when Get Story Should Not Null and Return Data`() = runTest {
        val data: PagingData<Report> = StoryPagingSource.snapshot(dummyStory)
        val expectedStory = MutableLiveData<PagingData<Report>>()
        expectedStory.value = data
        Mockito.`when`(reportRepository.getReport(dummyToken)).thenReturn(expectedStory)

        val mainViewModel = MainViewModel(reportRepository, pref)
        val actualStory: PagingData<Report> =
            mainViewModel.getStories(dummyToken).getOrAwaitValue()

        val differ  = AsyncPagingDataDiffer(
            diffCallback = ReportAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )
        differ.submitData(actualStory)

        assertNotNull(differ.snapshot())
        assertEquals(dummyStory.size, differ.snapshot().size)
        assertEquals(dummyStory[0], differ.snapshot()[0])
    }

    @Test
    fun `when Get Story Empty Should Return Zero or no Data`() = runTest {
        val data: PagingData<Report> = PagingData.from(emptyList())
        val expectedStory = MutableLiveData<PagingData<Report>>()
        expectedStory.value = data
        Mockito.`when`(reportRepository.getReport(dummyToken)).thenReturn(expectedStory)

        val mainViewModel = MainViewModel(reportRepository, pref)
        val actualStory: PagingData<Report> =
            mainViewModel.getStories(dummyToken).getOrAwaitValue()

        val differ  = AsyncPagingDataDiffer(
            diffCallback = ReportAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )
        differ.submitData(actualStory)
        assertEquals(0, differ.snapshot().size)
    }

    class StoryPagingSource: PagingSource<Int, LiveData<List<Report>>>() {
        companion object{
            fun snapshot(items: List<Report>): PagingData<Report>{
                return PagingData.from(items)
            }
        }

        override fun getRefreshKey(state: PagingState<Int, LiveData<List<Report>>>): Int {
            return 0
        }

        override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LiveData<List<Report>>> {
            return LoadResult.Page(emptyList(),0,1)
        }
    }

    private val noopListUpdateCallback = object : ListUpdateCallback {
        override fun onInserted(position: Int, count: Int) {

        }

        override fun onRemoved(position: Int, count: Int) {

        }

        override fun onMoved(fromPosition: Int, toPosition: Int) {

        }

        override fun onChanged(position: Int, count: Int, payload: Any?) {
        }

    }
}