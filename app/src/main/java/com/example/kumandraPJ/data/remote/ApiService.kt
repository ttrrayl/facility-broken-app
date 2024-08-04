package com.example.kumandraPJ.data.remote

import com.example.kumandraPJ.data.remote.response.AddStoryResponse
import com.example.kumandraPJ.data.remote.response.BuildingResponses
import com.example.kumandraPJ.data.remote.response.ClassesResponses
import com.example.kumandraPJ.data.remote.response.DetailFacilResponses
import com.example.kumandraPJ.data.remote.response.LoginResponse
import com.example.kumandraPJ.data.remote.response.MainResponse
import com.example.kumandraPJ.data.remote.response.SaveTokenResponses
import com.example.kumandraPJ.data.remote.response.StatusResponses
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @FormUrlEncoded
    @POST("login")
    fun login(
        @Field("contact") contact: String
    ): Call<LoginResponse>


    @GET("report/user/{id_pj}")
    suspend fun getStoriesByIdPj(
        @Header("Authorization") authorization: String,
        @Path("id_pj") id_pj:String,
        @Query("page") page: Int,
        @Query("size") size: Int,
    ): MainResponse

    @GET("status")
    suspend fun listStatus(): Response<StatusResponses>

    @GET("classes")
    suspend fun listClasses(): Response<ClassesResponses>

    @GET("detail_facil")
    suspend fun listDetailFacil(): Response<DetailFacilResponses>

    @FormUrlEncoded
    @POST("response")
    fun addResponse(
        @Header("Authorization") authorization: String,
        @Field("id_report") idReport: String,
        @Field("id_pj") idPj: String,
        @Field("id_status") idStatus: String,
        @Field("content") content: String,
        ): Call<AddStoryResponse>

    @FormUrlEncoded
    @POST("fcm")
    suspend fun addFcmToken(
        @Field("id_user") idUser: String,
        @Field("id_role") idRole: String,
        @Field("token") fcmToken: String
    ): Response<SaveTokenResponses>
}