package com.example.kumandraPJ.data.local

data class UserModel (
    val isLogin: Boolean,
    val token: String = ""
)

data class PjModel(
    val idPj: String,
    val username: String
)