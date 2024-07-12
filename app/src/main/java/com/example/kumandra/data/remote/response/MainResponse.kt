package com.example.kumandra.data.remote.response

data class MainResponse(
    val error: Boolean,
    val listReport: List<Report>,
    val message: String
)