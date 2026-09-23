package com.budgetbuddysa.app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expenses")
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val amount: Double,
    val description: String,
    val category: String,
    val date: String
)