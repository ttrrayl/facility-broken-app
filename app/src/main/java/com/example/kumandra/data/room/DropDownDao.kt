package com.example.kumandra.data.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.kumandra.data.remote.response.Building
import com.example.kumandra.data.remote.response.Classes
import com.example.kumandra.data.remote.response.DetailFacility
import com.example.kumandra.data.remote.response.Report

@Dao
interface BuildingDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDataBuilding(building: List<Building>)

    @Query("SELECT * FROM building")
    fun getAllBuilding(): LiveData<List<Building>>
}
@Dao
interface ClassesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDataClasses(classes: List<Classes>)
    @Query("SELECT * FROM classes WHERE id_building = :idBuilding")
    fun getAllClassesByIdBuilding(idBuilding: String): LiveData<List<Classes>>
}

@Dao
interface DetailFacilDao{
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDataFacil(deFacil: List<DetailFacility>)
    @Query("SELECT * FROM detailFacil WHERE id_classes = :idClasses")
    fun getAllFacilByIdClasses(idClasses: String): LiveData<List<DetailFacility>>
}

