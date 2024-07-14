package com.example.kumandra.di

import android.content.Context
import com.example.kumandra.data.ReportRepository
import com.example.kumandra.data.remote.ApiConfig
import com.example.kumandra.data.room.ReportDatabase

object Injection {
    fun provideRepository(context: Context): ReportRepository {
        val database = ReportDatabase.getDatabase(context)
        val apiService = ApiConfig.getApiService()
        return ReportRepository(database, apiService)


    }


}