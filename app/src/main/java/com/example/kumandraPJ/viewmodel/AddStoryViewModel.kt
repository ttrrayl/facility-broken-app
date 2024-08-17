package com.example.kumandraPJ.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.kumandraPJ.data.remote.ApiConfig
import com.example.kumandraPJ.data.remote.response.AddStoryResponse
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
    fun sendResponse(
        token: String,
        image_process: MultipartBody.Part?,
        image_done: MultipartBody.Part?,
        id_report: RequestBody,
        id_pj: RequestBody,
        id_status: RequestBody,
        level: RequestBody,
        content: RequestBody,
        lat: RequestBody?,
        lon: RequestBody?
        ) {
        _isLoading.value = true
        try {
            val client = ApiConfig.getApiService().addResponse(
                "Bearer $token",
                image_process,
                image_done,
                id_report,
                id_pj,
                id_status,
                level,
                content,
                lat,
                lon)
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
                            _msg.value = responseBody.message
                        }
                    } else {
                        _msg.value = "gagal"
                        Log.i("AddResponse", "tidak masuk")
                    }
                }
            })
        } catch ( e: Throwable){
            _isLoading.value = false
            Log.d("Add Story View Model", e.message.toString())
        }
    }
}