package com.example.kumandra.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.kumandra.data.local.ClassesModel
import com.example.kumandra.data.local.DetailFacilModel
import com.example.kumandra.data.remote.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailFacilViewModel: ViewModel() {
    private val _detailFacil = MutableLiveData<List<DetailFacilModel>>()
    val detailFacil: LiveData<List<DetailFacilModel>> get() = _detailFacil

    private val _msg = MutableLiveData<String>()
    val msg: LiveData<String> = _msg

    fun fetchDetailFacil() {
        val client = ApiConfig.getApiService().listDetailFacil()
        client.enqueue(object : Callback<List<DetailFacilModel>> {
            override fun onResponse(call: Call<List<DetailFacilModel>>, response: Response<List<DetailFacilModel>>) {
                if (response.isSuccessful) {
                    _detailFacil.value = response.body()
                }
            }

            override fun onFailure(call: Call<List<DetailFacilModel>>, t: Throwable) {
                _msg.value = t.message.toString()
            }
        })
    }
}