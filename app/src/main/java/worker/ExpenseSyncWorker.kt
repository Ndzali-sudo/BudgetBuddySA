package com.budgetbuddysa.app.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.budgetbuddysa.app.data.local.BudgetDatabase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ExpenseSyncWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {

        val userId = FirebaseAuth.getInstance().currentUser?.uid
            ?: return Result.success()

        return try {

            val database =
                BudgetDatabase.getDatabase(applicationContext)

            val expenseDao = database.expenseDao()

            val firestore = FirebaseFirestore.getInstance()

            val collection = firestore.collection("users")
                .document(userId)
                .collection("expenses")

            // STEP 1: Upload local expenses

            val localExpenses = expenseDao.getAllExpenses()

            val batch = firestore.batch()

            localExpenses.forEach { expense ->

                val document =
                    collection.document(expense.id.toString())

                val expenseData = hashMapOf(
                    "id" to expense.id.toString(),
                    "amount" to expense.amount,
                    "description" to expense.description,
                    "category" to expense.category,
                    "date" to expense.date,
                    "userId" to userId,
                    "synced" to true
                )

                batch.set(document, expenseData)
            }

            batch.commit().await()

            // STEP 2: Download cloud expenses

            val snapshot = collection.get().await()

            // STEP 3: Save downloaded expenses into Room

            snapshot.documents.forEach { document ->

                val expenseId = document.getString("id")
                    ?.toIntOrNull()

                if (expenseId != null) {

                    val expense = com.budgetbuddysa.app.data.local.ExpenseEntity(
                        id = expenseId,
                        amount = document.getDouble("amount") ?: 0.0,
                        description = document.getString("description") ?: "",
                        category = document.getString("category") ?: "",
                        date = document.getString("date") ?: ""
                    )

                    expenseDao.insertOrReplaceExpense(expense)
                }
            }

            Result.success()

        } catch (exception: Exception) {

            Result.retry()
        }
    }
}