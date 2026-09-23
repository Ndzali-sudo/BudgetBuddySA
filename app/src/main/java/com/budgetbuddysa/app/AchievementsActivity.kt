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

        setContentView(
            R.layout.activity_achievements
        )

        achievementPointsText =
            findViewById(
                R.id.achievementPointsText
            )

        achievementListText =
            findViewById(
                R.id.achievementListText
            )

        achievementProgressBar =
            findViewById(
                R.id.achievementProgressBar
            )

        achievementProgressText =
            findViewById(
                R.id.achievementProgressText
            )
    }

    override fun onResume() {
        super.onResume()

        updateAchievements()
    }

    private fun updateAchievements() {

        val achievementManager =
            AchievementManager(this)

        val points =
            achievementManager.getTotalPoints()

        val progress =
            achievementManager.getProgress()

        val firstExpenseCompleted =
            achievementManager
                .isFirstExpenseCompleted()

        val budgetCompleted =
            achievementManager
                .isBudgetPlannerCompleted()

        val savingsCompleted =
            achievementManager
                .isSavingsStarterCompleted()

        val smartSaverCompleted =
            achievementManager
                .isSmartSaverCompleted()

        achievementPointsText.text =
            "Points: $points"

        val firstExpenseText =
            if (firstExpenseCompleted) {

                "🏆 First Expense — COMPLETED"

            } else {

                "🏆 First Expense"
            }

        val budgetText =
            if (budgetCompleted) {

                "🏆 Budget Planner — COMPLETED"

            } else {

                "🏆 Budget Planner"
            }

        val savingsText =
            if (savingsCompleted) {

                "🏆 Savings Starter — COMPLETED"

            } else {

                "🏆 Savings Starter"
            }

        val smartSaverText =
            if (smartSaverCompleted) {

                "🏆 Smart Saver — COMPLETED"

            } else {

                "🏆 Smart Saver"
            }

        achievementListText.text =
            "$firstExpenseText\n\n" +
                    "$budgetText\n\n" +
                    "$savingsText\n\n" +
                    smartSaverText

        achievementProgressBar.progress =
            progress

        achievementProgressText.text =
            "$progress% achievement progress"
    }
}