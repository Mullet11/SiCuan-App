package com.example.sicuan.presentation.viewmodel

data class FirebaseAuthUiState(
    val isLoading: Boolean = true,
    val isSignedIn: Boolean = false,
    val uid: String = "",
    val isAnonymous: Boolean = false,
    val errorMessage: String? = null
)