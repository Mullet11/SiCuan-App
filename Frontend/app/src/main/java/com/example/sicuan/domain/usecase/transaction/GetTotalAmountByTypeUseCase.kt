package com.example.sicuan.domain.usecase.transaction

import com.example.sicuan.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow

class GetTotalAmountByTypeUseCase(
    private val repository: TransactionRepository
) {
    operator fun invoke(type: String): Flow<Double> {
        return repository.getTotalAmountByType(type)
    }
}
