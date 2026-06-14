package com.example.sicuan.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.sicuan.domain.usecase.plan.PlanUseCases

class PlanDetailViewModelFactory(
    private val planId: Int,
    private val planUseCases: PlanUseCases
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlanDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PlanDetailViewModel(planId, planUseCases) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
