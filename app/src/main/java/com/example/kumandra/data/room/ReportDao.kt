package com.example.kumandra.data.room

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.kumandra.data.remote.response.ListStoryItem
import com.example.kumandra.data.remote.response.Report

@Dao
interface ReportDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReport(report: List<Report>)

    @Query("SELECT * FROM report")
    fun getAllReport(): PagingSource<Int, Report>

    @Query("DELETE FROM report")
    suspend fun deleteAll()

    @Query("SELECT * FROM report WHERE id_student = :idStudent")
    fun getReportByStudentId(idStudent: Int): PagingSource<Int, Report>
}