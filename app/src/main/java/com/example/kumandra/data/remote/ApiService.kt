package com.example.kumandra.data.remote

import com.example.kumandra.data.remote.response.AddStoryResponse
import com.example.kumandra.data.remote.response.BuildingResponses
import com.example.kumandra.data.remote.response.ClassesResponses
import com.example.kumandra.data.remote.response.DeleteResponses
import com.example.kumandra.data.remote.response.DetailFacilResponses
import com.example.kumandra.data.remote.response.DetailReportResponses
import com.example.kumandra.data.remote.response.LoginResponse
import com.example.kumandra.data.remote.response.MainResponse
import com.example.kumandra.data.remote.response.RegisterResponse
import com.example.kumandra.data.remote.response.SaveTokenResponses
import com.example.kumandra.data.remote.response.UpdateResponses
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @FormUrlEncoded
    @POST("register")
    fun register(
        @Field("username") name: String,
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("confirm_password") confirmPassword: String,
    ): Call<RegisterResponse>

    @FormUrlEncoded
    @POST("login")
    fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<LoginResponse>

    @GET("report")
    suspend fun getStories(
        @Header("Authorization") authorization: String,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): MainResponse

    @GET("building")
    suspend fun listBuilding(): Response<BuildingResponses>

    @GET("classes")
    suspend fun listClasses(): Response<ClassesResponses>

    @GET("detail_facil")
    suspend fun listDetailFacil(): Response<DetailFacilResponses>

    @Multipart
    @POST("report")
    fun addStory(
        @Header("Authorization") authorization: String,
        @Part pictures: MultipartBody.Part?,
        @Part ("id_student") idStudent: RequestBody,
        @Part("id_building") idBuilding: RequestBody,
        @Part("id_classes") idClasses: RequestBody,
        @Part("id_detail_facilities") idDetailFacil: RequestBody,
        @Part("description") description: RequestBody,
        @Part("lat") lat: RequestBody?,
        @Part("lon") lon: RequestBody?,
    ): Call<AddStoryResponse>

    @GET("report/{id_report}")
    suspend fun detailReport(
        @Path("id_report") id_report: String
    ): Response<DetailReportResponses>

    @DELETE("report/{id_report}")
    fun deleteReport(
        @Path("id_report") id_report: String?
    ): Call<DeleteResponses>

    @Multipart
    @POST("report/{id_report}")
    fun updateReport(
        @Header("Authorization") authorization: String,
        @Path("id_report") id_report: String?,
        @Part pictures: MultipartBody.Part?,
        @Part("id_building") id_building: RequestBody,
        @Part("id_classes") id_classes: RequestBody,
        @Part("id_detail_facilities") id_detail_facilities: RequestBody,
        @Part("description") description: RequestBody,
    ): Call<UpdateResponses>

    @FormUrlEncoded
    @POST("fcm")
    suspend fun addFcmToken(
        @Field("id_user") idUser: String,
        @Field("id_role") idRole: String,
        @Field("token") fcmToken: String
    ): Response<SaveTokenResponses>
}