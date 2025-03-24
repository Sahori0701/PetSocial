package com.example.proyectopoli.ui.theme


import androidx.compose.ui.text.font.Font
import androidx.compose.ui.unit.sp
import com.example.proyectopoli.R
import androidx.compose.material3.Typography
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val Typography = Typography(
        bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.25.sp
    ),
    // Estilo para el cuerpo de texto mediano
    bodyMedium = TextStyle(
        fontFamily = FontFamily.Serif, // Usar Serif para un estilo más elegante
        fontWeight = FontWeight.W700, // Negrita
        color = Color(0xFF333333),
        fontSize = 20.sp, // Tamaño de la letra
        lineHeight = 28.sp, // Espaciado entre líneas
        letterSpacing = 0.5.sp // Espaciado entre letras
    ),
    // Estilo para el cuerpo de texto pequeño
    bodySmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp
    ),
    // Estilo para títulos grandes
    headlineLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = 0.sp
    ),
    // Estilo para títulos medianos
    headlineMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp
    ),
    // Estilo para títulos pequeños
    headlineSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.sp
    ),
    // Estilo para subtítulos
    titleMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp
    ),
    // Estilo para etiquetas o textos pequeños
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
)


val PetSocialTypography = TextStyle(
    fontFamily = FontFamily.Serif, // Usar Serif para un estilo más elegante
    fontWeight = FontWeight.Bold, // Negrita
    fontSize = 22.sp, // Tamaño de la letra
    lineHeight = 28.sp, // Espaciado entre líneas
    letterSpacing = 0.5.sp // Espaciado entre letras
)