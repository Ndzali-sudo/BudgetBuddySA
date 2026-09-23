package com.budgetbuddysa.app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.budgetbuddysa.app.data.local.BudgetDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DashboardActivity : AppCompatActivity() {

    private lateinit var balanceText: TextView
    private lateinit var expensesText: TextView
    private lateinit var budgetText: TextView
    private lateinit var budgetProgress: ProgressBar
    private lateinit var budgetProgressText: TextView

    private lateinit var addExpenseBtn: Button
    private lateinit var viewExpensesBtn: Button
    private lateinit var budgetBtn: Button
    private lateinit var savingsBtn: Button
    private lateinit var categoriesBtn: Button
    private lateinit var achievementsBtn: Button
    private lateinit var settingsBtn: Button

    private val database by lazy {
        BudgetDatabase.getDatabase(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        balanceText = findViewById(R.id.balanceText)
        expensesText = findViewById(R.id.expensesText)
        budgetText = findViewById(R.id.budgetText)
        budgetProgress = findViewById(R.id.budgetProgress)
        budgetProgressText = findViewById(R.id.budgetProgressText)

        addExpenseBtn = findViewById(R.id.addExpenseBtn)
        viewExpensesBtn = findViewById(R.id.viewExpensesBtn)
        budgetBtn = findViewById(R.id.budgetBtn)
        savingsBtn = findViewById(R.id.savingsBtn)
        categoriesBtn = findViewById(R.id.categoriesBtn)
        achievementsBtn = findViewById(R.id.achievementsBtn)
        settingsBtn = findViewById(R.id.settingsBtn)

        setupButtons()
        updateDashboard()
    }

    private fun setupButtons() {
        addExpenseBtn.setOnClickListener {
            startActivity(Intent(this, AddExpenseActivity::class.java))
        }

        viewExpensesBtn.setOnClickListener {
            startActivity(Intent(this, ViewExpensesActivity::class.java))
        }

        budgetBtn.setOnClickListener {
            startActivity(Intent(this, BudgetActivity::class.java))
        }

        savingsBtn.setOnClickListener {
            startActivity(Intent(this, SavingsActivity::class.java))
        }

        categoriesBtn.setOnClickListener {
            startActivity(Intent(this, CategoriesActivity::class.java))
        }

        achievementsBtn.setOnClickListener {
            startActivity(Intent(this, AchievementsActivity::class.java))
        }

        settingsBtn.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        updateDashboard()
    }

    private fun updateDashboard() {
        CoroutineScope(Dispatchers.IO).launch {

            val expenses = database.expenseDao().getAllExpenses()
            val budget = database.budgetDao().getBudget()

            val totalExpenses = expenses.sumOf { it.amount }
            val budgetAmount = budget?.amount ?: 0.0
            val remaining = budgetAmount - totalExpenses

            val percentage = if (budgetAmount > 0) {
                ((totalExpenses / budgetAmount) * 100)
                    .toInt()
                    .coerceIn(0, 100)
            } else {
                0
            }

            withContext(Dispatchers.Main) {
                expensesText.text =
                    "Expenses: R %.2f".format(totalExpenses)

                budgetText.text =
                    "Budget: R %.2f".format(budgetAmount)

                balanceText.text =
                    "Remaining: R %.2f".format(remaining)

                budgetProgress.progress = percentage

                budgetProgressText.text =
                    if (budgetAmount > 0) {
                        "$percentage% of budget used"
                    } else {
                        "No budget set"
                    }
            }
        }
    }
}