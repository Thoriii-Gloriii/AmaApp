package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val AmaLightColorScheme = lightColorScheme(
    primary = AmaAvocado,
    onPrimary = AmaCream,
    primaryContainer = AmaSage,
    onPrimaryContainer = AmaAvocado,
    secondary = AmaOlive,
    onSecondary = AmaCream,
    secondaryContainer = AmaSage,
    onSecondaryContainer = AmaAvocado,
    tertiary = AmaVerified,
    onTertiary = AmaCream,
    background = AmaCream,
    onBackground = AmaAvocado,
    surface = AmaCardCream,
    onSurface = AmaAvocado,
    surfaceTint = AmaCardCream,
    surfaceVariant = AmaSage,
    onSurfaceVariant = AmaMutedGreen,
    outline = AmaWarmBorder,
    outlineVariant = AmaWarmBorder,
    surfaceContainerLowest = AmaCardCream,
    surfaceContainerLow = AmaCardCream,
    surfaceContainer = AmaCardCream,
    surfaceContainerHigh = AmaSage,
    surfaceContainerHighest = AmaSage,
    error = androidx.compose.ui.graphics.Color(0xFFB3382C),
    onError = AmaCream
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = false, // AmaApp uses a single light, cream brand theme
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = AmaLightColorScheme,
        typography = Typography,
        content = content
    )
}
