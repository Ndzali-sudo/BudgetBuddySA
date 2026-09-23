package com.budgetbuddysa.app

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.budgetbuddysa.app.data.local.BudgetDatabase
import com.budgetbuddysa.app.data.local.SavingsEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SavingsActivity : AppCompatActivity() {

    private lateinit var goalNameInput: EditText
    private lateinit var goalAmountInput: EditText
    private lateinit var currentSavingsInput: EditText
    private lateinit var saveGoalBtn: Button
    private lateinit var goalNameText: TextView
    private lateinit var goalProgressText: TextView
    private lateinit var savingsProgressBar: ProgressBar
    private lateinit var savingsPercentageText: TextView

    private val database by lazy {
        BudgetDatabase.getDatabase(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_savings)

        goalNameInput = findViewById(R.id.goalNameInput)
        goalAmountInput = findViewById(R.id.goalAmountInput)
        currentSavingsInput = findViewById(R.id.currentSavingsInput)
        saveGoalBtn = findViewById(R.id.saveGoalBtn)
        goalNameText = findViewById(R.id.goalNameText)
        goalProgressText = findViewById(R.id.goalProgressText)
        savingsProgressBar = findViewById(R.id.savingsProgressBar)
        savingsPercentageText = findViewById(R.id.savingsPercentageText)

        loadSavings()

        saveGoalBtn.setOnClickListener {
            saveSavings()
        }
    }

    private fun saveSavings() {
        val goalName = goalNameInput.text.toString().trim()
        val targetAmount = goalAmountInput.text.toString().toDoubleOrNull()
        val currentAmount = currentSavingsInput.text.toString().toDoubleOrNull()

        if (goalName.isEmpty()) {
            Toast.makeText(this, "Enter a savings goal.", Toast.LENGTH_SHORT).show()
            return
        }

        if (targetAmount == null || targetAmount <= 0) {
            Toast.makeText(this, "Enter a valid goal amount.", Toast.LENGTH_SHORT).show()
            return
        }

        if (currentAmount == null || currentAmount < 0) {
            Toast.makeText(this, "Enter a valid current savings amount.", Toast.LENGTH_SHORT).show()
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            database.savingsDao().insertSavings(
                SavingsEntity(
                    goalName = goalName,
                    targetAmount = targetAmount,
                    currentAmount = currentAmount
                )
            )

            withContext(Dispatchers.Main) {
                updateDisplay(goalName, targetAmount, currentAmount)

                Toast.makeText(
                    this@SavingsActivity,
                    "Savings goal saved successfully!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun loadSavings() {
        CoroutineScope(Dispatchers.IO).launch {
            val savings = database.savingsDao().getSavings()

            withContext(Dispatchers.Main) {
                if (savings != null) {
                    goalNameInput.setText(savings.goalName)
                    goalAmountInput.setText(savings.targetAmount.toString())
                    currentSavingsInput.setText(savings.currentAmount.toString())

                    updateDisplay(
                        savings.goalName,
                        savings.targetAmount,
                        savings.currentAmount
                    )
                }
            }
        }
    }

    private fun updateDisplay(
        goalName: String,
        targetAmount: Double,
        currentAmount: Double
    ) {
        val percentage = if (targetAmount > 0) {
            ((currentAmount / targetAmount) * 100)
                .toInt()
                .coerceIn(0, 100)
        } else {
            0
        }

        goalNameText.text = "Goal: $goalName"
        goalProgressText.text =
            "Saved: R %.2f / R %.2f".format(currentAmount, targetAmount)

        savingsProgressBar.progress = percentage
        savingsPercentageText.text = "$percentage% complete"
    }
}