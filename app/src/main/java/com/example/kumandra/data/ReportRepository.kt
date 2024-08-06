package com.example.kumandra.data

import androidx.lifecycle.LiveData
import androidx.paging.*
import com.example.kumandra.data.remote.ApiService
import com.example.kumandra.data.remote.response.Building
import com.example.kumandra.data.remote.response.Classes
import com.example.kumandra.data.remote.response.DetailFacility
import com.example.kumandra.data.remote.response.Report
import com.example.kumandra.data.room.ReportDatabase
import com.example.kumandra.data.room.StoryRemoteMediator

class ReportRepository(
    private val reportDatabase: ReportDatabase,
    private val apiService: ApiService
){
    @OptIn(ExperimentalPagingApi::class)
    fun getReport(token: String, idStudent: String?, idStatus: String?): LiveData<PagingData<Report>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            remoteMediator = StoryRemoteMediator(token, reportDatabase, apiService),
            pagingSourceFactory = {
                if (idStudent != null){
                    if (idStatus != null){
                        reportDatabase.reportDao().getReportByStudentIdAndStatus(idStudent, idStatus)
                    } else{
                        reportDatabase.reportDao().getReportByStudentId(idStudent)
                    }
                } else {
                    if (idStatus != null) {
                        reportDatabase.reportDao().getAllReportByStatusId(idStatus)
                    } else{
                        reportDatabase.reportDao().getAllReport()
                    }
                }
            }
        ).liveData
    }

    suspend fun fetchBuilding(building: List<Building>){
        return reportDatabase.buildingDao().insertDataBuilding(building)
    }

    suspend fun fetchClasses(classes: List<Classes>){
        return reportDatabase.classesDao().insertDataClasses(classes)
    }

    suspend fun fetchDetailFacil(deFacil: List<DetailFacility>){
        return reportDatabase.facilDao().insertDataFacil(deFacil)
    }

    fun getAllBuilding(): LiveData<List<Building>>{
        return reportDatabase.buildingDao().getAllBuilding()
    }

    fun getAllClasses(idBuilding: String?): LiveData<List<Classes>>{
        return reportDatabase.classesDao().getAllClassesByIdBuilding(idBuilding)
    }

    fun getAllDeFacil(idClasses: String?): LiveData<List<DetailFacility>>{
        return reportDatabase.facilDao().getAllFacilByIdClasses(idClasses)
    }

}