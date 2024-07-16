package com.example.kumandraPJ.data.local

import java.util.Date

data class BuildingModel(
    val id_building: Int,
    val id_pj: Int,
    val nama_bulding: String,
    val tanggal_post: Date,
    val tanggal: Date
)
