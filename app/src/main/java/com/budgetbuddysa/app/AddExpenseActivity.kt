package com.budgetbuddysa.app

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import java.util.Calendar

class AddExpenseActivity : AppCompatActivity() {

    private lateinit var expenseAmountInput: EditText
    private lateinit var expenseDescriptionInput: EditText
    private lateinit var expenseCategorySpinner: Spinner
    private lateinit var expenseDateInput: EditText
    private lateinit var saveExpenseButton: Button

    private val defaultCategories = listOf(
        "Food",
        "Transport",
        "Entertainment",
        "Education",
        "Shopping",
        "Other"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_add_expense)

        expenseAmountInput = findViewById(R.id.expenseAmountInput)
        expenseDescriptionInput = findViewById(R.id.expenseDescriptionInput)
        expenseCategorySpinner = findViewById(R.id.expenseCategorySpinner)
        expenseDateInput = findViewById(R.id.expenseDateInput)
        saveExpenseButton = findViewById(R.id.saveExpenseBtn)

        loadCategories()

        expenseDateInput.setOnClickListener {
            showDatePicker()
        }

        saveExpenseButton.setOnClickListener {
            saveExpense()
        }
    }

    override fun onResume() {
        super.onResume()
        loadCategories()
    }

    private fun loadCategories() {

        val preferences =
            getSharedPreferences("BudgetBuddyCategories", MODE_PRIVATE)

        val customCategories =
            preferences.getStringSet("categories", emptySet())
                ?: emptySet()

        val allCategories =
            (defaultCategories + customCategories)
                .distinct()
                .sorted()

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            allCategories
        )

        adapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        expenseCategorySpinner.adapter = adapter
    }

    private fun showDatePicker() {

        val calendar = Calendar.getInstance()

        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePicker = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->

                val formattedMonth =
                    String.format("%02d", selectedMonth + 1)

                val formattedDay =
                    String.format("%02d", selectedDay)

                expenseDateInput.setText(
                    "$selectedYear-$formattedMonth-$formattedDay"
                )
            },
            year,
            month,
            day
        )

        datePicker.show()
    }

    private fun saveExpense() {

        val amountText =
            expenseAmountInput.text.toString().trim()

        val description =
            expenseDescriptionInput.text.toString().trim()

        val category =
            expenseCategorySpinner.selectedItem
                ?.toString()
                ?.trim()
                ?: ""

        val date =
            expenseDateInput.text.toString().trim()

        if (amountText.isEmpty()) {
            Toast.makeText(
                this,
                "Please enter an amount",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        if (description.isEmpty()) {
            Toast.makeText(
                this,
                "Please enter a description",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        if (date.isEmpty()) {
            Toast.makeText(
                this,
                "Please select a date",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        if (category.isEmpty()) {
            Toast.makeText(
                this,
                "Please select a category",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        try {

            val amount = amountText.toDouble()

            if (amount <= 0) {
                Toast.makeText(
                    this,
                    "Please enter a valid amount",
                    Toast.LENGTH_SHORT
                ).show()
                return
            }

            /*
             * Save locally first.
             * This keeps the existing BudgetBuddy SA
             * functionality working.
             */
            val expenseStorage = ExpenseStorage(this)

            expenseStorage.saveExpense(
                amountText.toDouble(),
                description,
                category,
                date
            )

            val expenses =
                expenseStorage.getExpenses()

            val achievementManager =
                AchievementManager(this)

            achievementManager.checkFirstExpense(
                expenses.length()
            )

            /*
             * Send the expense to the REST API.
             */
            lifecycleScope.launch {

                val serverExpense =
                    ApiService.addExpense(
                        amount = amount,
                        description = description,
                        category = category,
                        date = date
                    )

                if (serverExpense != null) {

                    Toast.makeText(
                        this@AddExpenseActivity,
                        "Expense saved and synced!",
                        Toast.LENGTH_SHORT
                    ).show()

                } else {

                    Toast.makeText(
                        this@AddExpenseActivity,
                        "Saved locally. Server sync failed.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            expenseAmountInput.text.clear()
            expenseDescriptionInput.text.clear()
            expenseDateInput.text.clear()

        } catch (e: NumberFormatException) {

            Toast.makeText(
                this,
                "Please enter a valid amount",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}