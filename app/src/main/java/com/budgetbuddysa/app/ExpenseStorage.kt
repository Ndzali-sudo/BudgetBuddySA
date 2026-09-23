package com.budgetbuddysa.app

import android.content.Context
import com.budgetbuddysa.app.data.local.BudgetDatabase
import com.budgetbuddysa.app.data.local.ExpenseEntity
import kotlinx.coroutines.runBlocking
import org.json.JSONArray
import org.json.JSONObject

class ExpenseStorage(context: Context) {

    private val database = BudgetDatabase.getDatabase(context)
    private val expenseDao = database.expenseDao()

    fun saveExpense(
        amount: Double,
        description: String,
        category: String,
        date: String
    ) {
        runBlocking {
            expenseDao.insertExpense(
                ExpenseEntity(
                    amount = amount,
                    description = description,
                    category = category,
                    date = date
                )
            )
        }
    }

    fun getExpenses(): JSONArray {
        return runBlocking {
            val expenses = expenseDao.getAllExpenses()
            val jsonArray = JSONArray()

            expenses.forEach { expense ->
                val jsonObject = JSONObject()
                jsonObject.put("id", expense.id)
                jsonObject.put("amount", expense.amount)
                jsonObject.put("description", expense.description)
                jsonObject.put("category", expense.category)
                jsonObject.put("date", expense.date)
                jsonArray.put(jsonObject)
            }

            jsonArray
        }
    }

    fun deleteExpense(index: Int) {
        runBlocking {
            val expenses = expenseDao.getAllExpenses()

            if (index in expenses.indices) {
                expenseDao.deleteExpense(expenses[index])
            }
        }
    }

    fun clearAllExpenses() {
        runBlocking {
            expenseDao.deleteAllExpenses()
        }
    }
}

