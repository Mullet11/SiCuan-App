package com.example.sicuan.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.sicuan.domain.usecase.auth.FirebaseAuthUseCases

import android.content.SharedPreferences
import com.example.sicuan.domain.repository.SupabaseStorageRepository

class FirebaseAuthViewModelFactory(
    private val firebaseAuthUseCases: FirebaseAuthUseCases,
    private val supabaseStorageRepository: SupabaseStorageRepository,
    private val sharedPreferences: SharedPreferences
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FirebaseAuthViewModel::class.java)) {
            return FirebaseAuthViewModel(firebaseAuthUseCases, supabaseStorageRepository, sharedPreferences) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
