package com.example.kumandra.data.local

data class UserModel (
    val isLogin: Boolean,
    val token: String = ""
        )

data class StudentModel(
    val idStudent: Int,
    val username: String
)