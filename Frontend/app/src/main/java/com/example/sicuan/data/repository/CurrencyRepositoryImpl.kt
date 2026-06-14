package com.example.sicuan.data.repository

import com.example.sicuan.data.mapper.toDomain
import com.example.sicuan.data.remote.api.CurrencyApiService
import com.example.sicuan.domain.model.CurrencyRate
import com.example.sicuan.domain.repository.CurrencyRepository

class CurrencyRepositoryImpl(
    private val currencyApiService: CurrencyApiService
) : CurrencyRepository {

    override suspend fun getLatestRates(): List<CurrencyRate> {
        return currencyApiService.getLatestRates().map { dto ->
            dto.toDomain()
        }
    }
}
