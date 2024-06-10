package com.example.storyapp.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit

class UserSession private constructor(private val dataStore: DataStore<Preferences>) {

    fun getToken(): Flow<UserModel> {
        return dataStore.data.map { preferences ->
            UserModel(
                preferences[STATE] ?: false,
                preferences[TOKEN] ?:""
            )
        }
    }

    suspend fun saveToken(user: UserModel) {
        dataStore.edit { preferences ->
            preferences[STATE] = user.isLogin
            preferences[TOKEN] = user.token
        }
    }

    suspend fun login(token: String){
        dataStore.edit { preferences ->
            preferences[STATE] = true
            preferences[TOKEN] = token
        }
    }

    suspend fun logout(){
        dataStore.edit { preferences ->
            preferences[STATE] = false
            preferences[TOKEN] = ""
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: UserSession? = null

        fun getInstance(dataStore: DataStore<Preferences>): UserSession {
            return INSTANCE ?: synchronized(this) {
                val instance = UserSession(dataStore)
                INSTANCE = instance
                instance
            }
        }

        private val STATE = booleanPreferencesKey("state")
        private val TOKEN = stringPreferencesKey("token")
    }
}