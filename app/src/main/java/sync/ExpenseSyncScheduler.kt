package com.budgetbuddysa.app.sync

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.budgetbuddysa.app.worker.ExpenseSyncWorker
import java.util.concurrent.TimeUnit

object ExpenseSyncScheduler {

    fun scheduleSync(context: Context) {

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val syncRequest =
            PeriodicWorkRequestBuilder<ExpenseSyncWorker>(
                15, TimeUnit.MINUTES
            )
                .setConstraints(constraints)
                .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "budgetbuddy_expense_sync",
            ExistingPeriodicWorkPolicy.KEEP,
            syncRequest
        )
    }
}

