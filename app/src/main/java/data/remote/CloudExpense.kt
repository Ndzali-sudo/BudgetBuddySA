
package com.budgetbuddysa.app.data.remote

data class CloudExpense(
    val id: String = "",
    val amount: Double = 0.0,
    val description: String = "",
    val category: String = "",
    val date: String = "",
    val userId: String = "",
    val synced: Boolean = true
)
