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
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.chemlab.assistant.data.InventoryItem
import com.chemlab.assistant.data.ReagentCategory
import com.chemlab.assistant.data.ReagentState
import com.chemlab.assistant.viewmodel.MainViewModel
import java.util.UUID

@Composable
fun InventoryScreen(viewModel: MainViewModel) {
    val inventory by viewModel.inventory.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var showAddDialog by remember { mutableStateOf(false) }

    val filteredInventory = inventory.filter { 
        it.name.contains(searchQuery, ignoreCase = true) 
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Добавить")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            Text("Склад реактивов", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))
            
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Поиск по названию...") },
                leadingIcon = { Icon(Icons.Default.Search, null) }
            )
            Spacer(modifier = Modifier.height(16.dp))

            if (filteredInventory.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Склад пуст или реактивы не найдены", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(filteredInventory, key = { it.id }) { item ->
                        InventoryItemCard(item, onDelete = { viewModel.removeInventoryItem(item.id) })
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddInventoryDialog(
            onDismiss = { showAddDialog = false },
            onSave = { 
                viewModel.addInventoryItem(it)
                showAddDialog = false 
            }
        )
    }
}

@Composable
fun InventoryItemCard(item: InventoryItem, onDelete: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(item.name, style = MaterialTheme.typography.titleMedium)
                val stateStr = when(item.state) {
                    ReagentState.DRY -> "Сухое"
                    ReagentState.LIQUID -> "Жидкость"
                    ReagentState.SOLUTION -> "Раствор ${item.concentration}%"
                }
                Text("${if(item.category == ReagentCategory.INORGANIC) "Неорганика" else "Органика"} • $stateStr", style = MaterialTheme.typography.bodySmall)
                Text("Локация: ${item.location ?: "—"}", style = MaterialTheme.typography.bodySmall)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("${item.quantity} ${item.unit}", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Удалить", tint = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@Composable
fun AddInventoryDialog(onDismiss: () -> Unit, onSave: (InventoryItem) -> Unit) {
    var name by remember { mutableStateOf("") }
    var isOrganic by remember { mutableStateOf(false) }
    var state by remember { mutableStateOf(ReagentState.DRY) }
    var quantity by remember { mutableStateOf("") }
    var concentration by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Новый реактив") },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name, 
                    onValueChange = { name = it; showError = false }, 
                    label = { Text("Название *") }, 
                    isError = showError && name.isBlank(),
                    modifier = Modifier.fillMaxWidth()
                )
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = isOrganic, onCheckedChange = { isOrganic = it })
                    Text("Органический")
                }

                Text("Состояние:", style = MaterialTheme.typography.labelMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ReagentState.values().forEach { s ->
                        val stateName = when(s) {
                            ReagentState.DRY -> "Сухое"
                            ReagentState.LIQUID -> "Жидкость"
                            ReagentState.SOLUTION -> "Раствор"
                        }
                        FilterChip(
                            selected = state == s,
                            onClick = { state = s },
                            label = { Text(stateName) }
                        )
                    }
                }

                if (state == ReagentState.SOLUTION) {
                    OutlinedTextField(
                        value = concentration, 
                        onValueChange = { concentration = it; showError = false }, 
                        label = { Text("Концентрация (%) *") }, 
                        isError = showError && concentration.toFloatOrNull() == null,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), 
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                OutlinedTextField(
                    value = quantity, 
                    onValueChange = { quantity = it; showError = false }, 
                    label = { Text("Количество (${if(state == ReagentState.DRY) "г" else "мл"}) *") }, 
                    isError = showError && quantity.toFloatOrNull() == null,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), 
                    modifier = Modifier.fillMaxWidth()
                )
                
                OutlinedTextField(
                    value = location, 
                    onValueChange = { location = it }, 
                    label = { Text("Место хранения") }, 
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                val q = quantity.replace(",", ".").toFloatOrNull()
                val c = concentration.replace(",", ".").toFloatOrNull()
                val isConcentrationValid = state != ReagentState.SOLUTION || c != null
                
                if (name.isNotBlank() && q != null && q > 0 && isConcentrationValid) {
                    onSave(InventoryItem(
                        id = UUID.randomUUID().toString(),
                        reagentId = "custom_${System.currentTimeMillis()}",
                        name = name,
                        category = if (isOrganic) ReagentCategory.ORGANIC else ReagentCategory.INORGANIC,
                        state = state,
                        quantity = q,
                        unit = if (state == ReagentState.DRY) "г" else "мл",
                        concentration = c,
                        location = location.takeIf { it.isNotBlank() }
                    ))
                } else {
                    showError = true
                }
            }) { Text("Сохранить") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Отмена") } }
    )
}