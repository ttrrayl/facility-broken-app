package com.example.kumandraPJ.data.room

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.kumandraPJ.data.remote.response.Report

@Dao
interface ReportDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReport(report: List<Report>)

    @Query("SELECT * FROM report")
    fun getAllReport(): PagingSource<Int, Report>

    @Query("DELETE FROM report")
    suspend fun deleteAll()

    @Query("SELECT * FROM report WHERE id_pj = :idPj")
    fun getReportByPjId(idPj: Int): PagingSource<Int, Report>
}