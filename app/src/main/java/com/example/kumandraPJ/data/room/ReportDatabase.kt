package com.example.kumandraPJ.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.kumandraPJ.data.remote.response.Report

@Database(entities = [Report::class, RemoteKeys::class], version = 4, exportSchema = false)
abstract class ReportDatabase : RoomDatabase() {
    abstract fun reportDao(): ReportDao
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