package com.budgetbuddysa.app

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
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

    private val notificationChannelId =
        "budgetbuddy_budget_alerts"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_budget)

        budgetAmountInput = findViewById(R.id.budgetAmountInput)
        budgetText = findViewById(R.id.budgetText)
        spentText = findViewById(R.id.spentText)
        remainingText = findViewById(R.id.remainingText)
        budgetProgress = findViewById(R.id.budgetProgress)
        saveBudgetBtn = findViewById(R.id.saveBudgetBtn)

        createNotificationChannel()

        loadBudget()

        saveBudgetBtn.setOnClickListener {
            saveBudget()
        }
    }

    private fun saveBudget() {

        val amount = budgetAmountInput.text.toString().toDoubleOrNull()

        if (amount == null || amount <= 0) {

            Toast.makeText(
                this,
                "Enter a valid budget amount.",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        val month = SimpleDateFormat(
            "MMMM yyyy",
            Locale.getDefault()
        ).format(Date())

        CoroutineScope(Dispatchers.IO).launch {

            database.budgetDao().insertBudget(
                BudgetEntity(
                    amount = amount,
                    month = month
                )
            )

            val synced = syncBudgetToServer(amount, month)

            withContext(Dispatchers.Main) {

                updateBudgetDisplay(amount)

                checkBudgetAlerts(amount)

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

            val connection =
                url.openConnection() as HttpURLConnection

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

            val success = connection.responseCode in 200..299

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

    // Create notification channel

    private fun createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val channel = NotificationChannel(
                notificationChannelId,
                "Budget Alerts",
                NotificationManager.IMPORTANCE_HIGH
            )

            channel.description =
                "Notifications about monthly budget spending"

            val manager =
                getSystemService(Context.NOTIFICATION_SERVICE)
                        as NotificationManager

            manager.createNotificationChannel(channel)
        }
    }

    // Check budget spending and send alerts

    private fun checkBudgetAlerts(budgetAmount: Double) {

        val expenseStorage = ExpenseStorage(this)

        val expenses = expenseStorage.getExpenses()

        var totalSpent = 0.0

        for (i in 0 until expenses.length()) {

            val expense = expenses.getJSONObject(i)

            totalSpent += expense.optDouble("amount", 0.0)
        }

        val percentage =
            (totalSpent / budgetAmount) * 100

        if (percentage >= 100) {

            sendBudgetNotification(
                "Budget Limit Reached!",
                "You have reached or exceeded your monthly budget."
            )

        } else if (percentage >= 80) {

            sendBudgetNotification(
                "Budget Alert!",
                "You have used ${percentage.toInt()}% of your monthly budget."
            )
        }
    }

    // Display notification only once per threshold each month

    private fun sendBudgetNotification(
        title: String,
        message: String
    ) {

        if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        if (!NotificationManagerCompat.from(this).areNotificationsEnabled()) {
            return
        }

        val month = SimpleDateFormat(
            "yyyy_MM",
            Locale.getDefault()
        ).format(Date())

        val threshold = if (title.contains("Limit Reached")) {
            "100"
        } else {
            "80"
        }

        val preferences = getSharedPreferences(
            "BudgetBuddyAlerts",
            Context.MODE_PRIVATE
        )

        val alertKey = "${month}_$threshold"

        if (preferences.getBoolean(alertKey, false)) {
            return
        }

        val intent = Intent(this, BudgetActivity::class.java)

        val pendingIntent = PendingIntent.getActivity(
            this,
            200,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or
                    PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(
            this,
            notificationChannelId
        )
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(message)
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        try {

            NotificationManagerCompat.from(this).notify(
                threshold.toInt(),
                notification
            )

            preferences.edit()
                .putBoolean(alertKey, true)
                .apply()

        } catch (e: SecurityException) {

            e.printStackTrace()
        }
    }
}