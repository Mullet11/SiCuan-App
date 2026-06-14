package com.example.sicuan.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.sicuan.data.local.entity.PlanHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlanHistoryDao {
    @Query("SELECT * FROM plan_history WHERE planId = :planId ORDER BY dateMillis DESC")
    fun getHistoryByPlanId(planId: Int): Flow<List<PlanHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlanHistory(history: PlanHistoryEntity)

    @Query("DELETE FROM plan_history WHERE planId = :planId")
    suspend fun deleteHistoryByPlanId(planId: Int)
}
