package com.example.kumandra.viewmodel

import android.util.Log
import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.kumandra.data.StoryRepository
import com.example.kumandra.data.local.UserModel
import com.example.kumandra.data.local.UserSession
import com.example.kumandra.data.remote.ApiConfig
import com.example.kumandra.data.remote.response.ListStoryItem
import kotlinx.coroutines.launch

class MainViewModel (private val storyRepository: StoryRepository,private val pref: UserSession) : ViewModel(){

//    private val _listStory = MutableLiveData<List<ListStoryItem>>()
//    val listStory: LiveData<List<ListStoryItem>> = _listStory

    private val _listStoryMap = MutableLiveData<List<ListStoryItem>>()
    val listStoryMap: LiveData<List<ListStoryItem>> = _listStoryMap

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun getToken(): LiveData<UserModel>{
        return pref.getToken().asLiveData()
    }

    fun logout(){
        viewModelScope.launch {
            pref.logout()
        }
    }

    fun getStories(token: String): LiveData<PagingData<ListStoryItem>> =
        storyRepository.getStory(token).cachedIn(viewModelScope)

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