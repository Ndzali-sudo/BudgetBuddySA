package com.budgetbuddysa.app

import com.budgetbuddysa.app.data.local.ExpenseEntity
import org.junit.Assert.assertEquals
import org.junit.Test

class ExpenseEntityTest {

    @Test
    fun expenseAmount_isStoredCorrectly() {
        val expense = ExpenseEntity(
            amount = 250.0,
            description = "Groceries",
            category = "Food",
            date = "2026-09-23"
        )

        assertEquals(250.0, expense.amount, 0.0)
    }

    @Test
    fun expenseCategory_isStoredCorrectly() {
        val expense = ExpenseEntity(
            amount = 250.0,
            description = "Groceries",
            category = "Food",
            date = "2026-09-23"
        )

        assertEquals("Food", expense.category)
    }
}