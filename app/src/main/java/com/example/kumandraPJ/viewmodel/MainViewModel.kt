package com.example.kumandraPJ.viewmodel

import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.kumandraPJ.data.ReportRepository
import com.example.kumandraPJ.data.local.PjModel
import com.example.kumandraPJ.data.local.UserModel
import com.example.kumandraPJ.data.local.UserSession
import com.example.kumandraPJ.data.remote.response.ListStoryItem
import com.example.kumandraPJ.data.remote.response.Report
import kotlinx.coroutines.launch

class MainViewModel (private val reportRepository: ReportRepository, private val pref: UserSession) : ViewModel(){

//    private val _listStory = MutableLiveData<List<ListStoryItem>>()
//    val listStory: LiveData<List<ListStoryItem>> = _listStory

    private val _listStoryMap = MutableLiveData<List<ListStoryItem>>()
    val listStoryMap: LiveData<List<ListStoryItem>> = _listStoryMap

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun getToken(): LiveData<UserModel>{
        return pref.getToken().asLiveData()
    }

    fun getUser(): LiveData<PjModel>{
        return pref.getUser().asLiveData()
    }

    fun logout(){
        viewModelScope.launch {
            pref.logout()
        }
    }

    fun getStories(token: String, idPj: String,idStatus: String? ): LiveData<PagingData<Report>> =
        reportRepository.getReport(token, idPj, idStatus).cachedIn(viewModelScope)

    fun getTotalReports(idStatus: String?): LiveData<Int> = reportRepository.getTotalReports(idStatus)

    companion object{
        private const val TAG = "MainViewModel"
    }
}