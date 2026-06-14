package com.example.sicuan.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "plan_history",
    foreignKeys = [
        ForeignKey(
            entity = PlanEntity::class,
            parentColumns = ["id"],
            childColumns = ["planId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["planId"])]
)
data class PlanHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val planId: Int,
    val amount: Double,
    val type: String,
    val dateMillis: Long
)
