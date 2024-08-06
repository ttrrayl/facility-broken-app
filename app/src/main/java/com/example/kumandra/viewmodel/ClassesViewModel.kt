package com.example.kumandra.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kumandra.data.ReportRepository
import com.example.kumandra.data.Results
import com.example.kumandra.data.local.ClassesModel
import com.example.kumandra.data.remote.ApiConfig
import com.example.kumandra.data.remote.response.BuildingResponses
import com.example.kumandra.data.remote.response.Classes
import com.example.kumandra.data.remote.response.ClassesResponses
import com.example.kumandra.data.remote.response.DetailFacility
import kotlinx.coroutines.launch

class ClassesViewModel(private val reportRepository: ReportRepository): ViewModel() {
  //  private val _classes = MutableLiveData<List<ClassesModel>>()
    //val classes: LiveData<List<ClassesModel>> get() = _classes

    private val _msg = MutableLiveData<String>()
    val msg: LiveData<String> = _msg

    val _listClasses = MutableLiveData<Results<List<Classes>>>()
    val listClasses: LiveData<Results<List<Classes>>> = _listClasses

//    val _classes = MutableLiveData<Results<ClassesResponses>>()
//    val classes: LiveData<Results<ClassesResponses>> = _classes

    val classes = MutableLiveData<Results<ClassesResponses>>()


    private suspend fun fetchClasses() {
        classes.postValue(Results.Loading())
        try {
            val response = ApiConfig.getApiService().listClasses()
            if (response.isSuccessful){
                response.body()?.let {
                    reportRepository.fetchClasses(it.classes)
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

    fun getClasses(idBuilding: String?) = viewModelScope.launch {
        fetchClasses()
        reportRepository.getAllClasses(idBuilding).observeForever{
            _listClasses .postValue(Results.Success(it))
        }
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