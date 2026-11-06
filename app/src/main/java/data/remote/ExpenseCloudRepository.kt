package com.budgetbuddysa.app.data.remote

import com.budgetbuddysa.app.data.local.ExpenseEntity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ExpenseCloudRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // SAVE EXPENSE TO FIRESTORE
    fun saveExpense(
        expense: ExpenseEntity,
        onComplete: (Boolean, String?) -> Unit
    ) {
        val userId = auth.currentUser?.uid

        if (userId == null) {
            onComplete(false, "Please sign in first.")
            return
        }

        val cloudExpense = hashMapOf(
            "id" to expense.id.toString(),
            "amount" to expense.amount,
            "description" to expense.description,
            "category" to expense.category,
            "date" to expense.date,
            "userId" to userId,
            "synced" to true
        )

        firestore.collection("users")
            .document(userId)
            .collection("expenses")
            .document(expense.id.toString())
            .set(cloudExpense)
            .addOnSuccessListener {
                onComplete(true, null)
            }
            .addOnFailureListener { exception ->
                onComplete(false, exception.message)
            }
    }

    // RETRIEVE EXPENSES FROM FIRESTORE
    fun getExpenses(
        onComplete: (List<CloudExpense>?, String?) -> Unit
    ) {
        val userId = auth.currentUser?.uid

        if (userId == null) {
            onComplete(null, "Please sign in first.")
            return
        }

        firestore.collection("users")
            .document(userId)
            .collection("expenses")
            .get()
            .addOnSuccessListener { snapshot ->

                val expenses = snapshot.documents.mapNotNull {
                    it.toObject(CloudExpense::class.java)
                }

                onComplete(expenses, null)
            }
            .addOnFailureListener { exception ->
                onComplete(null, exception.message)
            }
    }
}


