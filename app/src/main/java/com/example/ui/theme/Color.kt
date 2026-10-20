package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// AmaApp brand palette (from the AmaApp UI Design & Developer Guide)
val Avocado = Color(0xFF263F27)      // primary buttons, headings, selected nav, banners
val Cream = Color(0xFFF7F3E7)        // main background
val Olive = Color(0xFF789047)        // secondary highlights, decorative waves
val OliveDeep = Color(0xFF5F7A35)    // olive with enough contrast for white text
val PaleSage = Color(0xFFE9E8D4)     // inactive chips, tab backgrounds, subtle fills
val MutedGreen = Color(0xFF526354)   // secondary text, metadata
val WarmBorder = Color(0xFFE5DCC8)   // dividers, card outlines, input borders
val CardCream = Color(0xFFFFFDF5)    // lighter card surfaces
val Gold = Color(0xFF9B7920)         // star ratings
val Danger = Color(0xFFB3261E)
val Success = Color(0xFF2E7D32)

// Legacy names still referenced by older screens (Dashboard, Community, IP Vault,
// Admin, dialogs). They now resolve to the new palette so every screen follows the
// same visual system without rewriting each call site.
val CyanPrimary = Avocado
val CyanPrimaryDark = Color(0xFF1B2D1C)
val CyanPrimaryLight = MutedGreen

val VioletSecondary = OliveDeep
val VioletSecondaryDark = Color(0xFF45591F)
val VioletSecondaryLight = Olive

val EmeraldTertiary = Success
val EmeraldDark = Color(0xFF1B5E20)

// Dark scheme surfaces (avocado based) - used only if dark theme is requested
val DarkBg = Color(0xFF121C13)
val DarkSurface = Color(0xFF1A2A1B)
val DarkSurfaceElevated = Color(0xFF223624)
val DarkSurfaceBorder = Color(0xFF34503A)
val DarkTextPrimary = Color(0xFFF1EEDF)
val DarkTextSecondary = Color(0xFFB9C4B5)
val DarkTextMuted = Color(0xFF8A9A88)

// Light scheme surfaces
val LightBg = Cream
val LightSurface = CardCream
val LightSurfaceElevated = PaleSage
val LightSurfaceBorder = WarmBorder
val LightTextPrimary = Avocado
val LightTextSecondary = MutedGreen
val LightTextMuted = Color(0xFF7C8A7D)

// Status & accent colors (chosen to keep contrast on cream surfaces)
val StatusBug = Danger
val StatusFeature = Color(0xFFB26A00)
val StatusResolved = Success
val StatusUnderReview = Color(0xFF1F6F8B)
val BlockchainGold = Gold
val AppleBrandColor = Color(0xFF007AFF)
val GooglePlayBrandColor = Color(0xFF00865F)

// Splash video background tone (matches the edges of the splash video)
val SplashBackground = Color(0xFFEADFCB)
