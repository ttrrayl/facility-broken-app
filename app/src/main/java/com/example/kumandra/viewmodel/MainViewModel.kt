package com.example.kumandra.viewmodel

import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.kumandra.data.ReportRepository
import com.example.kumandra.data.local.StudentModel
import com.example.kumandra.data.local.UserModel
import com.example.kumandra.data.local.UserSession
import com.example.kumandra.data.remote.response.ListStoryItem
import com.example.kumandra.data.remote.response.Report
import kotlinx.coroutines.launch

class MainViewModel (private val reportRepository: ReportRepository, private val pref: UserSession) : ViewModel(){

//    private val _listStory = MutableLiveData<List<ListStoryItem>>()
//    val listStory: LiveData<List<ListStoryItem>> = _listStory

    private val _listStoryMap = MutableLiveData<List<ListStoryItem>>()
    val listStoryMap: LiveData<List<ListStoryItem>> = _listStoryMap



    fun getToken(): LiveData<UserModel>{
        return pref.getToken().asLiveData()
    }

    fun getUser(): LiveData<StudentModel>{
        return pref.getUser().asLiveData()
    }

    fun logout(){
        viewModelScope.launch {
            pref.logout()
        }
    }

    fun getStories(token: String, idStudent: String?, idStatus: String?): LiveData<PagingData<Report>>{
           return reportRepository.getReport(token, idStudent, idStatus).cachedIn(viewModelScope)
    }

    fun getTotalReports(idStudent: String,idStatus: String?): LiveData<Int> {
       return reportRepository.getTotalReports(idStudent, idStatus)
    }

    companion object{
        private const val TAG = "MainViewModel"
    }


}