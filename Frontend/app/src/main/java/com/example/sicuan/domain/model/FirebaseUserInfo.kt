package com.example.sicuan.domain.model

data class FirebaseUserInfo(
    val uid: String,
    val isAnonymous: Boolean,
    val email: String? = null
)