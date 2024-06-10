package com.example.kumandra.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.kumandra.data.local.BuildingModel
import com.example.kumandra.data.remote.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BuildingViewModel: ViewModel() {
    private val _building = MutableLiveData<List<BuildingModel>>()
    val building: LiveData<List<BuildingModel>> get() = _building

    private val _msg = MutableLiveData<String>()
    val msg: LiveData<String> = _msg

    fun fetchBuildings() {
        val client = ApiConfig.getApiService().listBuilding()
        client.enqueue(object : Callback<List<BuildingModel>> {
            override fun onResponse(call: Call<List<BuildingModel>>, response: Response<List<BuildingModel>>) {
                if (response.isSuccessful) {
                    _building.value = response.body()
                }
            }

            override fun onFailure(call: Call<List<BuildingModel>>, t: Throwable) {
                _msg.value = t.message.toString()
            }
        })
    }
}