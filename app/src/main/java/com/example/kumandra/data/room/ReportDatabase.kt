package com.example.kumandra.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.kumandra.data.remote.response.Building
import com.example.kumandra.data.remote.response.Classes
import com.example.kumandra.data.remote.response.DetailFacility
import com.example.kumandra.data.remote.response.ListStoryItem
import com.example.kumandra.data.remote.response.Report

@Database(entities = [Report::class, RemoteKeys::class,Building::class, Classes::class, DetailFacility::class], version = 6, exportSchema = false)
abstract class ReportDatabase : RoomDatabase() {
    abstract fun reportDao(): ReportDao
    abstract fun buildingDao(): BuildingDao
    abstract fun classesDao(): ClassesDao
    abstract fun facilDao(): DetailFacilDao
    abstract fun remoteKeysDao(): RemoteKeysDao

    companion object {
        @Volatile
        private var INSTANCE: ReportDatabase? = null

        @JvmStatic
        fun getDatabase(context: Context): ReportDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    ReportDatabase::class.java, "report_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}