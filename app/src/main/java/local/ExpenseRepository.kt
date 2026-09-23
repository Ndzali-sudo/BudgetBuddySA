package com.budgetbuddysa.app.data.local

class ExpenseRepository(
    private val expenseDao: ExpenseDao
) {

    suspend fun getAllExpenses(): List<ExpenseEntity> {
        return expenseDao.getAllExpenses()
    }

    suspend fun insertExpense(expense: ExpenseEntity) {
        expenseDao.insertExpense(expense)
    }

    suspend fun deleteExpense(expense: ExpenseEntity) {
        expenseDao.deleteExpense(expense)
    }

    suspend fun deleteAllExpenses() {
        expenseDao.deleteAllExpenses()
    }
}