package com.chemlab.assistant.ui.navigation

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import com.chemlab.assistant.ui.screens.*
import com.chemlab.assistant.viewmodel.MainViewModel

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Dashboard : Screen("dashboard", "Главная", Icons.Default.Home)
    object Calculator : Screen("calculator", "Калькулятор", Icons.Default.Calculate)
    object Inventory : Screen("inventory", "Склад", Icons.Default.Science)
    object LabWorks : Screen("labworks", "Работы", Icons.Default.CalendarMonth)
}

val items = listOf(Screen.Dashboard, Screen.Calculator, Screen.Inventory, Screen.LabWorks)

@Composable
fun AppNavigation(windowSizeClass: WindowSizeClass, viewModel: MainViewModel) {
    val navController = rememberNavController()
    val isExpanded = windowSizeClass.widthSizeClass != WindowWidthSizeClass.Compact

    Scaffold(
        bottomBar = {
            if (!isExpanded) {
                NavigationBar {
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentDestination = navBackStackEntry?.destination
                    items.forEach { screen ->
                        NavigationBarItem(
                            icon = { Icon(screen.icon, contentDescription = screen.title) },
                            label = { Text(screen.title) },
                            selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Row(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            if (isExpanded) {
                NavigationRail {
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentDestination = navBackStackEntry?.destination
                    items.forEach { screen ->
                        NavigationRailItem(
                            icon = { Icon(screen.icon, contentDescription = screen.title) },
                            label = { Text(screen.title) },
                            selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
            
            NavHost(
                navController = navController,
                startDestination = Screen.Dashboard.route,
                modifier = Modifier.fillMaxSize()
            ) {
                composable(Screen.Dashboard.route) { DashboardScreen(viewModel) }
                composable(Screen.Calculator.route) { CalculatorScreen() }
                composable(Screen.Inventory.route) { InventoryScreen(viewModel) }
                composable(Screen.LabWorks.route) { LabWorksScreen(viewModel) }
            }
        }
    }
}