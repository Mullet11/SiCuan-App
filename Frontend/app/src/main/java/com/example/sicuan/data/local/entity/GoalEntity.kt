package com.example.sicuan.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.sicuan.domain.model.Goal

@Entity(tableName = "goals")
data class GoalEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val targetAmount: Double,
    val currentAmount: Double,
    val deadlineMillis: Long?
) {
    fun toDomainModel(): Goal {
        return Goal(
            id = id,
            title = title,
            targetAmount = targetAmount,
            currentAmount = currentAmount,
            deadlineMillis = deadlineMillis
        )
    }

    companion object {
        fun fromDomainModel(goal: Goal): GoalEntity {
            return GoalEntity(
                id = goal.id,
                title = goal.title,
                targetAmount = goal.targetAmount,
                currentAmount = goal.currentAmount,
                deadlineMillis = goal.deadlineMillis
            )
        }
    }
}
