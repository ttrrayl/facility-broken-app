package com.example.storyapp.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.paging.*
import com.example.storyapp.data.remote.ApiConfig
import com.example.storyapp.data.remote.ApiService
import com.example.storyapp.data.remote.response.AddStoryResponse
import com.example.storyapp.data.remote.response.ListStoryItem
import com.example.storyapp.data.remote.response.MainResponse
import com.example.storyapp.data.room.StoryDatabase
import com.example.storyapp.data.room.StoryRemoteMediator
import com.example.storyapp.viewmodel.MainViewModel
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StoryRepository(
    private val storyDatabase: StoryDatabase,
    private val apiService: ApiService
){
    @OptIn(ExperimentalPagingApi::class)
    fun getStory(token: String): LiveData<PagingData<ListStoryItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            remoteMediator = StoryRemoteMediator(token, storyDatabase, apiService),
            pagingSourceFactory = {
                storyDatabase.storyDao().getAllStory()
            }
        ).liveData
    }
}