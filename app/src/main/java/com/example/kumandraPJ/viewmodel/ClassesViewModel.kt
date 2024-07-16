package com.example.kumandraPJ.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kumandraPJ.data.Results
import com.example.kumandraPJ.data.local.ClassesModel
import com.example.kumandraPJ.data.remote.ApiConfig
import com.example.kumandraPJ.data.remote.response.ClassesResponses
import kotlinx.coroutines.launch

class ClassesViewModel: ViewModel() {
    private val _classes = MutableLiveData<List<ClassesModel>>()
    //val classes: LiveData<List<ClassesModel>> get() = _classes

    private val _msg = MutableLiveData<String>()
    val msg: LiveData<String> = _msg

    val classes = MutableLiveData<Results<ClassesResponses>>()

    private suspend fun fetchClasses() {
        classes.postValue(Results.Loading())
        try {
            val response = ApiConfig.getApiService().listClasses()
            if (response.isSuccessful){
                response.body()?.let {
                    classes.postValue(Results.Success(it))
                }
            } else {
                Log.i("ClassesViewModel", "safeGetRoles: $response")
                classes.postValue(Results.Error("Gagal memuat data"))
            }
        } catch (e: Throwable){
            classes.postValue(Results.Error(e.message.toString()))
        }
    }

    fun getClasses() = viewModelScope.launch {
        fetchClasses()
    }

//    fun fetchClasses() {
//        val client = ApiConfig.getApiService().listClasses()
//        client.enqueue(object : Callback<List<ClassesModel>> {
//            override fun onResponse(call: Call<List<ClassesModel>>, response: Response<List<ClassesModel>>) {
//                if (response.isSuccessful) {
//                    _classes.value = response.body()
//                }
//            }
//
//            override fun onFailure(call: Call<List<ClassesModel>>, t: Throwable) {
//                _msg.value = t.message.toString()
//            }
//        })
//    }
}