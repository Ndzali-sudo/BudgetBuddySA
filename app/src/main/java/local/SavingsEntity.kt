package com.budgetbuddysa.app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "savings")
data class SavingsEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val goalName: String,
    val targetAmount: Double,
    val currentAmount: Double
)