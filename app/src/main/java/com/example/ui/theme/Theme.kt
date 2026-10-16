package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = EmeraldPrimary,
    onPrimary = Color.White,
    primaryContainer = EmeraldSurfaceCard,
    onPrimaryContainer = TilawaTextPrimary,
    secondary = IslamicGold,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF282415),
    onSecondaryContainer = IslamicGoldSoft,
    background = EmeraldDarkBg,
    onBackground = TilawaTextPrimary,
    surface = EmeraldSurface,
    onSurface = TilawaTextPrimary,
    surfaceVariant = EmeraldSurfaceCard,
    onSurfaceVariant = TilawaTextSecondary,
    outline = EmeraldSurfaceCardBorder,
    error = TilawaError
)

private val LightColorScheme = DarkColorScheme // Tilawa is designed primarily with an atmospheric Islamic emerald palette

@Composable
fun TilawaTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    TilawaTheme(content = content)
}
