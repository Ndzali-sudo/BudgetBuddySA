package com.budgetbuddysa.app.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface BudgetDao {

    @Query("SELECT * FROM budgets ORDER BY id DESC LIMIT 1")
    suspend fun getBudget(): BudgetEntity?

    @Insert
    suspend fun insertBudget(budget: BudgetEntity)

    @Query("DELETE FROM budgets")
    suspend fun deleteAllBudgets()
}