package com.example.kumandraPJ.data.remote.response

data class MainResponse(
    val error: Boolean,
    val listReport: List<Report>,
    val message: String
)