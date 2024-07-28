package com.example.kumandra.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.kumandra.data.remote.ApiConfig
import com.example.kumandra.data.remote.response.AddStoryResponse
import com.example.kumandra.data.remote.response.UpdateResponses
import retrofit2.Callback
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response

class AddStoryViewModel : ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _msg = MutableLiveData<String>()
    val msg: LiveData<String> = _msg

    fun uploadStory(
        token: String,
        image: MultipartBody.Part,
        id_student: RequestBody,
        id_building: RequestBody,
        id_classes: RequestBody,
        id_detailFacil: RequestBody,
        description: RequestBody,
        lat: RequestBody?,
        lon: RequestBody?
    ) {
        _isLoading.value = true
        try {
            val client = ApiConfig.getApiService().addStory(
                "Bearer $token",
                image,
                id_student,
                id_building,
                id_classes,
                id_detailFacil,
                description,
                lat,
                lon
            )
            client.enqueue(object : Callback<AddStoryResponse> {
                override fun onFailure(call: Call<AddStoryResponse>, t: Throwable) {
                    _isLoading.value = false
                    _msg.value = t.message.toString()
                }

                override fun onResponse(
                    call: Call<AddStoryResponse>,
                    response: Response<AddStoryResponse>
                ) {
                    _isLoading.value = false
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        if (responseBody != null) {
                            _msg.value = "Successfully upload the report"
                        }
                    } else {
                        _msg.value = "gagal"
                        Log.i("AddReport", "tidak masuk")
                    }


                }
            })
        } catch ( e: Throwable){
            _isLoading.value = false
            Log.d("Add Story View Model", e.message.toString())

        }
    }

    fun updateReport(
        token: String,
        id_report: String,
        image: MultipartBody.Part?,
        id_building: RequestBody,
        id_classes: RequestBody,
        id_detailFacil: RequestBody,
        description: RequestBody
    ) {
        _isLoading.value = true
        try {
            val client = ApiConfig.getApiService().updateReport(
                "Bearer $token",
                id_report,
                image,
                id_building,
                id_classes,
                id_detailFacil,
                description
                )
            client.enqueue(object : Callback<UpdateResponses> {
                override fun onFailure(call: Call<UpdateResponses>, t: Throwable) {
                    _isLoading.value = false
                    _msg.value = t.message.toString()
                }

                override fun onResponse(
                    call: Call<UpdateResponses>,
                    response: Response<UpdateResponses>
                ) {
                    _isLoading.value = false
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        if (responseBody != null) {
                            _msg.value = "Successfully update the report"
                        }
                    } else {
                        _msg.value = "gagal"
                        Log.i("AddReport", "tidak masuk")
                    }


                }
            })
        } catch ( e: Throwable){
            _isLoading.value = false
            Log.d("Add Story View Model", e.message.toString())

        }
    }

}