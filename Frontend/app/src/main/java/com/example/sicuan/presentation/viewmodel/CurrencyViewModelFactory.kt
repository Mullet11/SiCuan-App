package com.example.sicuan.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.sicuan.domain.usecase.currency.CurrencyUseCases

class CurrencyViewModelFactory(
    private val currencyUseCases: CurrencyUseCases
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CurrencyViewModel::class.java)) {
            return CurrencyViewModel(currencyUseCases) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
