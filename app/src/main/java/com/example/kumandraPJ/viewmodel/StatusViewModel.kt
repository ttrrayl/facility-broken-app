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
import com.example.kumandraPJ.data.remote.response.StatusResponses
import kotlinx.coroutines.launch

class StatusViewModel: ViewModel() {
    private val _building = MutableLiveData<List<Building>>()
    val building: LiveData<List<Building>> get() = _building

    private val _msg = MutableLiveData<String>()
    val msg: LiveData<String> = _msg

    val status = MutableLiveData<Results<StatusResponses>>()

    private lateinit var adapter: ArrayAdapter<String>

    private suspend fun fetchStatus() {
        status.postValue(Results.Loading())
        try {
            val response = ApiConfig.getApiService().listStatus()
            if (response.isSuccessful){
                response.body()?.let{
                    status.postValue(Results.Success(it))
                }
            } else {
                Log.i("StatusViewModel", "safeGetRoles: $response")
                status.postValue(Results.Error("Gagal memuat data"))
            }
        } catch (e: Throwable){
            status.postValue(Results.Error(e.message.toString()))
        }
    }

    fun getStatus() = viewModelScope.launch {
        fetchStatus()
    }
}