package com.example.kumandra.di

import android.content.Context
import com.example.kumandra.data.StoryRepository
import com.example.kumandra.data.remote.ApiConfig
import com.example.kumandra.data.room.StoryDatabase

object Injection {
    fun provideRepository(context: Context): StoryRepository {
        val database = StoryDatabase.getDatabase(context)
        val apiService = ApiConfig.getApiService()
        return StoryRepository(database, apiService)
    }
}