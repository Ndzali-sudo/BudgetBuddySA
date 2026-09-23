package com.budgetbuddysa.app

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.budgetbuddysa.app.data.local.BudgetDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ViewExpensesActivity : AppCompatActivity() {

    private lateinit var expenseListText: TextView
    private lateinit var totalExpensesText: TextView
    private lateinit var addAnotherExpenseBtn: Button
    private lateinit var clearAllExpensesBtn: Button

    private val database by lazy {
        BudgetDatabase.getDatabase(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_expenses)

        expenseListText = findViewById(R.id.expenseListText)
        totalExpensesText = findViewById(R.id.totalExpensesText)
        addAnotherExpenseBtn = findViewById(R.id.addAnotherExpenseBtn)
        clearAllExpensesBtn = findViewById(R.id.clearAllExpensesBtn)

        addAnotherExpenseBtn.setOnClickListener {
            finish()
        }

        clearAllExpensesBtn.setOnClickListener {
            clearExpenses()
        }

        loadExpenses()
    }

    override fun onResume() {
        super.onResume()
        loadExpenses()
    }

    private fun loadExpenses() {
        CoroutineScope(Dispatchers.IO).launch {

            val expenses = database.expenseDao().getAllExpenses()
            val total = expenses.sumOf { it.amount }

            val displayText = if (expenses.isEmpty()) {
                "No expenses recorded."
            } else {
                buildString {
                    expenses.forEachIndexed { index, expense ->
                        append("${index + 1}. ${expense.description}\n")
                        append("Amount: R %.2f\n".format(expense.amount))
                        append("Category: ${expense.category}\n")
                        append("Date: ${expense.date}\n\n")
                    }
                }
            }

            withContext(Dispatchers.Main) {
                totalExpensesText.text =
                    "Total Expenses: R %.2f".format(total)

                expenseListText.text = displayText
            }
        }
    }

    private fun clearExpenses() {
        CoroutineScope(Dispatchers.IO).launch {
            database.expenseDao().deleteAllExpenses()

            withContext(Dispatchers.Main) {
                Toast.makeText(
                    this@ViewExpensesActivity,
                    "All expenses cleared.",
                    Toast.LENGTH_SHORT
                ).show()

                loadExpenses()
            }
        }
    }
}
