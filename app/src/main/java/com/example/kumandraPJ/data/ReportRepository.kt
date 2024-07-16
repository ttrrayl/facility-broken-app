package com.example.kumandraPJ.data

import androidx.lifecycle.LiveData
import androidx.paging.*
import com.example.kumandraPJ.data.remote.ApiService
import com.example.kumandraPJ.data.remote.response.Report
import com.example.kumandraPJ.data.room.ReportDatabase
import com.example.kumandraPJ.data.room.StoryRemoteMediator

class ReportRepository(
    private val reportDatabase: ReportDatabase,
    private val apiService: ApiService
){
    @OptIn(ExperimentalPagingApi::class)
    fun getReport(token: String, idPj: String): LiveData<PagingData<Report>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            remoteMediator = StoryRemoteMediator(token, reportDatabase, apiService, idPj),
            pagingSourceFactory = {
               reportDatabase.reportDao().getReportByPjId(idPj)

            }
        ).liveData
    }
}