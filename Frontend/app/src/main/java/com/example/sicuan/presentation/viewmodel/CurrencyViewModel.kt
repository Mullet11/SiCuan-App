package com.example.sicuan.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sicuan.domain.usecase.currency.CurrencyUseCases
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException

class CurrencyViewModel(
    private val currencyUseCases: CurrencyUseCases
) : ViewModel() {

    private val _uiState = MutableStateFlow(CurrencyUiState())
    val uiState = _uiState.asStateFlow()

    init {
        fetchLatestRates()
    }

    fun fetchLatestRates() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null
                )
            }

            try {
                val rates = currencyUseCases.getLatestCurrencyRates()

                _uiState.update {
                    it.copy(
                        rates = rates,
                        isLoading = false,
                        errorMessage = null
                    )
                }
            } catch (error: IOException) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Gagal terhubung ke internet. Periksa koneksi lalu coba lagi."
                    )
                }
            } catch (error: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Gagal mengambil data kurs mata uang"
                    )
                }
            }
        }
    }
}
