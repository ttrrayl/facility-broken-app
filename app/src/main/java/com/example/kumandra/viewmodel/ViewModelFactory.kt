package com.example.kumandra.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.kumandra.data.local.UserSession
import com.example.kumandra.di.Injection

class ViewModelFactory (private val context: Context, private val pref: UserSession) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(pref) as T
            }
            modelClass.isAssignableFrom(RegisterViewModel::class.java) -> {
                RegisterViewModel(pref) as T
            }
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(Injection.provideRepository(context), pref) as T
            }
            modelClass.isAssignableFrom(AddStoryViewModel::class.java) -> {
                AddStoryViewModel() as T
            }
            modelClass.isAssignableFrom(BuildingViewModel::class.java) -> {
                BuildingViewModel() as T
            }
            modelClass.isAssignableFrom(ClassesViewModel::class.java) -> {
                ClassesViewModel() as T
            }
            modelClass.isAssignableFrom(DetailFacilViewModel::class.java) -> {
                DetailFacilViewModel() as T
            }
            modelClass.isAssignableFrom(DetailReportViewModel::class.java) -> {
                DetailReportViewModel(pref) as T
            }
            else -> throw java.lang.IllegalArgumentException("Uknown Viewmodel Class: " + modelClass.name)
        }
    }

//    companion object{
//        @Volatile
//        private var instance: ViewModelFactory? = null
//
//        @JvmStatic
//        fun getInstance(context: Context): ViewModelFactory =
//            instance ?: synchronized(this) {
//                instance ?: ViewModelFactory(context)
//            }.also { instance = it }
//    }
}