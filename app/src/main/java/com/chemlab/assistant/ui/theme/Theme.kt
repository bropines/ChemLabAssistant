package com.chemlab.assistant.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = Indigo600,
    secondary = Indigo500,
    background = Slate50,
    surface = androidx.compose.ui.graphics.Color.White
)

@Composable
fun ChemLabTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        content = content
    )
}
