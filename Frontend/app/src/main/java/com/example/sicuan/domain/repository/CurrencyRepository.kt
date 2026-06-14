package com.example.sicuan.domain.repository

import com.example.sicuan.domain.model.CurrencyRate

interface CurrencyRepository {

    suspend fun getLatestRates(): List<CurrencyRate>
}
