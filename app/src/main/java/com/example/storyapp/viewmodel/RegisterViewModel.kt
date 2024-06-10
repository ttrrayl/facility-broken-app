package com.example.storyapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storyapp.data.local.UserModel
import com.example.storyapp.data.local.UserSession
import com.example.storyapp.data.remote.ApiConfig
import com.example.storyapp.data.remote.response.LoginResponse
import com.example.storyapp.data.remote.response.RegisterResponse
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterViewModel(private val pref: UserSession) : ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _msg = MutableLiveData<String>()
    val msg: LiveData<String> = _msg

    private val _isError = MutableLiveData<Boolean>()
    val isError: LiveData<Boolean> = _isError

    fun register(name: String, email: String, password: String) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().register(name, email, password)
        client.enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val responBody = response.body()
                    if (responBody != null && !responBody.error){
                        _msg.value = "Daftar Berhasil"
                        viewModelScope.launch {
                            pref.saveToken(
                                UserModel(false,"")
                            )
                        }
                        _isError.value = false
                    } else {
                        _isError.value = true
                    }
                } else {
                    val responBody = Gson().fromJson(
                        response.errorBody()?.charStream(),RegisterResponse::class.java
                    )
                    _msg.value = responBody.message
                    _isError.value = true
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                _isLoading.value = false
                _msg.value = t.message.toString()
            }
        })
    }
}