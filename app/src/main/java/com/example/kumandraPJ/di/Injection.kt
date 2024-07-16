package com.example.kumandraPJ.di

import android.content.Context
import com.example.kumandraPJ.data.ReportRepository
import com.example.kumandraPJ.data.remote.ApiConfig
import com.example.kumandraPJ.data.room.ReportDatabase

object Injection {
    fun provideRepository(context: Context): ReportRepository {
        val database = ReportDatabase.getDatabase(context)
        val apiService = ApiConfig.getApiService()
        return ReportRepository(database, apiService)


    }


}