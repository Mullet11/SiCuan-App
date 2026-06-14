package com.example.sicuan.presentation.viewmodel

data class FirebaseAuthUiState(
    val isLoading: Boolean = true,
    val isSignedIn: Boolean = false,
    val uid: String = "",
    val isAnonymous: Boolean = false,
    val errorMessage: String? = null,
    val displayName: String? = null,
    val username: String? = null,
    val email: String? = null,
    val phone: String? = null,
    val dob: String? = null,
    val bio: String? = null,
    val photoUrl: String? = null
)
