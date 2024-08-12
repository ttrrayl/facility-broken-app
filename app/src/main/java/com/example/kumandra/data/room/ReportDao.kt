package com.example.kumandra.data.room

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.kumandra.data.remote.response.Building
import com.example.kumandra.data.remote.response.Classes
import com.example.kumandra.data.remote.response.DetailFacility
import com.example.kumandra.data.remote.response.ListStoryItem
import com.example.kumandra.data.remote.response.Report

@Dao
interface ReportDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReport(report: List<Report>)

    @Query("SELECT * FROM report")
    fun getAllReport(): PagingSource<Int, Report>

    @Query("SELECT * FROM report WHERE id_status = :idStatus")
    fun getAllReportByStatusId(idStatus: String): PagingSource<Int, Report>

    @Query("DELETE FROM report")
    suspend fun deleteAll()

    @Query("SELECT * FROM report WHERE id_student = :idStudent")
    fun getReportByStudentId(idStudent: String): PagingSource<Int, Report>

    @Query("SELECT * FROM report WHERE id_student = :idStudent AND id_status= :idStatus")
    fun getReportByStudentIdAndStatus(idStudent: String, idStatus: String): PagingSource<Int, Report>

    @Query("SELECT COUNT(*) FROM report WHERE id_student = :idStudent AND id_status= :idStatus")
    fun getTotalReportsByStat(idStudent: String, idStatus: String): LiveData<Int>

    @Query("SELECT COUNT(*) FROM report WHERE id_student = :idStudent")
    fun getTotalReports(idStudent: String): LiveData<Int>
//    @Query("SELECT COUNT(*) FROM report WHERE id_status = :specificId")
//    fun getTotalReports(specificId: String?): LiveData<Int>


}