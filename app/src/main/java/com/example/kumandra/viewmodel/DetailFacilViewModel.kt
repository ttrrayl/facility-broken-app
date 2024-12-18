package com.example.kumandra.viewmodel

import android.util.Log
import android.widget.ArrayAdapter
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kumandra.data.ReportRepository
import com.example.kumandra.data.Results
import com.example.kumandra.data.local.ClassesModel
import com.example.kumandra.data.local.DetailFacilModel
import com.example.kumandra.data.remote.ApiConfig
import com.example.kumandra.data.remote.response.ClassesResponses
import com.example.kumandra.data.remote.response.DetailFacilResponses
import com.example.kumandra.data.remote.response.DetailFacility
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailFacilViewModel(private val reportRepository: ReportRepository): ViewModel() {
    private val _detailFacil = MutableLiveData<List<DetailFacilModel>>()
  //  val detailFacil: LiveData<List<DetailFacilModel>> get() = _detailFacil

    private val _msg = MutableLiveData<String>()
    val msg: LiveData<String> = _msg

    val _listDetailFacil = MutableLiveData<Results<List<DetailFacility>>>()
    val listFacil: LiveData<Results<List<DetailFacility>>> = _listDetailFacil

    val facil = MutableLiveData<Results<DetailFacilResponses>>()



    private suspend fun fetchDetailFacil() {
        _listDetailFacil.postValue(Results.Loading())
        try {
            val response = ApiConfig.getApiService().listDetailFacil()
            if (response.isSuccessful){
                response.body()?.let {
                    reportRepository.fetchDetailFacil(it.detail_facilities)
                    facil.postValue(Results.Success(it))
                }
            } else {
                Log.i("DetailFacilViewModel", "safeGetRoles: $response")
                _listDetailFacil.postValue(Results.Error("Gagal memuat data"))
            }
        } catch (e: Throwable){
            _listDetailFacil.postValue(Results.Error(e.message.toString()))
        }
    }

    fun getDetailFacil(idClasses: String?) = viewModelScope.launch {
        fetchDetailFacil()
        reportRepository.getAllDeFacil(idClasses).observeForever{
            _listDetailFacil.postValue(Results.Success(it))
        }
    }

//    fun fetchDetailFacil() {
//        val client = ApiConfig.getApiService().listDetailFacil()
//        client.enqueue(object : Callback<List<DetailFacilModel>> {
//            override fun onResponse(call: Call<List<DetailFacilModel>>, response: Response<List<DetailFacilModel>>) {
//                if (response.isSuccessful) {
//                    _detailFacil.value = response.body()
//                }
//            }
//
//            override fun onFailure(call: Call<List<DetailFacilModel>>, t: Throwable) {
//                _msg.value = t.message.toString()
//            }
//        })
//    }
}