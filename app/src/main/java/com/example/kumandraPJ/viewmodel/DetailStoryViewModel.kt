package com.example.kumandraPJ.viewmodel


import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.kumandraPJ.data.local.PjModel
import com.example.kumandraPJ.data.local.UserSession

class DetailStoryViewModel (private val pref: UserSession) : ViewModel() {

    fun getUser(): LiveData<PjModel> {
        return pref.getUser().asLiveData()
    }

}