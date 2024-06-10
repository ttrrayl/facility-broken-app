package com.example.storyapp.data.remote

import com.example.storyapp.data.local.BuildingModel
import com.example.storyapp.data.local.ClassesModel
import com.example.storyapp.data.local.DetailFacilModel
import com.example.storyapp.data.remote.response.AddStoryResponse
import com.example.storyapp.data.remote.response.LoginResponse
import com.example.storyapp.data.remote.response.MainResponse
import com.example.storyapp.data.remote.response.RegisterResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

//    @FormUrlEncoded
//    @POST("register")
//    fun register(
//        @Field("name") name: String,
//        @Field("email") email: String,
//        @Field("password") password: String
//    ): Call<RegisterResponse>
//
//    @FormUrlEncoded
//    @POST("login")
//    fun login(
//        @Field("email") email: String,
//        @Field("password") password: String
//    ): Call<LoginResponse>

    @GET("stories")
    suspend fun getStories(
        @Header("Authorization") authorization: String,
        @Query("page") page: Int,
        @Query("size") size: Int
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
    @POST("stories")
    fun addStory(
        @Header("Authorization") authorization: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody
    ): Call<AddStoryResponse>
}