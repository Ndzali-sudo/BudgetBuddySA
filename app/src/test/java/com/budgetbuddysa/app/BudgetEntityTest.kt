package com.budgetbuddysa.app

import com.budgetbuddysa.app.data.local.BudgetEntity
import org.junit.Assert.assertEquals
import org.junit.Test

class BudgetEntityTest {

    @Test
    fun budgetAmount_isStoredCorrectly() {
        val budget = BudgetEntity(
            amount = 1000.0,
            month = "September 2026"
        )

        assertEquals(1000.0, budget.amount, 0.0)
    }

    @Test
    fun budgetMonth_isStoredCorrectly() {
        val budget = BudgetEntity(
            amount = 1000.0,
            month = "September 2026"
        )

        assertEquals("September 2026", budget.month)
    }
}