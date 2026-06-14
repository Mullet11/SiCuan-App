package com.example.sicuan.domain.repository

import com.example.sicuan.domain.model.Plan
import kotlinx.coroutines.flow.Flow

interface PlanRepository {
    fun getAllPlans(): Flow<List<Plan>>
    suspend fun getPlanById(id: Int): Plan?
    suspend fun insertPlan(plan: Plan)
    suspend fun updatePlan(plan: Plan)
    suspend fun deletePlan(plan: Plan)
    
    fun getPlanHistory(planId: Int): Flow<List<com.example.sicuan.domain.model.PlanHistory>>
    suspend fun addPlanHistory(history: com.example.sicuan.domain.model.PlanHistory)
}
