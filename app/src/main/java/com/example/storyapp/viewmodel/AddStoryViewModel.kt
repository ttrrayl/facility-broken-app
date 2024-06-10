package com.example.storyapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.storyapp.data.remote.ApiConfig
import com.example.storyapp.data.remote.response.AddStoryResponse
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
        description: RequestBody
    ) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().addStory(
            "Bearer $token",image,description
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