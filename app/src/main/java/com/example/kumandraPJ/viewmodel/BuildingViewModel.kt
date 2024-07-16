package com.example.kumandraPJ.viewmodel

import android.util.Log
import android.widget.ArrayAdapter
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kumandraPJ.data.Results
import com.example.kumandraPJ.data.remote.ApiConfig
import com.example.kumandraPJ.data.remote.response.Building
import com.example.kumandraPJ.data.remote.response.BuildingResponses
import kotlinx.coroutines.launch

class BuildingViewModel: ViewModel() {
    private val _building = MutableLiveData<List<Building>>()
    val building: LiveData<List<Building>> get() = _building

    private val _msg = MutableLiveData<String>()
    val msg: LiveData<String> = _msg

    val builds = MutableLiveData<Results<BuildingResponses>>()

    private lateinit var adapter: ArrayAdapter<String>

    private suspend fun fetchBuildings() {
        builds.postValue(Results.Loading())
        try {
            val response = ApiConfig.getApiService().listBuilding()
            if (response.isSuccessful){
                response.body()?.let{
                    builds.postValue(Results.Success(it))
                    _building.postValue(response.body()!!.building)
                }
            } else {
                Log.i("BuildingViewModel", "safeGetRoles: $response")
                builds.postValue(Results.Error("Gagal memuat data"))
            }
        } catch (e: Throwable){
            builds.postValue(Results.Error(e.message.toString()))
        }

//        val client = ApiConfig.getApiService().listBuilding()
//        client.enqueue(object : Callback<List<BuildingModel>> {
//            override fun onResponse(call: Call<List<BuildingModel>>, response: Response<List<BuildingModel>>) {
//                if (response.isSuccessful) {
//                    _building.value = response.body()
//                }
//            }
//
//            override fun onFailure(call: Call<List<BuildingModel>>, t: Throwable) {
//                _msg.value = t.message.toString()
//            }
//        })
    }

    fun getBuilding() = viewModelScope.launch {
        fetchBuildings()
    }
}