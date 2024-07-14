package com.example.kumandra.data

import androidx.lifecycle.LiveData
import androidx.paging.*
import com.example.kumandra.data.remote.ApiService
import com.example.kumandra.data.remote.response.Report
import com.example.kumandra.data.room.ReportDatabase
import com.example.kumandra.data.room.StoryRemoteMediator

class ReportRepository(
    private val reportDatabase: ReportDatabase,
    private val apiService: ApiService
){
    @OptIn(ExperimentalPagingApi::class)
    fun getReport(token: String, idStudent: Int?): LiveData<PagingData<Report>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            remoteMediator = StoryRemoteMediator(token, reportDatabase, apiService, idStudent),
            pagingSourceFactory = {
                if (idStudent != null){
                    reportDatabase.reportDao().getReportByStudentId(idStudent)
                } else {
                    reportDatabase.reportDao().getAllReport()
                }

            }
        ).liveData
    }
}