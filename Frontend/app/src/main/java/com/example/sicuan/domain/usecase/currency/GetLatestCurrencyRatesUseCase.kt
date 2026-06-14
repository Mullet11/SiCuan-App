package com.example.sicuan.domain.usecase.currency

import com.example.sicuan.domain.model.CurrencyRate
import com.example.sicuan.domain.repository.CurrencyRepository

class GetLatestCurrencyRatesUseCase(
    private val repository: CurrencyRepository
) {
    suspend operator fun invoke(): List<CurrencyRate> {
        return repository.getLatestRates()
    }
}
