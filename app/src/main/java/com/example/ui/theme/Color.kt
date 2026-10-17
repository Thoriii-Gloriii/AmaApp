package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// AmaApp brand palette (UI Design & Developer Guide). Declared first so legacy aliases below initialise correctly.
val AmaAvocado = Color(0xFF263F27)
val AmaAvocadoDeep = Color(0xFF1B2E1C)
val AmaCream = Color(0xFFF7F3E7)
val AmaCardCream = Color(0xFFFFFDF5)
val AmaOlive = Color(0xFF789047)
val AmaOliveDark = Color(0xFF5B6F35)
val AmaOliveLight = Color(0xFFA9C27A)
val AmaSage = Color(0xFFE9E8D4)
val AmaMutedGreen = Color(0xFF526354)
val AmaWarmBorder = Color(0xFFE5DCC8)
val AmaGold = Color(0xFF9B7920)
val AmaVerified = Color(0xFF3F7A3A)
val AmaOnDarkMuted = Color(0xFFC9CFB5)

// Legacy names remapped so existing references compile and take the new look
val CyanPrimary = AmaAvocado
val CyanPrimaryDark = AmaOliveDark
val CyanPrimaryLight = AmaMutedGreen
val VioletSecondary = AmaOlive
val VioletSecondaryDark = AmaOliveDark
val VioletSecondaryLight = AmaOliveDark
val EmeraldTertiary = AmaVerified
val EmeraldDark = AmaVerified

// Legacy surface and text tokens (unused by the new theme, kept so nothing breaks)
val DarkBg = Color(0xFF0B0F19)
val DarkSurface = Color(0xFF131B2E)
val DarkSurfaceElevated = Color(0xFF1B253F)
val DarkSurfaceBorder = Color(0xFF263353)
val DarkTextPrimary = Color(0xFFF1F5F9)
val DarkTextSecondary = Color(0xFF94A3B8)
val DarkTextMuted = Color(0xFF64748B)
val LightBg = Color(0xFFF8FAFC)
val LightSurface = Color(0xFFFFFFFF)
val LightSurfaceElevated = Color(0xFFF1F5F9)
val LightSurfaceBorder = Color(0xFFE2E8F0)
val LightTextPrimary = Color(0xFF0F172A)
val LightTextSecondary = Color(0xFF475569)
val LightTextMuted = Color(0xFF94A3B8)

// Status & accent colours
val StatusBug = Color(0xFFC0443A)
val StatusFeature = Color(0xFFB07A10)
val StatusResolved = AmaVerified
val StatusUnderReview = Color(0xFF2F6F7C)
val BlockchainGold = AmaGold
val AppleBrandColor = Color(0xFF007AFF)
val GooglePlayBrandColor = Color(0xFF00865F)
