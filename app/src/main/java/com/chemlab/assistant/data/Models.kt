package com.chemlab.assistant.data

import java.time.LocalDate

enum class ReagentState { DRY, LIQUID, SOLUTION }
enum class ReagentCategory { INORGANIC, ORGANIC }

data class InventoryItem(
    val id: String,
    val reagentId: String,
    val name: String,
    val category: ReagentCategory,
    val state: ReagentState,
    val quantity: Float,
    val unit: String,
    val concentration: Float? = null,
    val location: String? = null
)

data class LabWork(
    val id: String,
    val title: String,
    val date: LocalDate,
    val groupsCount: Int,
    val reagentsNeeded: List<ReagentNeed>,
    val equipmentNeeded: List<EquipmentNeed>
)

data class ReagentNeed(val inventoryId: String, val amountPerGroup: Float)
data class EquipmentNeed(val name: String, val amountPerGroup: Int)