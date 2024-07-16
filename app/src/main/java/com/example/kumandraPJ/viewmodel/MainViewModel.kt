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

    fun getStories(token: String, idPj: String): LiveData<PagingData<Report>> =
        reportRepository.getReport(token, idPj).cachedIn(viewModelScope)

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