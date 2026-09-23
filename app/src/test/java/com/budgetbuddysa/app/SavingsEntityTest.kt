package com.budgetbuddysa.app

import com.budgetbuddysa.app.data.local.SavingsEntity
import org.junit.Assert.assertEquals
import org.junit.Test

class SavingsEntityTest {

    @Test
    fun savingsGoal_isStoredCorrectly() {
        val savings = SavingsEntity(
            goalName = "New Phone",
            targetAmount = 5000.0,
            currentAmount = 1000.0
        )

        assertEquals("New Phone", savings.goalName)
    }

    @Test
    fun savingsAmounts_areStoredCorrectly() {
        val savings = SavingsEntity(
            goalName = "New Phone",
            targetAmount = 5000.0,
            currentAmount = 1000.0
        )

        assertEquals(5000.0, savings.targetAmount, 0.0)
        assertEquals(1000.0, savings.currentAmount, 0.0)
    }
}