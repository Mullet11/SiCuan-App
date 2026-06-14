package com.example.sicuan.data.repository

import com.example.sicuan.data.local.dao.PlanDao
import com.example.sicuan.data.local.dao.PlanHistoryDao
import com.example.sicuan.data.local.entity.PlanEntity
import com.example.sicuan.data.local.entity.PlanHistoryEntity
import com.example.sicuan.domain.model.Plan
import com.example.sicuan.domain.model.PlanHistory
import com.example.sicuan.domain.model.PlanHistoryType
import com.example.sicuan.domain.repository.PlanRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PlanRepositoryImpl(
    private val dao: PlanDao,
    private val historyDao: PlanHistoryDao
) : PlanRepository {

    override fun getAllPlans(): Flow<List<Plan>> {
        return dao.getAllPlans().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override suspend fun getPlanById(id: Int): Plan? {
        return dao.getPlanById(id)?.toDomainModel()
    }

    override suspend fun insertPlan(plan: Plan) {
        dao.insertPlan(plan.toEntity())
    }

    override suspend fun updatePlan(plan: Plan) {
        dao.updatePlan(plan.toEntity())
    }

    override suspend fun deletePlan(plan: Plan) {
        dao.deletePlan(plan.toEntity())
    }

    override fun getPlanHistory(planId: Int): Flow<List<PlanHistory>> {
        return historyDao.getHistoryByPlanId(planId).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override suspend fun addPlanHistory(history: PlanHistory) {
        historyDao.insertPlanHistory(history.toEntity())
    }

    private fun PlanEntity.toDomainModel(): Plan {
        return Plan(
            id = id,
            title = title,
            targetAmount = targetAmount,
            savedAmount = savedAmount,
            deadlineDateMillis = deadlineDateMillis,
            createdAt = createdAt
        )
    }

    private fun Plan.toEntity(): PlanEntity {
        return PlanEntity(
            id = id,
            title = title,
            targetAmount = targetAmount,
            savedAmount = savedAmount,
            deadlineDateMillis = deadlineDateMillis,
            createdAt = createdAt
        )
    }

    private fun PlanHistoryEntity.toDomainModel(): PlanHistory {
        return PlanHistory(
            id = id,
            planId = planId,
            amount = amount,
            type = PlanHistoryType.valueOf(type),
            dateMillis = dateMillis
        )
    }

    private fun PlanHistory.toEntity(): PlanHistoryEntity {
        return PlanHistoryEntity(
            id = id,
            planId = planId,
            amount = amount,
            type = type.name,
            dateMillis = dateMillis
        )
    }
}
