package com.example.kumandra.data.remote.response

data class DetailReportResponses(
    val error: Boolean,
    val message: String,
    val report: ReportDetail
)
data class ReportDetail(
    val created_at: String,
    val description: String,
    val email: String,
    val id_building: String,
    val id_classes: String,
    val id_detail_facilities: String,
    val id_pj: String,
    val id_report: String,
    val id_status: String,
    val id_student: String,
    val image_url: String,
    val nama_classes: String,
    val nama_detail_facilities: String,
    val nama_status: String,
    val updated_at: String
)