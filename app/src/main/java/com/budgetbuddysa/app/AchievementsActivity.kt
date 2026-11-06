package com.budgetbuddysa.app

import android.os.Bundle
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class AchievementsActivity : AppCompatActivity() {

    private lateinit var achievementPointsText: TextView
    private lateinit var achievementListText: TextView
    private lateinit var achievementProgressBar: ProgressBar
    private lateinit var achievementProgressText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_achievements)

        achievementPointsText =
            findViewById(R.id.achievementPointsText)

        achievementListText =
            findViewById(R.id.achievementListText)

        achievementProgressBar =
            findViewById(R.id.achievementProgressBar)

        achievementProgressText =
            findViewById(R.id.achievementProgressText)
    }

    override fun onResume() {
        super.onResume()
        updateAchievements()
    }

    private fun updateAchievements() {

        val achievementManager = AchievementManager(this)

        val points = achievementManager.getTotalPoints()
        val progress = achievementManager.getProgress()

        val firstExpenseCompleted =
            achievementManager.isFirstExpenseCompleted()

        val budgetCompleted =
            achievementManager.isBudgetPlannerCompleted()

        val savingsCompleted =
            achievementManager.isSavingsStarterCompleted()

        val smartSaverCompleted =
            achievementManager.isSmartSaverCompleted()

        // Display points
        achievementPointsText.text =
            getString(R.string.achievement_points, points)

        // First Expense
        val firstExpenseText =
            if (firstExpenseCompleted) {
                getString(R.string.first_expense_completed)
            } else {
                getString(R.string.first_expense)
            }

        // Budget Planner
        val budgetText =
            if (budgetCompleted) {
                getString(R.string.budget_planner_completed)
            } else {
                getString(R.string.budget_planner)
            }

        // Savings Starter
        val savingsText =
            if (savingsCompleted) {
                getString(R.string.savings_starter_completed)
            } else {
                getString(R.string.savings_starter)
            }

        // Smart Saver
        val smartSaverText =
            if (smartSaverCompleted) {
                getString(R.string.smart_saver_completed)
            } else {
                getString(R.string.smart_saver)
            }

        achievementListText.text =
            "$firstExpenseText\n\n" +
                    "$budgetText\n\n" +
                    "$savingsText\n\n" +
                    smartSaverText

        // Update progress
        achievementProgressBar.progress = progress

        achievementProgressText.text =
            getString(R.string.achievement_progress, progress)
    }
}