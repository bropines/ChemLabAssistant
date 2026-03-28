package com.chemlab.assistant.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ChemLabRepository(
    private val inventoryDao: InventoryDao,
    private val labWorkDao: LabWorkDao
) {
    // Маппинг Entity -> Model
    val allInventory: Flow<List<InventoryItem>> = inventoryDao.getAllInventory().map { entities ->
        entities.map { 
            InventoryItem(
                it.id, it.reagentId, it.name, 
                ReagentCategory.valueOf(it.category), 
                ReagentState.valueOf(it.state), 
                it.quantity, it.unit, it.concentration, it.location
            ) 
        }
    }

    val allLabWorks: Flow<List<LabWork>> = labWorkDao.getAllLabWorks().map { entities ->
        entities.map {
            LabWork(it.id, it.title, it.date, it.groupsCount, it.reagentsNeeded, it.equipmentNeeded)
        }
    }

    // Маппинг Model -> Entity
    suspend fun insertInventory(item: InventoryItem) {
        inventoryDao.insertInventoryItem(
            InventoryEntity(
                item.id, item.reagentId, item.name, 
                item.category.name, item.state.name, 
                item.quantity, item.unit, item.concentration, item.location
            )
        )
    }

    suspend fun deleteInventory(id: String) = inventoryDao.deleteInventoryItem(id)

    suspend fun insertLabWork(work: LabWork) {
        labWorkDao.insertLabWork(
            LabWorkEntity(work.id, work.title, work.date, work.groupsCount, work.reagentsNeeded, work.equipmentNeeded)
        )
    }

    suspend fun deleteLabWork(id: String) = labWorkDao.deleteLabWork(id)
}