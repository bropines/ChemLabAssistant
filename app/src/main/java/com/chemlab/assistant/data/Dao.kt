package com.chemlab.assistant.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface InventoryDao {
    @Query("SELECT * FROM inventory")
    fun getAllInventory(): Flow<List<InventoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInventoryItem(item: InventoryEntity)

    @Query("DELETE FROM inventory WHERE id = :id")
    suspend fun deleteInventoryItem(id: String)
}

@Dao
interface LabWorkDao {
    @Query("SELECT * FROM lab_works ORDER BY date ASC")
    fun getAllLabWorks(): Flow<List<LabWorkEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLabWork(labWork: LabWorkEntity)

    @Query("DELETE FROM lab_works WHERE id = :id")
    suspend fun deleteLabWork(id: String)
}