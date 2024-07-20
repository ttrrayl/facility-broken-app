package com.example.kumandraPJ.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.kumandraPJ.data.Results
import com.example.kumandraPJ.data.local.PjModel
import com.example.kumandraPJ.data.local.UserModel
import com.example.kumandraPJ.data.local.UserSession
import com.example.kumandraPJ.data.remote.ApiConfig
import com.example.kumandraPJ.data.remote.response.DetailFacilResponses
import com.example.kumandraPJ.data.remote.response.SaveTokenResponses
import kotlinx.coroutines.launch

class FcmViewModel(private val pref: UserSession): ViewModel() {

    val fcm = MutableLiveData<Results<SaveTokenResponses>>()

    fun getToken(): LiveData<UserModel> {
        return pref.getToken().asLiveData()
    }

    fun getUser(): LiveData<PjModel> {
        return pref.getUser().asLiveData()
    }

    fun addFcmToken(idUser: String, idRole: String, fcmToken: String){
        viewModelScope.launch {
            try {
                val response =ApiConfig.getApiService().addFcmToken(idUser, idRole, fcmToken)
                if (response.isSuccessful){
                    response.body()?.let {
                        Log.i("FcmViewModel", "fcm: $response")
                    }
                } else{
                    Log.i("FcmViewModel", "fcm: $response")
                }

            } catch (e: Throwable){
                Log.i("FcmViewModel", e.message.toString())
            }
        }

    }
}