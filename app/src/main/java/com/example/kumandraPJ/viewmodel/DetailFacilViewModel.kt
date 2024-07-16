package com.example.kumandraPJ.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kumandraPJ.data.Results
import com.example.kumandraPJ.data.local.DetailFacilModel
import com.example.kumandraPJ.data.remote.ApiConfig
import com.example.kumandraPJ.data.remote.response.DetailFacilResponses
import kotlinx.coroutines.launch

class DetailFacilViewModel: ViewModel() {
    private val _detailFacil = MutableLiveData<List<DetailFacilModel>>()
  //  val detailFacil: LiveData<List<DetailFacilModel>> get() = _detailFacil

    private val _msg = MutableLiveData<String>()
    val msg: LiveData<String> = _msg

    val detailFacil = MutableLiveData<Results<DetailFacilResponses>>()

    private suspend fun fetchDetailFacil() {
        detailFacil.postValue(Results.Loading())
        try {
            val response = ApiConfig.getApiService().listDetailFacil()
            if (response.isSuccessful){
                response.body()?.let {
                    detailFacil.postValue(Results.Success(it))
                }
            } else {
                Log.i("DetailFacilViewModel", "safeGetRoles: $response")
                detailFacil.postValue(Results.Error("Gagal memuat data"))
            }
        } catch (e: Throwable){
            detailFacil.postValue(Results.Error(e.message.toString()))
        }
    }

    fun getDetailFacil() = viewModelScope.launch {
        fetchDetailFacil()
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