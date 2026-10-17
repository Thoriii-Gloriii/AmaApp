package com.example.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val AmaLightColorScheme: ColorScheme = lightColorScheme(
    primary = Avocado,
    onPrimary = Cream,
    primaryContainer = PaleSage,
    onPrimaryContainer = Avocado,
    secondary = OliveDeep,
    onSecondary = Color.White,
    secondaryContainer = PaleSage,
    onSecondaryContainer = Avocado,
    tertiary = Gold,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFF3E9C9),
    onTertiaryContainer = Color(0xFF3B2F08),
    background = Cream,
    onBackground = Avocado,
    surface = CardCream,
    onSurface = Avocado,
    surfaceVariant = PaleSage,
    onSurfaceVariant = MutedGreen,
    surfaceTint = Color.Transparent,
    outline = WarmBorder,
    outlineVariant = WarmBorder,
    error = Danger,
    onError = Color.White,
    errorContainer = Color(0xFFF9DEDC),
    onErrorContainer = Color(0xFF410E0B),
    inverseSurface = Avocado,
    inverseOnSurface = Cream,
    inversePrimary = Olive,
    surfaceBright = CardCream,
    surfaceDim = PaleSage,
    surfaceContainerLowest = CardCream,
    surfaceContainerLow = CardCream,
    surfaceContainer = CardCream,
    surfaceContainerHigh = Color(0xFFF4EFDF),
    surfaceContainerHighest = PaleSage
)

private val AmaDarkColorScheme: ColorScheme = darkColorScheme(
    primary = Olive,
    onPrimary = Color(0xFF14210F),
    primaryContainer = Avocado,
    onPrimaryContainer = Cream,
    secondary = Color(0xFFA9C37A),
    onSecondary = Color(0xFF1B2D0B),
    background = DarkBg,
    onBackground = DarkTextPrimary,
    surface = DarkSurface,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkSurfaceElevated,
    onSurfaceVariant = DarkTextSecondary,
    outline = DarkSurfaceBorder,
    surfaceTint = Color.Transparent
)

/**
 * AmaApp theme. The brand identity is avocado green on warm cream, so the light scheme
 * is the default. Dynamic colour is intentionally not used: it would override the brand.
 */
@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) AmaDarkColorScheme else AmaLightColorScheme,
        typography = Typography,
        content = content
    )
}
