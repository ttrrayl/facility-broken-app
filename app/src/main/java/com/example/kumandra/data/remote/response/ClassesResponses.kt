package com.example.kumandra.data.remote.response

import androidx.room.Entity
import androidx.room.PrimaryKey

data class ClassesResponses(
    val classes: List<Classes>,
    val message: String
)
@Entity(tableName = "classes")
data class Classes(
    @PrimaryKey
    val id_classes: String,
    val id_building: String,
    val nama_classes: String
)