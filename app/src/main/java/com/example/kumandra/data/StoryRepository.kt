package com.example.kumandra.data

import androidx.lifecycle.LiveData
import androidx.paging.*
import com.example.kumandra.data.remote.ApiService
import com.example.kumandra.data.remote.response.ListStoryItem
import com.example.kumandra.data.room.StoryDatabase
import com.example.kumandra.data.room.StoryRemoteMediator

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