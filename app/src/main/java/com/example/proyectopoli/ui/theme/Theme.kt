package com.example.proyectopoli.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// 🎨 Definimos una paleta de colores en tonos de azul oscuro
private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF64B5F6), // Azul más oscuro
    secondary = Color(0xFF1976D2), // Azul intermedio
    tertiary = Color(0xFF64B5F6),  // Azul claro
    surface = Color.Transparent
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFFE2E5FB), // Azul fuerte en modo claro
    secondary = Color(0xFF343F4B), // Azul intermedio
    tertiary = Color(0xFFF5F5F5),
    surface = Color.Transparent// Azul claro
)

@Composable
fun ProyectoPOLITheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,  // ❌ Desactivamos colores dinámicos para mantener el azul oscuro
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    // Configuración de la barra de estado
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb() // Color azul oscuro en la barra de estado
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}