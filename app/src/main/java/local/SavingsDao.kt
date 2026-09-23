package com.budgetbuddysa.app.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface SavingsDao {

    @Query("SELECT * FROM savings ORDER BY id DESC LIMIT 1")
    suspend fun getSavings(): SavingsEntity?

    @Insert
    suspend fun insertSavings(savings: SavingsEntity)

    @Query("DELETE FROM savings")
    suspend fun deleteAllSavings()
}