package com.example.kumandra.viewmodel


import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.kumandra.data.local.StudentModel
import com.example.kumandra.data.local.UserSession

class DetailStoryViewModel (private val pref: UserSession) : ViewModel() {

    fun getUser(): LiveData<StudentModel> {
        return pref.getUser().asLiveData()
    }
}