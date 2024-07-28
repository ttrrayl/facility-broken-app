package com.example.kumandra.data.remote.response

import androidx.room.Entity
import androidx.room.PrimaryKey

data class DetailFacilResponses(
    val detail_facilities: List<DetailFacility>,
    val message: String
)
@Entity(tableName = "detailFacil")
data class DetailFacility(
    @PrimaryKey
    val id_detail_facilities: String,
    val id_classes: String,
    val id_facilities: String,
    val nama_detail_facilities: String,
    val quantity: String,
    val tanggal_post: String
)