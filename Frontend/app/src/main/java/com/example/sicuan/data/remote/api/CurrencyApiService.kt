package com.example.sicuan.data.remote.api

import com.example.sicuan.data.remote.dto.CurrencyRateDto
import retrofit2.http.GET
import retrofit2.http.Query

interface CurrencyApiService {

    @GET("v2/rates")
    suspend fun getLatestRates(
        @Query("base") base: String = "USD",
        @Query("quotes") quotes: String = "IDR,EUR,JPY,SGD,MYR"
    ): List<CurrencyRateDto>
}