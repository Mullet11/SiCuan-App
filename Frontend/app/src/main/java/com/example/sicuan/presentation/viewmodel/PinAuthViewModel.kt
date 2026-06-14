package com.example.sicuan.presentation.viewmodel

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.sicuan.domain.repository.PinRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PinAuthUiState(
    val currentPinInput: String = "",
    val isSettingUpPin: Boolean = false,
    val isConfirmingPin: Boolean = false,
    val initialPinSetup: String? = null,
    val isAuthenticated: Boolean = false,
    val errorMessage: String? = null,
    val isBiometricEnabled: Boolean = false
)

class PinAuthViewModel(
    private val pinRepository: PinRepository,
    private val sharedPreferences: SharedPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(PinAuthUiState())
    val uiState: StateFlow<PinAuthUiState> = _uiState.asStateFlow()

    init {
        checkPinStatus()
    }

    private fun checkPinStatus() {
        viewModelScope.launch {
            val isPinSet = pinRepository.isPinSet()
            val biometricEnabled = sharedPreferences.getBoolean("biometric_enabled", false)
            _uiState.update {
                it.copy(
                    isSettingUpPin = !isPinSet,
                    isBiometricEnabled = biometricEnabled && isPinSet
                )
            }
        }
    }

    fun onNumberClick(number: String) {
        val currentInput = _uiState.value.currentPinInput
        if (currentInput.length < 6) {
            val newInput = currentInput + number
            _uiState.update { it.copy(currentPinInput = newInput, errorMessage = null) }
            
            if (newInput.length == 6) {
                processCompletePin(newInput)
            }
        }
    }

    fun onBackspaceClick() {
        val currentInput = _uiState.value.currentPinInput
        if (currentInput.isNotEmpty()) {
            _uiState.update { 
                it.copy(
                    currentPinInput = currentInput.dropLast(1),
                    errorMessage = null
                ) 
            }
        }
    }

    fun onBiometricSuccess() {
        _uiState.update { it.copy(isAuthenticated = true) }
    }

    private fun processCompletePin(pin: String) {
        viewModelScope.launch {
            val state = _uiState.value
            
            if (state.isSettingUpPin) {
                if (!state.isConfirmingPin) {
                    // First time entering new PIN
                    _uiState.update { 
                        it.copy(
                            currentPinInput = "",
                            isConfirmingPin = true,
                            initialPinSetup = pin
                        ) 
                    }
                } else {
                    // Confirming new PIN
                    if (pin == state.initialPinSetup) {
                        pinRepository.savePin(pin)
                        _uiState.update { it.copy(isAuthenticated = true) }
                    } else {
                        _uiState.update { 
                            it.copy(
                                currentPinInput = "",
                                errorMessage = "PIN tidak cocok. Silakan coba lagi."
                            ) 
                        }
                    }
                }
            } else {
                // Authenticating existing PIN
                val savedPin = pinRepository.getPin()
                if (pin == savedPin) {
                    _uiState.update { it.copy(isAuthenticated = true) }
                } else {
                    _uiState.update { 
                        it.copy(
                            currentPinInput = "",
                            errorMessage = "PIN salah"
                        ) 
                    }
                }
            }
        }
    }
}

class PinAuthViewModelFactory(
    private val pinRepository: PinRepository,
    private val sharedPreferences: SharedPreferences
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PinAuthViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PinAuthViewModel(pinRepository, sharedPreferences) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
