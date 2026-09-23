package com.budgetbuddysa.app

import android.content.Context

class AchievementManager(
    private val context: Context
) {

    private val preferences =
        context.getSharedPreferences(
            "BudgetBuddyAchievements",
            Context.MODE_PRIVATE
        )

    fun checkFirstExpense(
        expenseCount: Int
    ) {

        if (expenseCount > 0) {

            preferences.edit()
                .putBoolean(
                    "firstExpenseCompleted",
                    true
                )
                .apply()
        }
    }

    fun checkBudget(
        budgetAmount: Double
    ) {

        if (budgetAmount > 0) {

            preferences.edit()
                .putBoolean(
                    "budgetPlannerCompleted",
                    true
                )
                .apply()
        }
    }

    fun checkSavingsGoal(
        goalAmount: Double
    ) {

        if (goalAmount > 0) {

            preferences.edit()
                .putBoolean(
                    "savingsStarterCompleted",
                    true
                )
                .apply()
        }
    }

    fun checkSmartSaver(
        currentSavings: Double
    ) {

        if (currentSavings > 0) {

            preferences.edit()
                .putBoolean(
                    "smartSaverCompleted",
                    true
                )
                .apply()
        }
    }

    fun isFirstExpenseCompleted(): Boolean {

        return preferences.getBoolean(
            "firstExpenseCompleted",
            false
        )
    }

    fun isBudgetPlannerCompleted(): Boolean {

        return preferences.getBoolean(
            "budgetPlannerCompleted",
            false
        )
    }

    fun isSavingsStarterCompleted(): Boolean {

        return preferences.getBoolean(
            "savingsStarterCompleted",
            false
        )
    }

    fun isSmartSaverCompleted(): Boolean {

        return preferences.getBoolean(
            "smartSaverCompleted",
            false
        )
    }

    fun getTotalPoints(): Int {

        var points = 0

        if (isFirstExpenseCompleted()) {
            points += 25
        }

        if (isBudgetPlannerCompleted()) {
            points += 25
        }

        if (isSavingsStarterCompleted()) {
            points += 25
        }

        if (isSmartSaverCompleted()) {
            points += 25
        }

        return points
    }

    fun getProgress(): Int {

        var completed = 0

        if (isFirstExpenseCompleted()) {
            completed++
        }

        if (isBudgetPlannerCompleted()) {
            completed++
        }

        if (isSavingsStarterCompleted()) {
            completed++
        }

        if (isSmartSaverCompleted()) {
            completed++
        }

        return (
                (completed.toDouble() / 4) * 100
                ).toInt()
    }
}