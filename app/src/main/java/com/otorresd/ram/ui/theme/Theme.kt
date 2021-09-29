package com.otorresd.ram.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable

private val DarkColorPalette = darkColors(
    primary = Background,
    primaryVariant = TextOrange,
    secondary = Teal200,
    background = Background,
    onPrimary = Background
)

private val LightColorPalette = lightColors(
    primary = Background,
    primaryVariant = TextOrange,
    secondary = Teal200,
    background = Background,
    onPrimary = Background
)

@Composable
fun RAMTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable() () -> Unit) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}