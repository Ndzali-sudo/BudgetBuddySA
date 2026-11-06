package com.budgetbuddysa.app

import android.app.Application
import com.budgetbuddysa.app.sync.ExpenseSyncScheduler

class BudgetBuddyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        ExpenseSyncScheduler.scheduleSync(this)
    }
}