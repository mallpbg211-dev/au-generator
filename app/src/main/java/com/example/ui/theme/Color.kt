package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// Default template colors preserved for tests
val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

// 1. Soft Pastel Palette (Pastel Pink, Lavender, Clean White)
val PastelPinkPrimary = Color(0xFFE57399)
val PastelLavenderSecondary = Color(0xFFA594F9)
val PastelSkyTertiary = Color(0xFF81B5E8)
val PastelBackground = Color(0xFFFFF7FA)
val PastelSurface = Color(0xFFFFFFFF)
val PastelSurfaceVariant = Color(0xFFF7ECF4)
val PastelOnPrimary = Color(0xFFFFFFFF)
val PastelOnBackground = Color(0xFF2C1929)
val PastelOnSurface = Color(0xFF2C1929)
val PastelOutline = Color(0xFFE8D0E0)

// 2. Dark Mode Aesthetic Palette (Deep Midnight, Neon Purple, Neon Pink, Electric Cyan)
val DarkNeonPurplePrimary = Color(0xFFC084FC)
val DarkNeonPinkSecondary = Color(0xFFFF4081)
val DarkNeonCyanTertiary = Color(0xFF38BDF8)
val DarkAestheticBackground = Color(0xFF0D0B14)
val DarkAestheticSurface = Color(0xFF171424)
val DarkAestheticSurfaceVariant = Color(0xFF241F36)
val DarkAestheticOnPrimary = Color(0xFF17082A)
val DarkAestheticOnBackground = Color(0xFFF1F5F9)
val DarkAestheticOnSurface = Color(0xFFF1F5F9)
val DarkAestheticOutline = Color(0xFF383056)

// 3. Minimalist Monochrome Palette (White/Slate + Bold Accent)
val MonoPrimary = Color(0xFF18181B)
val MonoSecondary = Color(0xFF52525B)
val MonoAccentTertiary = Color(0xFFE11D48)
val MonoBackground = Color(0xFFF8F9FA)
val MonoSurface = Color(0xFFFFFFFF)
val MonoSurfaceVariant = Color(0xFFEBECEF)
val MonoOnPrimary = Color(0xFFFFFFFF)
val MonoOnBackground = Color(0xFF0F172A)
val MonoOnSurface = Color(0xFF0F172A)
val MonoOutline = Color(0xFFD4D4D8)

// Utility to parse hex color safely
fun parseColorSafe(hex: String, fallback: Color): Color {
  return try {
    val clean = hex.removePrefix("#")
    val fullHex = if (clean.length == 6) "FF$clean" else clean
    Color(fullHex.toLong(16))
  } catch (e: Exception) {
    fallback
  }
}
