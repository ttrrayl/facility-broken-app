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

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

//    private val _statusId = MutableLiveData<String>()
//    val reports: LiveData<PagingData<Report>> = _statusId.switchMap { statusId ->
//        getReport("", "", statusId)
//    }



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

    fun getStories(token: String, idStudent: Int?, idStatus: String?): LiveData<PagingData<Report>>{
           return reportRepository.getReport(token, idStudent, idStatus).cachedIn(viewModelScope)
    }

//    fun getStoriesMap(token: String) {
//        _isLoading.value = true
//        val client = ApiConfig.getApiService().getStoriesMap("Bearer $token")
//        client.enqueue(object : Callback<MainResponse>{
//            override fun onFailure(call: Call<MainResponse>, t: Throwable) {
//                _isLoading.value = false
//                Log.e(TAG,"onFailure: ${t.message}")
//            }
//
//            override fun onResponse(call: Call<MainResponse>, response: Response<MainResponse>) {
//                _isLoading.value = false
//                if (response.isSuccessful){
//                    val responBody = response.body()
//                    if (responBody != null){
//                        _listStoryMap.value = responBody.listStory
//                    }
//                } else{
//                    Log.e(TAG,"onFailure: ${response.message()}")
//                }
//            }
//        })
//    }

    companion object{
        private const val TAG = "MainViewModel"
    }


}