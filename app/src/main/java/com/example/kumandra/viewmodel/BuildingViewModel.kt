package com.example.kumandra.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kumandra.data.ReportRepository
import com.example.kumandra.data.Results
import com.example.kumandra.data.remote.ApiConfig
import com.example.kumandra.data.remote.response.Building
import com.example.kumandra.data.remote.response.BuildingResponses
import kotlinx.coroutines.launch

class BuildingViewModel(private val reportRepository: ReportRepository): ViewModel() {
    private val _building = MutableLiveData<List<Building>>()
    val building: LiveData<List<Building>> get() = _building

    private val _msg = MutableLiveData<String>()
    val msg: LiveData<String> = _msg

    val builds = MutableLiveData<Results<BuildingResponses>>()

    private suspend fun fetchBuildings() {
        builds.postValue(Results.Loading())
        try {
            val response = ApiConfig.getApiService().listBuilding()
            if (response.isSuccessful){
                response.body()?.let{
                    builds.postValue(Results.Success(it))
                    _building.postValue(response.body()!!.building)
                    reportRepository.fetchBuilding(it.building)
                }
            } else {
                Log.i("BuildingViewModel", "safeGetRoles: $response")
                builds.postValue(Results.Error("Gagal memuat data"))
            }
        } catch (e: Throwable){
            builds.postValue(Results.Error(e.message.toString()))
        }
    }

    fun getBuilding() = viewModelScope.launch {
        fetchBuildings()
      //  reportRepository.getAllBuilding()
    }
}