package com.example.kumandraPJ.data.remote

import com.example.kumandraPJ.data.remote.response.AddStoryResponse
import com.example.kumandraPJ.data.remote.response.BuildingResponses
import com.example.kumandraPJ.data.remote.response.ClassesResponses
import com.example.kumandraPJ.data.remote.response.DetailFacilResponses
import com.example.kumandraPJ.data.remote.response.LoginResponse
import com.example.kumandraPJ.data.remote.response.MainResponse
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

    @GET("report")
    suspend fun getStories(
        @Header("Authorization") authorization: String,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): MainResponse

    @GET("report/user/{id_pj}")
    suspend fun getStoriesByIdPj(
        @Header("Authorization") authorization: String,
        @Path("id_pj") id_pj:Int,
        @Query("page") page: Int,
        @Query("size") size: Int,
    ): MainResponse

    @GET("building")
    suspend fun listBuilding(): Response<BuildingResponses>

    @GET("classes")
    suspend fun listClasses(): Response<ClassesResponses>

    @GET("detail_facil")
    suspend fun listDetailFacil(): Response<DetailFacilResponses>

//    @GET("stories?location=1")
//    fun getStoriesMap(
//        @Header("Authorization") authorization: String
//    ): Call<MainResponse>

    @Multipart
    @POST("report")
    fun addStory(
        @Header("Authorization") authorization: String,
        @Part pictures: MultipartBody.Part,
        @Part ("id_student") idStudent: RequestBody,
        @Part("id_building") idBuilding: RequestBody,
        @Part("id_classes") idClasses: RequestBody,
        @Part("id_detail_facilities") idDetailFacil: RequestBody,
        @Part("description") description: RequestBody,
        @Part("lat") lat: RequestBody?,
        @Part("lon") lon: RequestBody?,
    ): Call<AddStoryResponse>
}