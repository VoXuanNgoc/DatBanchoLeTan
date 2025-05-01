package com.example.datban.model

//data class UserModel(
//    val name: String? = null,
//    val email: String? = null,
//    val password: String? = null,
//
//    )
data class UserModel(
    val name: String? = null,
    val email: String? = null,
    val password: String? = null,
    val role: String = "user" // Thêm trường role với giá trị mặc định
)
