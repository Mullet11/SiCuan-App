package com.example.sicuan.domain.model

data class OcrReceiptResult(
    val rawText: String = "",
    val merchant: String = "",
    val amountText: String = "",
    val dateText: String = "",
    val dateMillis: Long? = null,
    val title: String = "",
    val note: String = "",
    val category: String? = null
)
