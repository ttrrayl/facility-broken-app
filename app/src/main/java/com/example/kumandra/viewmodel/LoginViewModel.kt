package com.example.kumandra.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.kumandra.data.local.StudentModel
import com.example.kumandra.data.local.UserModel
import com.example.kumandra.data.local.UserSession
import com.example.kumandra.data.remote.ApiConfig
import com.example.kumandra.data.remote.response.LoginResponse
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel(private val pref: UserSession) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _msg = MutableLiveData<String>()
    val msg: LiveData<String> = _msg

    private val _isLogin = MutableLiveData<Boolean>()
    val isLogin: LiveData<Boolean> = _isLogin

    fun getToken(): LiveData<UserModel> {
        return pref.getToken().asLiveData()
    }

    fun getUser(): LiveData<StudentModel>{
        return pref.getUser().asLiveData()
    }

    fun authenticate(email: String, password: String) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().login(email,password)
        client.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val responBody = response.body()
                    if (responBody != null && !responBody.error){
                        _isLogin.value = true
                        viewModelScope.launch {
                            if (getToken().value == null) pref.saveToken(
                                UserModel(
                                    true,
                                    responBody.loginResult.token
                                )
                            )
                            pref.saveUser(
                                StudentModel(
                                    responBody.loginResult.idStudent,
                                    responBody.loginResult.username
                                )
                            )
                            Log.i("ID VM", "id: ${responBody.loginResult.idStudent}")
                            pref.login(responBody.loginResult.token)
                        }
                    } else {
                        _msg.value = responBody?.message
                        _isLogin.value = false
                    }
                } else {
                    val responBody = Gson().fromJson(
                        response.errorBody()?.charStream(), LoginResponse::class.java
                    )
                    _msg.value = responBody.message
                    _isLogin.value = false
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                _isLoading.value = false
                _isLogin.value = false
                Log.e("LoginViewModel","onFailure: ${t.message}")
            }
        })
    }
}