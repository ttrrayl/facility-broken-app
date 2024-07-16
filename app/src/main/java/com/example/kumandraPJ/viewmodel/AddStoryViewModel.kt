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
//            val lat = latLng?.latitude?.toFloat()
//            val lon = latLng?.longitude?.toFloat()
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
                            _msg.value = "Successfully upload the file"
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

//    fun submit(
//        token: String,
//        image: MultipartBody.Part,
//        id_building: RequestBody,
//        id_classes: RequestBody,
//        id_detailFacil: RequestBody,
//        description: RequestBody,
//        latLng: LatLng?
//    ) = viewModelScope.launch {
//        uploadStory("Bearer $token",image,id_building,id_classes,id_detailFacil,description, latLng)
//    }
}