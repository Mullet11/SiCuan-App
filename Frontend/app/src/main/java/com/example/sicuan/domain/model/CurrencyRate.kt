package com.example.sicuan.domain.model

data class CurrencyRate(
    val date: String,
    val base: String,
    val quote: String,
    val rate: Double
)
