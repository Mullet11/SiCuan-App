package com.example.sicuan.presentation.viewmodel

import com.example.sicuan.domain.model.CurrencyRate

data class CurrencyUiState(
    val rates: List<CurrencyRate> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)