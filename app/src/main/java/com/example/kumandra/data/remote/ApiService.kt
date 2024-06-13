package com.example.kumandra.data.remote

import com.example.kumandra.data.local.BuildingModel
import com.example.kumandra.data.local.ClassesModel
import com.example.kumandra.data.local.DetailFacilModel
import com.example.kumandra.data.remote.response.AddStoryResponse
import com.example.kumandra.data.remote.response.LoginResponse
import com.example.kumandra.data.remote.response.MainResponse
import com.example.kumandra.data.remote.response.RegisterResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
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
//        @Query("page") page: Int,
//        @Query("size") size: Int
    ): MainResponse

    @GET("building")
    fun listBuilding(): Call<List<BuildingModel>>

    @GET("classes")
    fun listClasses(): Call<List<ClassesModel>>

    @GET("detail_facil")
    fun listDetailFacil(): Call<List<DetailFacilModel>>

//    @GET("stories?location=1")
//    fun getStoriesMap(
//        @Header("Authorization") authorization: String
//    ): Call<MainResponse>

    @Multipart
    @POST("report")
    fun addStory(
        @Header("Authorization") authorization: String,
        @Part file: MultipartBody.Part,
        @Part("id_building") idBuilding: RequestBody,
        @Part("id_classes") idClasses: RequestBody,
        @Part("id_detail_facilities") idDetailFacil: RequestBody,
        @Part("description") description: RequestBody,
        @Part("lat") lat: Float?,
        @Part("lon") lon: Float?,
    ): Call<AddStoryResponse>
}