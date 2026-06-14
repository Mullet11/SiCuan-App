package com.example.sicuan.domain.usecase.plan

import com.example.sicuan.domain.model.Plan
import com.example.sicuan.domain.model.PlanHistory
import com.example.sicuan.domain.model.PlanHistoryType
import com.example.sicuan.domain.repository.PlanRepository
import kotlinx.coroutines.flow.Flow

data class PlanUseCases(
    val getPlans: GetPlans,
    val addPlan: AddPlan,
    val updatePlan: UpdatePlan,
    val deletePlan: DeletePlan,
    val topUpPlan: TopUpPlan,
    val withdrawPlan: WithdrawPlan,
    val getPlanHistory: GetPlanHistory
)

class GetPlans(private val repository: PlanRepository) {
    operator fun invoke(): Flow<List<Plan>> {
        return repository.getAllPlans()
    }
}

class AddPlan(private val repository: PlanRepository) {
    suspend operator fun invoke(plan: Plan) {
        if (plan.title.isBlank()) {
            throw IllegalArgumentException("Judul target tidak boleh kosong")
        }
        if (plan.targetAmount <= 0) {
            throw IllegalArgumentException("Target nominal harus lebih dari 0")
        }
        repository.insertPlan(plan)
    }
}

class UpdatePlan(private val repository: PlanRepository) {
    suspend operator fun invoke(plan: Plan) {
        if (plan.title.isBlank()) {
            throw IllegalArgumentException("Judul target tidak boleh kosong")
        }
        if (plan.targetAmount <= 0) {
            throw IllegalArgumentException("Target nominal harus lebih dari 0")
        }
        repository.updatePlan(plan)
    }
}

class DeletePlan(private val repository: PlanRepository) {
    suspend operator fun invoke(plan: Plan) {
        repository.deletePlan(plan)
    }
}

class TopUpPlan(private val repository: PlanRepository) {
    suspend operator fun invoke(plan: Plan, amount: Double) {
        if (amount <= 0) {
            throw IllegalArgumentException("Nominal top up harus lebih dari 0")
        }
        val maxTopUp = plan.targetAmount - plan.savedAmount
        if (amount > maxTopUp) {
            throw IllegalArgumentException("Nominal melebihi sisa target (Maks. ${maxTopUp})")
        }
        
        val updatedPlan = plan.copy(savedAmount = plan.savedAmount + amount)
        repository.updatePlan(updatedPlan)
        
        val history = PlanHistory(
            planId = plan.id,
            amount = amount,
            type = PlanHistoryType.TOP_UP
        )
        repository.addPlanHistory(history)
    }
}

class WithdrawPlan(private val repository: PlanRepository) {
    suspend operator fun invoke(plan: Plan, amount: Double) {
        if (amount <= 0) {
            throw IllegalArgumentException("Nominal tarik harus lebih dari 0")
        }
        if (amount > plan.savedAmount) {
            throw IllegalArgumentException("Saldo tidak mencukupi")
        }
        
        val updatedPlan = plan.copy(savedAmount = plan.savedAmount - amount)
        repository.updatePlan(updatedPlan)
        
        val history = PlanHistory(
            planId = plan.id,
            amount = amount,
            type = PlanHistoryType.WITHDRAW
        )
        repository.addPlanHistory(history)
    }
}

class GetPlanHistory(private val repository: PlanRepository) {
    operator fun invoke(planId: Int): Flow<List<PlanHistory>> {
        return repository.getPlanHistory(planId)
    }
}
