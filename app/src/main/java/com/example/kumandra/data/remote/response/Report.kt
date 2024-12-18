package com.example.kumandra.data.remote.response

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Entity(tableName = "report")
@Parcelize
data class Report(

    @PrimaryKey
    @field:SerializedName("id_report")
    val id_report: String,

    @field:SerializedName("created_at")
    val created_at: String,

    @field:SerializedName("description")
    val description: String,

    @field:SerializedName("email")
    val email: String,

    @field:SerializedName("image_url")
    val image_url: String,

    @field:SerializedName("id_classes")
    val id_classes: String,

    @field:SerializedName("id_building")
    val id_building: String,

    @field:SerializedName("id_detail_facilities")
    val id_detail_facilities: String,

    @field:SerializedName("id_status_respon")
    val id_status: String,

    @field:SerializedName("id_student")
    val id_student: String,

    @field:SerializedName("nama_classes")
    val nama_classes: String,

    @field:SerializedName("nama_building")
    val nama_building: String,

    @field:SerializedName("nama_detail_facilities")
    val nama_detail_facilities: String,

    @field:SerializedName("nama_status")
    val nama_status: String,

    @field:SerializedName("username")
    val username: String,

    @field:SerializedName("respon")
    val respon: String?,

    @field:SerializedName("level_report")
    val level_report: String?,

    @field:SerializedName("process_image")
    val processImage: String?,

    @field:SerializedName("process_image_date")
    val processImageDate: String?,

    @field:SerializedName("completion_image")
    val completionImage: String?,

    @field:SerializedName("completion_image_date")
    val completionImageDate: String?,

    @field:SerializedName("updated_at")
    val updated_at: String
): Parcelable