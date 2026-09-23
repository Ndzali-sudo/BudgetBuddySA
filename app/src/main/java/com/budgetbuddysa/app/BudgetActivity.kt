package com.budgetbuddysa.app

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.budgetbuddysa.app.data.local.BudgetDatabase
import com.budgetbuddysa.app.data.local.BudgetEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.net.HttpURLConnection
import java.net.URL
import org.json.JSONObject

class BudgetActivity : AppCompatActivity() {

    private lateinit var budgetAmountInput: EditText
    private lateinit var budgetText: TextView
    private lateinit var spentText: TextView
    private lateinit var remainingText: TextView
    private lateinit var budgetProgress: ProgressBar
    private lateinit var saveBudgetBtn: Button

    private val database by lazy {
        BudgetDatabase.getDatabase(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_budget)

        budgetAmountInput = findViewById(R.id.budgetAmountInput)
        budgetText = findViewById(R.id.budgetText)
        spentText = findViewById(R.id.spentText)
        remainingText = findViewById(R.id.remainingText)
        budgetProgress = findViewById(R.id.budgetProgress)
        saveBudgetBtn = findViewById(R.id.saveBudgetBtn)

        loadBudget()

        saveBudgetBtn.setOnClickListener {
            saveBudget()
        }
    }

    private fun saveBudget() {
        val amount = budgetAmountInput.text.toString().toDoubleOrNull()

        if (amount == null || amount <= 0) {
            Toast.makeText(this, "Enter a valid budget amount.", Toast.LENGTH_SHORT).show()
            return
        }

        val month = SimpleDateFormat(
            "MMMM yyyy",
            Locale.getDefault()
        ).format(Date())

        CoroutineScope(Dispatchers.IO).launch {

            // Save locally first
            database.budgetDao().insertBudget(
                BudgetEntity(
                    amount = amount,
                    month = month
                )
            )

            // Try server sync
            val synced = syncBudgetToServer(amount, month)

            withContext(Dispatchers.Main) {
                updateBudgetDisplay(amount)

                if (synced) {
                    Toast.makeText(
                        this@BudgetActivity,
                        "Budget saved and synced!",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        this@BudgetActivity,
                        "Budget saved locally. Server sync failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun syncBudgetToServer(
        amount: Double,
        month: String
    ): Boolean {
        return try {
            val url = URL("http://192.168.1.82:8080/api/budgets")
            val connection = url.openConnection() as HttpURLConnection

            connection.requestMethod = "POST"
            connection.setRequestProperty(
                "Content-Type",
                "application/json"
            )
            connection.doOutput = true
            connection.connectTimeout = 5000
            connection.readTimeout = 5000

            val json = JSONObject()
            json.put("amount", amount)
            json.put("month", month)

            connection.outputStream.use { output ->
                output.write(json.toString().toByteArray())
            }

            val success =
                connection.responseCode in 200..299

            connection.disconnect()

            success

        } catch (e: Exception) {
            false
        }
    }

    private fun loadBudget() {
        CoroutineScope(Dispatchers.IO).launch {

            val budget = database.budgetDao().getBudget()

            withContext(Dispatchers.Main) {
                if (budget != null) {
                    budgetAmountInput.setText(
                        budget.amount.toString()
                    )

                    updateBudgetDisplay(budget.amount)
                }
            }
        }
    }

    private fun updateBudgetDisplay(
        budgetAmount: Double
    ) {
        val expenseStorage = ExpenseStorage(this)
        val expenses = expenseStorage.getExpenses()

        var totalSpent = 0.0

        for (i in 0 until expenses.length()) {
            val expense = expenses.getJSONObject(i)
            totalSpent += expense.optDouble("amount", 0.0)
        }

        val remaining = budgetAmount - totalSpent

        budgetText.text =
            "Budget: R %.2f".format(budgetAmount)

        spentText.text =
            "Spent: R %.2f".format(totalSpent)

        remainingText.text =
            "Remaining: R %.2f".format(remaining)

        val percentage =
            if (budgetAmount > 0) {
                ((totalSpent / budgetAmount) * 100)
                    .toInt()
                    .coerceIn(0, 100)
            } else {
                0
            }

        budgetProgress.progress = percentage
    }
}