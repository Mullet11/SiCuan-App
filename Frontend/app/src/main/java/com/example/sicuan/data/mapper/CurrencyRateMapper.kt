package com.example.sicuan.data.mapper

import com.example.sicuan.data.remote.dto.CurrencyRateDto
import com.example.sicuan.domain.model.CurrencyRate

fun CurrencyRateDto.toDomain(): CurrencyRate {
    return CurrencyRate(
        date = date,
        base = base,
        quote = quote,
        rate = rate
    )
}
