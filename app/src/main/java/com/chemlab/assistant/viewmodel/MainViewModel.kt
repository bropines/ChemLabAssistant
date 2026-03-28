package com.chemlab.assistant.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.chemlab.assistant.data.ChemLabRepository
import com.chemlab.assistant.data.InventoryItem
import com.chemlab.assistant.data.LabWork
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(private val repository: ChemLabRepository) : ViewModel() {

    // stateIn превращает Flow из БД в StateFlow для Compose
    val inventory: StateFlow<List<InventoryItem>> = repository.allInventory
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val labWorks: StateFlow<List<LabWork>> = repository.allLabWorks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addInventoryItem(item: InventoryItem) {
        viewModelScope.launch { repository.insertInventory(item) }
    }

    fun removeInventoryItem(id: String) {
        viewModelScope.launch { repository.deleteInventory(id) }
    }

    fun addLabWork(labWork: LabWork) {
        viewModelScope.launch { repository.insertLabWork(labWork) }
    }

    fun removeLabWork(id: String) {
        viewModelScope.launch { repository.deleteLabWork(id) }
    }
}

// Фабрика нужна, чтобы передать репозиторий в конструктор ViewModel
class MainViewModelFactory(private val repository: ChemLabRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}