package com.chemlab.assistant

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.lifecycle.viewmodel.compose.viewModel
import com.chemlab.assistant.data.AppDatabase
import com.chemlab.assistant.data.ChemLabRepository
import com.chemlab.assistant.ui.navigation.AppNavigation
import com.chemlab.assistant.ui.theme.ChemLabTheme
import com.chemlab.assistant.viewmodel.MainViewModel
import com.chemlab.assistant.viewmodel.MainViewModelFactory

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Инициализируем БД и Репозиторий
        val database = AppDatabase.getDatabase(applicationContext)
        val repository = ChemLabRepository(database.inventoryDao(), database.labWorkDao())
        
        setContent {
            ChemLabTheme {
                // Передаем фабрику во viewModel()
                val viewModel: MainViewModel = viewModel(
                    factory = MainViewModelFactory(repository)
                )
                
                val windowSizeClass = calculateWindowSizeClass(this)
                // Обязательно добавь параметр viewModel в функцию AppNavigation
                AppNavigation(windowSizeClass = windowSizeClass, viewModel = viewModel)
            }
        }
    }
}