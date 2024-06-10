package com.example.kumandra.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.kumandra.data.local.BuildingModel
import com.example.kumandra.data.local.ClassesModel
import com.example.kumandra.data.remote.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ClassesViewModel: ViewModel() {
    private val _classes = MutableLiveData<List<ClassesModel>>()
    val classes: LiveData<List<ClassesModel>> get() = _classes

    private val _msg = MutableLiveData<String>()
    val msg: LiveData<String> = _msg

    fun fetchClasses() {
        val client = ApiConfig.getApiService().listClasses()
        client.enqueue(object : Callback<List<ClassesModel>> {
            override fun onResponse(call: Call<List<ClassesModel>>, response: Response<List<ClassesModel>>) {
                if (response.isSuccessful) {
                    _classes.value = response.body()
                }
            }

            override fun onFailure(call: Call<List<ClassesModel>>, t: Throwable) {
                _msg.value = t.message.toString()
            }
        })
    }
}