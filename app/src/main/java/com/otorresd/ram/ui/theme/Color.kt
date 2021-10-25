package com.otorresd.ram.ui.theme

import androidx.compose.material.Colors
import androidx.compose.ui.graphics.Color

// Dark Mode
val DarkBackground = Color(0xFF24282F)
val TransparentGray = Color(0x7A24282F)
val PrimaryOrange = Color(0xFFF08D49)
val DarkCardBackground = Color(0xFF3C3E44)

// Light Mode
val LightBackground = Color.White

// Common
val Colors.cardBackground: Color
    get() = if (isLight) LightBackground else DarkCardBackground

val Colors.topBarBackground: Color
    get() = if(isLight) DarkBackground else PrimaryOrange