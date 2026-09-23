package com.budgetbuddysa.app

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

object ApiService {

    private const val BASE_URL = "http://192.168.1.82:8080"

    suspend fun testConnection(): String {
        return withContext(Dispatchers.IO) {
            var connection: HttpURLConnection? = null

            try {
                val url = URL("$BASE_URL/api/health")

                connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connectTimeout = 10000
                connection.readTimeout = 10000

                val responseCode = connection.responseCode

                Log.d("BudgetBuddyAPI", "Response code: $responseCode")

                val response = if (responseCode in 200..299) {
                    connection.inputStream
                        .bufferedReader()
                        .use { it.readText() }
                } else {
                    connection.errorStream
                        ?.bufferedReader()
                        ?.use { it.readText() }
                        ?: "HTTP error: $responseCode"
                }

                if (responseCode == 200) {
                    response
                } else {
                    "Server returned HTTP $responseCode"
                }

            } catch (e: Exception) {

                Log.e(
                    "BudgetBuddyAPI",
                    "Connection error",
                    e
                )

                "Connection failed: ${e.javaClass.simpleName}: ${e.message}"

            } finally {
                connection?.disconnect()
            }
        }
    }

    suspend fun getExpenses(): JSONArray? {
        return withContext(Dispatchers.IO) {
            try {
                val url = URL("$BASE_URL/api/expenses")
                val connection = url.openConnection() as HttpURLConnection

                connection.requestMethod = "GET"
                connection.connectTimeout = 10000
                connection.readTimeout = 10000

                val response = connection.inputStream
                    .bufferedReader()
                    .use { it.readText() }

                connection.disconnect()

                JSONArray(response)

            } catch (e: Exception) {
                Log.e("BudgetBuddyAPI", "Get expenses failed", e)
                null
            }
        }
    }

    suspend fun addExpense(
        amount: Double,
        description: String,
        category: String,
        date: String
    ): JSONObject? {
        return withContext(Dispatchers.IO) {
            try {
                val url = URL("$BASE_URL/api/expenses")
                val connection = url.openConnection() as HttpURLConnection

                connection.requestMethod = "POST"
                connection.setRequestProperty(
                    "Content-Type",
                    "application/json"
                )
                connection.doOutput = true
                connection.connectTimeout = 10000
                connection.readTimeout = 10000

                val json = JSONObject().apply {
                    put("amount", amount)
                    put("description", description)
                    put("category", category)
                    put("date", date)
                }

                connection.outputStream.use { output ->
                    output.write(json.toString().toByteArray())
                }

                val response = connection.inputStream
                    .bufferedReader()
                    .use { it.readText() }

                connection.disconnect()

                JSONObject(response)

            } catch (e: Exception) {
                Log.e("BudgetBuddyAPI", "Add expense failed", e)
                null
            }
        }
    }

    suspend fun getBudget(): JSONObject? {
        return withContext(Dispatchers.IO) {
            try {
                val url = URL("$BASE_URL/api/budgets")
                val connection = url.openConnection() as HttpURLConnection

                connection.requestMethod = "GET"
                connection.connectTimeout = 10000
                connection.readTimeout = 10000

                val response = connection.inputStream
                    .bufferedReader()
                    .use { it.readText() }

                connection.disconnect()

                if (response == "null") {
                    null
                } else {
                    JSONObject(response)
                }

            } catch (e: Exception) {
                Log.e("BudgetBuddyAPI", "Get budget failed", e)
                null
            }
        }
    }

    suspend fun getSavings(): JSONObject? {
        return withContext(Dispatchers.IO) {
            try {
                val url = URL("$BASE_URL/api/savings")
                val connection = url.openConnection() as HttpURLConnection

                connection.requestMethod = "GET"
                connection.connectTimeout = 10000
                connection.readTimeout = 10000

                val response = connection.inputStream
                    .bufferedReader()
                    .use { it.readText() }

                connection.disconnect()

                if (response == "null") {
                    null
                } else {
                    JSONObject(response)
                }

            } catch (e: Exception) {
                Log.e("BudgetBuddyAPI", "Get savings failed", e)
                null
            }
        }
    }
}