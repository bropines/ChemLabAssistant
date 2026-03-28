package com.chemlab.assistant.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.chemlab.assistant.data.EquipmentNeed
import com.chemlab.assistant.data.InventoryItem
import com.chemlab.assistant.data.LabWork
import com.chemlab.assistant.data.ReagentNeed
import com.chemlab.assistant.viewmodel.MainViewModel
import java.time.LocalDate
import java.time.format.DateTimeParseException
import java.util.UUID

@Composable
fun LabWorksScreen(viewModel: MainViewModel) {
    val labWorks by viewModel.labWorks.collectAsState()
    val inventory by viewModel.inventory.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Запланировать")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            Text("Лабораторные работы", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))

            if (labWorks.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Нет запланированных работ", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(labWorks.sortedBy { it.date }, key = { it.id }) { lab ->
                        LabWorkCard(lab, inventory, onDelete = { viewModel.removeLabWork(lab.id) })
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddLabWorkDialog(
            inventory = inventory,
            onDismiss = { showAddDialog = false },
            onSave = { 
                viewModel.addLabWork(it)
                showAddDialog = false 
            }
        )
    }
}

@Composable
fun LabWorkCard(lab: LabWork, inventory: List<InventoryItem>, onDelete: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text(lab.title, style = MaterialTheme.typography.titleLarge)
                    Text("${lab.date} • ${lab.groupsCount} групп(ы)", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Удалить", tint = MaterialTheme.colorScheme.error)
                }
            }
            
            if (lab.reagentsNeeded.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text("Реактивы (всего):", style = MaterialTheme.typography.labelLarge)
                lab.reagentsNeeded.forEach { r ->
                    val invItem = inventory.find { it.id == r.inventoryId }
                    val total = r.amountPerGroup * lab.groupsCount
                    val isLow = invItem == null || invItem.quantity < total
                    
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("• ${invItem?.name ?: "Неизвестно"}", style = MaterialTheme.typography.bodyMedium)
                        Text("$total ${invItem?.unit ?: "ед."}", color = if (isLow) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface)
                    }
                }
            }

            if (lab.equipmentNeeded.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text("Оборудование (всего):", style = MaterialTheme.typography.labelLarge)
                lab.equipmentNeeded.forEach { e ->
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("• ${e.name}", style = MaterialTheme.typography.bodyMedium)
                        Text("${e.amountPerGroup * lab.groupsCount} шт.", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}

@Composable
fun AddLabWorkDialog(inventory: List<InventoryItem>, onDismiss: () -> Unit, onSave: (LabWork) -> Unit) {
    var title by remember { mutableStateOf("") }
    var dateStr by remember { mutableStateOf(LocalDate.now().toString()) }
    var groupsCount by remember { mutableStateOf("15") }
    var showError by remember { mutableStateOf(false) }
    
    val reagents = remember { mutableStateListOf<ReagentNeed>() }
    val equipment = remember { mutableStateListOf<EquipmentNeed>() }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Планирование работы") },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = title, 
                    onValueChange = { title = it; showError = false }, 
                    label = { Text("Название работы *") },
                    isError = showError && title.isBlank(),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = dateStr, 
                    onValueChange = { dateStr = it; showError = false }, 
                    label = { Text("Дата (YYYY-MM-DD) *") },
                    isError = showError && runCatching { LocalDate.parse(dateStr) }.isFailure,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = groupsCount, 
                    onValueChange = { groupsCount = it; showError = false }, 
                    label = { Text("Кол-во групп *") }, 
                    isError = showError && groupsCount.toIntOrNull() == null,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), 
                    modifier = Modifier.fillMaxWidth()
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Реактивы", style = MaterialTheme.typography.titleMedium)
                    TextButton(onClick = { if (inventory.isNotEmpty()) reagents.add(ReagentNeed(inventory.first().id, 0f)) }) { 
                        Text("+ Добавить") 
                    }
                }
                
                reagents.forEachIndexed { index, r ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            // ID / Имя вынесено наверх
                            val reagentName = inventory.find { it.id == r.inventoryId }?.name ?: "ID: ${r.inventoryId.take(6)}"
                            Text("Реактив: $reagentName", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                OutlinedTextField(
                                    value = if (r.amountPerGroup == 0f) "" else r.amountPerGroup.toString(),
                                    onValueChange = { newVal -> 
                                        reagents[index] = r.copy(amountPerGroup = newVal.replace(",", ".").toFloatOrNull() ?: 0f)
                                        showError = false
                                    },
                                    label = { Text("На 1 группу *") },
                                    isError = showError && r.amountPerGroup <= 0f,
                                    modifier = Modifier.weight(1f),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                IconButton(onClick = { reagents.removeAt(index) }) { 
                                    Icon(Icons.Default.Delete, contentDescription = "Удалить", tint = MaterialTheme.colorScheme.error) 
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                try {
                    val d = LocalDate.parse(dateStr)
                    val g = groupsCount.toIntOrNull()
                    val allReagentsValid = reagents.all { it.amountPerGroup > 0f }
                    
                    if (title.isNotBlank() && g != null && g > 0 && allReagentsValid) {
                        onSave(LabWork(UUID.randomUUID().toString(), title, d, g, reagents.toList(), equipment.toList()))
                    } else {
                        showError = true
                    }
                } catch (e: DateTimeParseException) {
                    showError = true
                }
            }) { Text("Сохранить") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Отмена") } }
    )
}