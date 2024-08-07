package com.example.kumandra.viewmodel


import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.kumandra.data.Results
import com.example.kumandra.data.local.StudentModel
import com.example.kumandra.data.local.UserModel
import com.example.kumandra.data.local.UserSession
import com.example.kumandra.data.remote.ApiConfig
import com.example.kumandra.data.remote.response.DeleteResponses
import com.example.kumandra.data.remote.response.DetailFacilResponses
import com.example.kumandra.data.remote.response.DetailReportResponses
import com.example.kumandra.data.remote.response.RegisterResponse
import com.example.kumandra.data.remote.response.UpdateResponses
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailReportViewModel (private val pref: UserSession) : ViewModel() {

    val _report = MutableLiveData<Results<DetailReportResponses>>()
    val report: LiveData<Results<DetailReportResponses>> = _report

    val _delreport = MutableLiveData<Results<DeleteResponses>>()
    val delReport: LiveData<Results<DeleteResponses>> = _delreport

    val _upreport = MutableLiveData<Results<UpdateResponses>>()
    val upReport: LiveData<Results<UpdateResponses>> = _upreport

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _msg = MutableLiveData<String>()
    val msg: LiveData<String> = _msg

    private suspend fun detailReport(idReport: String) {
        _report.postValue(Results.Loading())
        try {
            val response = ApiConfig.getApiService().detailReport(idReport)
            if (response.isSuccessful) {
                response.body()?.let {
                    _report.postValue(Results.Success(it))
                }
            } else {
                Log.i("DetailReportViewModel", "safeGetRoles: $response")
                _report.postValue(Results.Error("Gagal memuat data"))
            }
        } catch (e: Throwable) {
            _report.postValue(Results.Error(e.message.toString()))
        }
    }

    fun getDetailReport(idReport: String) = viewModelScope.launch {
        detailReport(idReport)
    }

    fun deleteReport(idReport: String?) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().deleteReport(idReport)
        client.enqueue(object : Callback<DeleteResponses> {
            override fun onResponse(
                call: Call<DeleteResponses>,
                response: Response<DeleteResponses>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val responBody = response.body()
                    if (responBody != null) {
                        _delreport.postValue(Results.Success(responBody))
                    } else {
                        _delreport.postValue(Results.Error("Gagal menghapus data"))
                    }
                } else {
                    val responBody = Gson().fromJson(
                        response.errorBody()?.charStream(), DeleteResponses::class.java
                    )
                    _delreport.postValue(Results.Error(responBody.message))
                }
            }

            override fun onFailure(call: Call<DeleteResponses>, t: Throwable) {
                _isLoading.value = false
            }
        })
    }

    fun getUser(): LiveData<StudentModel> {
        return pref.getUser().asLiveData()
    }
}