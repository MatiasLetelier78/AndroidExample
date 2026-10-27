package com.example.androidexample.ui.ui.Theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val EsquemaClaro = lightColorScheme(
    primary = VerdeJunta,
    onPrimary = Color.White,
    primaryContainer = VerdeClaro,
    onPrimaryContainer = Color(0xFF0B3D12),
    secondary = GrisAzulado,
    secondaryContainer = GrisAzuladoClaro,
    onSecondaryContainer = Color(0xFF1B2A30),
    tertiary = Ambar,
    background = FondoClaro,
    surface = FondoClaro,
    error = Color(0xFFB3261E),
    errorContainer = Color(0xFFF9DEDC)
)

private val EsquemaOscuro = darkColorScheme(
    primary = VerdeOscuroPrimario,
    onPrimary = Color(0xFF0B3D12),
    primaryContainer = VerdeOscuroContenedor,
    onPrimaryContainer = VerdeClaro,
    secondary = GrisAzuladoClaro,
    tertiary = Ambar,
    background = FondoOscuro,
    surface = FondoOscuro
)

@Composable
fun AndroidExampleTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) EsquemaOscuro else EsquemaClaro,
        typography = Typography(),
        content = content
    )
}