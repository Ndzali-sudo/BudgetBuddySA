package com.budgetbuddysa.app.data.remote

import com.budgetbuddysa.app.data.local.ExpenseDao
import com.budgetbuddysa.app.data.local.ExpenseEntity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ExpenseSyncManager {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // Upload local expenses to Firestore
    fun syncExpenses(
        expenses: List<ExpenseEntity>,
        onComplete: (Boolean, String?) -> Unit
    ) {
        val userId = auth.currentUser?.uid

        if (userId == null) {
            onComplete(false, "Please sign in first.")
            return
        }

        if (expenses.isEmpty()) {
            onComplete(true, "No local expenses to upload.")
            return
        }

        val batch = firestore.batch()

        expenses.forEach { expense ->

            val document = firestore.collection("users")
                .document(userId)
                .collection("expenses")
                .document(expense.id.toString())

            val data = hashMapOf(
                "id" to expense.id.toString(),
                "amount" to expense.amount,
                "description" to expense.description,
                "category" to expense.category,
                "date" to expense.date,
                "userId" to userId,
                "synced" to true
            )

            batch.set(document, data)
        }

        batch.commit()
            .addOnSuccessListener {
                onComplete(true, "Expenses uploaded successfully.")
            }
            .addOnFailureListener {
                onComplete(false, it.message)
            }
    }

    // Upload and download expenses
    fun syncExpensesAndDownload(
        expenses: List<ExpenseEntity>,
        expenseDao: ExpenseDao,
        onComplete: (Boolean, String?) -> Unit
    ) {
        val userId = auth.currentUser?.uid

        if (userId == null) {
            onComplete(false, "Please sign in first.")
            return
        }

        val collection = firestore.collection("users")
            .document(userId)
            .collection("expenses")

        fun downloadExpenses() {

            collection.get()
                .addOnSuccessListener { snapshot ->

                    val cloudExpenses = snapshot.documents.mapNotNull { document ->

                        val id = document.getString("id")
                            ?.toIntOrNull()

                        if (id == null) {
                            null
                        } else {
                            ExpenseEntity(
                                id = id,
                                amount = document.getDouble("amount") ?: 0.0,
                                description = document.getString("description") ?: "",
                                category = document.getString("category") ?: "",
                                date = document.getString("date") ?: ""
                            )
                        }
                    }

                    CoroutineScope(Dispatchers.IO).launch {

                        try {
                            cloudExpenses.forEach { expense ->
                                expenseDao.insertOrReplaceExpense(expense)
                            }

                            withContext(Dispatchers.Main) {
                                onComplete(
                                    true,
                                    "Expenses synchronised successfully."
                                )
                            }

                        } catch (e: Exception) {

                            withContext(Dispatchers.Main) {
                                onComplete(false, e.message)
                            }
                        }
                    }
                }
                .addOnFailureListener {
                    onComplete(false, it.message)
                }
        }

        if (expenses.isEmpty()) {
            downloadExpenses()
            return
        }

        val batch = firestore.batch()

        expenses.forEach { expense ->

            val document = collection.document(expense.id.toString())

            val data = hashMapOf(
                "id" to expense.id.toString(),
                "amount" to expense.amount,
                "description" to expense.description,
                "category" to expense.category,
                "date" to expense.date,
                "userId" to userId,
                "synced" to true
            )

            batch.set(document, data)
        }

        batch.commit()
            .addOnSuccessListener {
                downloadExpenses()
            }
            .addOnFailureListener {
                onComplete(false, it.message)
            }
    }
}
