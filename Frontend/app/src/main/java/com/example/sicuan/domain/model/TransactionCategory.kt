package com.example.sicuan.domain.model

object TransactionCategory {

    val expenseCategories = listOf(
        "Makan",
        "Transportasi",
        "Tugas",
        "Belanja",
        "Hiburan",
        "Kesehatan",
        "Lainnya"
    )

    val incomeCategories = listOf(
        "Uang Saku",
        "Gaji",
        "Beasiswa",
        "Hadiah",
        "Lainnya"
    )

    fun getCategoriesByType(type: String): List<String> {
        return if (type == TransactionType.INCOME) {
            incomeCategories
        } else {
            expenseCategories
        }
    }

    fun getDefaultCategoryByType(type: String): String {
        return if (type == TransactionType.INCOME) {
            "Uang Saku"
        } else {
            "Makan"
        }
    }
}
