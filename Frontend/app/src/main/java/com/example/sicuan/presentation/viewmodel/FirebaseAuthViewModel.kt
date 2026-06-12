package com.example.sicuan.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sicuan.domain.usecase.auth.FirebaseAuthUseCases
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FirebaseAuthViewModel(
    private val firebaseAuthUseCases: FirebaseAuthUseCases
) : ViewModel() {

    private val _uiState = MutableStateFlow(FirebaseAuthUiState())
    val uiState = _uiState.asStateFlow()

    init {
        signInAnonymouslyIfNeeded()
    }

    fun signInAnonymouslyIfNeeded() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null
                )
            }

            try {
                val currentUser = firebaseAuthUseCases.getCurrentFirebaseUser()
                val user = currentUser ?: firebaseAuthUseCases.signInAnonymously()

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isSignedIn = true,
                        uid = user.uid,
                        isAnonymous = user.isAnonymous,
                        errorMessage = null
                    )
                }
            } catch (error: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isSignedIn = false,
                        uid = "",
                        isAnonymous = false,
                        errorMessage = error.message ?: "Gagal login anonymous ke Firebase"
                    )
                }
            }
        }
    }
}