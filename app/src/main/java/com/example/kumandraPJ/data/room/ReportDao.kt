package com.example.kumandraPJ.data.room

import androidx.paging.DataSource
import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RawQuery
import androidx.sqlite.db.SupportSQLiteQuery
import com.example.kumandraPJ.data.remote.response.Report

@Dao
interface ReportDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReport(report: List<Report>)

//    @RawQuery(observedEntities = [Report::class])
//    fun getHabits(query: SupportSQLiteQuery): DataSource.Factory<Int, Report>

    @Query("SELECT * FROM report")
    fun getAllReport(): PagingSource<Int, Report>

    @Query("DELETE FROM report")
    suspend fun deleteAll()

    @Query("SELECT * FROM report WHERE id_status = :idStatus")
    fun getReportByStatusId(idStatus: String?): PagingSource<Int, Report>
}