package com.example.sicuan.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import android.content.SharedPreferences
import com.example.sicuan.domain.repository.SupabaseStorageRepository
import com.example.sicuan.domain.usecase.auth.FirebaseAuthUseCases
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File

class FirebaseAuthViewModel(
    private val firebaseAuthUseCases: FirebaseAuthUseCases,
    private val supabaseStorageRepository: SupabaseStorageRepository,
    private val sharedPreferences: SharedPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(FirebaseAuthUiState())
    val uiState = _uiState.asStateFlow()

    init {
        checkCurrentUser()
    }

    fun checkCurrentUser() {
        val currentUser = firebaseAuthUseCases.getCurrentFirebaseUser()
        if (currentUser != null) {
            val username = sharedPreferences.getString("profile_username_${currentUser.uid}", null)
            val phone = sharedPreferences.getString("profile_phone_${currentUser.uid}", null)
            val dob = sharedPreferences.getString("profile_dob_${currentUser.uid}", null)
            val bio = sharedPreferences.getString("profile_bio_${currentUser.uid}", null)

            _uiState.update {
                it.copy(
                    isLoading = false,
                    isSignedIn = true,
                    uid = currentUser.uid,
                    isAnonymous = currentUser.isAnonymous,
                    errorMessage = null,
                    displayName = currentUser.displayName,
                    photoUrl = currentUser.photoUrl,
                    username = username,
                    phone = phone,
                    dob = dob,
                    bio = bio
                )
            }
        } else {
            _uiState.update {
                it.copy(isLoading = false, isSignedIn = false)
            }
        }
    }

    fun signIn(email: String, pass: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val user = firebaseAuthUseCases.signInWithEmailAndPassword(email, pass)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isSignedIn = true,
                        uid = user.uid,
                        isAnonymous = user.isAnonymous,
                        errorMessage = null,
                        displayName = user.displayName,
                        photoUrl = user.photoUrl
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, isSignedIn = false, errorMessage = e.message)
                }
            }
        }
    }

    fun signUp(email: String, pass: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val user = firebaseAuthUseCases.signUpWithEmailAndPassword(email, pass)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isSignedIn = true,
                        uid = user.uid,
                        isAnonymous = user.isAnonymous,
                        errorMessage = null,
                        displayName = user.displayName,
                        photoUrl = user.photoUrl
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, isSignedIn = false, errorMessage = e.message)
                }
            }
        }
    }

    fun updateProfile(displayName: String?, photoUrl: String?) {
        viewModelScope.launch {
            try {
                val updatedUser = firebaseAuthUseCases.updateProfile(displayName, photoUrl)
                _uiState.update {
                    it.copy(
                        displayName = updatedUser.displayName,
                        photoUrl = updatedUser.photoUrl
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = e.message) }
            }
        }
    }

    fun updateFullProfile(
        name: String,
        username: String,
        email: String,
        phone: String,
        dob: String,
        bio: String,
        photoUrl: String? = null
    ) {
        val finalPhotoUrl = if (photoUrl == "") null else (photoUrl ?: _uiState.value.photoUrl)
        
        // Simpan username dll ke SharedPreferences
        val uid = _uiState.value.uid
        if (uid.isNotBlank()) {
            sharedPreferences.edit().apply {
                putString("profile_username_$uid", username)
                putString("profile_phone_$uid", phone)
                putString("profile_dob_$uid", dob)
                putString("profile_bio_$uid", bio)
                apply()
            }
        }

        // Perbarui Firebase Auth untuk name dan photoUrl
        updateProfile(displayName = name, photoUrl = finalPhotoUrl)

        _uiState.update {
            it.copy(
                displayName = name,
                username = username,
                email = email,
                phone = phone,
                dob = dob,
                bio = bio,
                photoUrl = finalPhotoUrl
            )
        }
    }

    fun uploadProfilePicture(file: File) {
        val uid = _uiState.value.uid
        if (uid.isBlank()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = supabaseStorageRepository.uploadProfilePicture(uid, file)
            
            result.onSuccess { publicUrl ->
                // Perbarui profil dengan URL baru
                updateProfile(displayName = _uiState.value.displayName, photoUrl = publicUrl)
                _uiState.update { it.copy(isLoading = false) }
            }.onFailure { exception ->
                _uiState.update { it.copy(isLoading = false, errorMessage = "Gagal mengunggah foto: ${exception.message}") }
            }
        }
    }

    fun signOut() {
        firebaseAuthUseCases.signOut()
        _uiState.update { FirebaseAuthUiState(isLoading = false, isSignedIn = false) }
    }
    
    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}