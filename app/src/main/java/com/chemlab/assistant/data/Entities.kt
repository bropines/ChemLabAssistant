package com.chemlab.assistant.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "inventory")
data class InventoryEntity(
    @PrimaryKey val id: String,
    val reagentId: String,
    val name: String,
    val category: String, // Сохраняем Enum как String
    val state: String,    // Сохраняем Enum как String
    val quantity: Float,
    val unit: String,
    val concentration: Float?,
    val location: String?
)

@Entity(tableName = "lab_works")
data class LabWorkEntity(
    @PrimaryKey val id: String,
    val title: String,
    val date: LocalDate,
    val groupsCount: Int,
    val reagentsNeeded: List<ReagentNeed>,
    val equipmentNeeded: List<EquipmentNeed>
)