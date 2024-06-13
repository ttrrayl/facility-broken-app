package com.example.kumandra.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.kumandra.data.remote.ApiConfig
import com.google.android.gms.maps.model.LatLng
import com.example.kumandra.data.remote.response.AddStoryResponse
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
        id_building: RequestBody,
        id_classes: RequestBody,
        id_detailFacil: RequestBody,
        description: RequestBody,
        latLng: LatLng?
    ) {
        _isLoading.value = true
        val lat = latLng?.latitude?.toFloat()
        val lon = latLng?.longitude?.toFloat()
        val client = ApiConfig.getApiService().addStory(
            "Bearer $token",image,id_building,id_classes,id_detailFacil,description, lat, lon
        )
        client.enqueue(object : Callback<AddStoryResponse>{
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
                    if (responseBody != null && !responseBody.error) {
                        _msg.value = "Successfully upload the file"
                    }
                } else {
                    _msg.value = response.message().toString()
                }


            }
        })
    }
}