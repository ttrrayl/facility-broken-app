package com.example.kumandra.viewmodel


import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.kumandra.data.Results
import com.example.kumandra.data.local.StudentModel
import com.example.kumandra.data.local.UserSession
import com.example.kumandra.data.remote.ApiConfig
import com.example.kumandra.data.remote.response.DetailFacilResponses
import com.example.kumandra.data.remote.response.DetailReportResponses
import kotlinx.coroutines.launch

class DetailReportViewModel (private val pref: UserSession) : ViewModel() {

    val _report = MutableLiveData<Results<DetailReportResponses>>()
    val report: LiveData<Results<DetailReportResponses>> = _report

    private suspend fun detailReport(idReport: String) {
        _report.postValue(Results.Loading())
        try {
            val response = ApiConfig.getApiService().detailReport(idReport)
            if (response.isSuccessful){
                response.body()?.let {
                    _report.postValue(Results.Success(it))
                }
            } else {
                Log.i("DetailReportViewModel", "safeGetRoles: $response")
                _report.postValue(Results.Error("Gagal memuat data"))
            }
        } catch (e: Throwable){
            _report.postValue(Results.Error(e.message.toString()))
        }
    }

    fun getDetailReport(idReport: String) = viewModelScope.launch {
        detailReport(idReport)
    }
    fun getUser(): LiveData<StudentModel> {
        return pref.getUser().asLiveData()
    }
}