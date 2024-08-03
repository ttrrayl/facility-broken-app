package com.example.kumandra.data.remote.response

import androidx.room.Entity
import androidx.room.PrimaryKey

data class BuildingResponses(
    val building: List<Building>,
    val message: String
)
@Entity(tableName = "building")
data class Building(
    @PrimaryKey val id_building: String,
    val id_pj: String,
    val nama_building: String
)