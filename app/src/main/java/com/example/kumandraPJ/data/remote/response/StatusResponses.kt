package com.example.kumandraPJ.data.remote.response

data class StatusResponses(
    val status: List<Status>,
    val message: String
)
data class Status(
    val id_status_respon: String,
    val nama_status: String
)