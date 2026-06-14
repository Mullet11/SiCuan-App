package com.example.sicuan.data.remote.dto

data class CurrencyRateDto(
    val date: String,
    val base: String,
    val quote: String,
    val rate: Double
)
