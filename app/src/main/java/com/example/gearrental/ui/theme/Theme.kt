package com.example.gearrental.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Color(0xFF2E7D32),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFC8E6C9),
    onPrimaryContainer = Color(0xFF1B5E20),
    secondary = Color(0xFF6D4C41),
    secondaryContainer = Color(0xFFD7CCC8),
    onSecondaryContainer = Color(0xFF3E2723),
    tertiary = Color(0xFFF57C00),
    tertiaryContainer = Color(0xFFFFE0B2),
    onTertiaryContainer = Color(0xFFE65100),
    error = Color(0xFFB3261E),
    errorContainer = Color(0xFFF9DEDC),
    onErrorContainer = Color(0xFF410E0B)
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF81C784),
    onPrimary = Color(0xFF1B5E20),
    primaryContainer = Color(0xFF2E7D32),
    onPrimaryContainer = Color(0xFFC8E6C9),
    secondary = Color(0xFFBCAAA4),
    secondaryContainer = Color(0xFF4E342E),
    onSecondaryContainer = Color(0xFFD7CCC8),
    tertiary = Color(0xFFFFB74D),
    tertiaryContainer = Color(0xFF8D5300),
    onTertiaryContainer = Color(0xFFFFE0B2),
    error = Color(0xFFF2B8B5),
    errorContainer = Color(0xFF8C1D18),
    onErrorContainer = Color(0xFFF9DEDC)
)

/** Wraps content in Material3 theming, automatically following the system's light/dark setting. */
@Composable
fun GearRentalTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        content = content
    )
}
