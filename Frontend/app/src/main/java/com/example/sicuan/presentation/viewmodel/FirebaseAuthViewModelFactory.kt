package com.example.sicuan.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.sicuan.domain.usecase.auth.FirebaseAuthUseCases

class FirebaseAuthViewModelFactory(
    private val firebaseAuthUseCases: FirebaseAuthUseCases
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FirebaseAuthViewModel::class.java)) {
            return FirebaseAuthViewModel(firebaseAuthUseCases) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}