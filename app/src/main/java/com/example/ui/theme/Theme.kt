package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.example.data.model.ThemeCustomConfig
import com.example.data.model.ThemePreset

// Preset 1: Soft Pastel
val SoftPastelColorScheme = lightColorScheme(
  primary = PastelPinkPrimary,
  onPrimary = PastelOnPrimary,
  primaryContainer = PastelSurfaceVariant,
  onPrimaryContainer = PastelPinkPrimary,
  secondary = PastelLavenderSecondary,
  onSecondary = Color.White,
  secondaryContainer = PastelSurfaceVariant,
  onSecondaryContainer = PastelLavenderSecondary,
  tertiary = PastelSkyTertiary,
  onTertiary = Color.White,
  background = PastelBackground,
  onBackground = PastelOnBackground,
  surface = PastelSurface,
  onSurface = PastelOnSurface,
  surfaceVariant = PastelSurfaceVariant,
  onSurfaceVariant = PastelOnSurface.copy(alpha = 0.75f),
  outline = PastelOutline
)

// Preset 2: Dark Mode Aesthetic
val DarkAestheticColorScheme = darkColorScheme(
  primary = DarkNeonPurplePrimary,
  onPrimary = DarkAestheticOnPrimary,
  primaryContainer = DarkAestheticSurfaceVariant,
  onPrimaryContainer = DarkNeonPurplePrimary,
  secondary = DarkNeonPinkSecondary,
  onSecondary = Color.White,
  secondaryContainer = DarkAestheticSurfaceVariant,
  onSecondaryContainer = DarkNeonPinkSecondary,
  tertiary = DarkNeonCyanTertiary,
  onTertiary = Color.Black,
  background = DarkAestheticBackground,
  onBackground = DarkAestheticOnBackground,
  surface = DarkAestheticSurface,
  onSurface = DarkAestheticOnSurface,
  surfaceVariant = DarkAestheticSurfaceVariant,
  onSurfaceVariant = DarkAestheticOnSurface.copy(alpha = 0.75f),
  outline = DarkAestheticOutline
)

// Preset 3: Minimalist Monochrome
val MinimalistMonoColorScheme = lightColorScheme(
  primary = MonoPrimary,
  onPrimary = MonoOnPrimary,
  primaryContainer = MonoSurfaceVariant,
  onPrimaryContainer = MonoPrimary,
  secondary = MonoSecondary,
  onSecondary = Color.White,
  secondaryContainer = MonoSurfaceVariant,
  onSecondaryContainer = MonoSecondary,
  tertiary = MonoAccentTertiary,
  onTertiary = Color.White,
  background = MonoBackground,
  onBackground = MonoOnBackground,
  surface = MonoSurface,
  onSurface = MonoOnSurface,
  surfaceVariant = MonoSurfaceVariant,
  onSurfaceVariant = MonoOnSurface.copy(alpha = 0.75f),
  outline = MonoOutline
)

fun createCustomColorScheme(config: ThemeCustomConfig): ColorScheme {
  val primary = parseColorSafe(config.primaryHex, Color(0xFFC084FC))
  val secondary = parseColorSafe(config.secondaryHex, Color(0xFFFF4081))
  val bg = parseColorSafe(config.backgroundHex, if (config.isDark) Color(0xFF0D0B14) else Color(0xFFF8F9FA))
  val surface = parseColorSafe(config.surfaceHex, if (config.isDark) Color(0xFF171424) else Color.White)
  val onSurface = if (config.isDark) Color(0xFFF1F5F9) else Color(0xFF0F172A)

  return if (config.isDark) {
    darkColorScheme(
      primary = primary,
      onPrimary = Color.Black,
      secondary = secondary,
      onSecondary = Color.White,
      background = bg,
      onBackground = onSurface,
      surface = surface,
      onSurface = onSurface,
      surfaceVariant = surface,
      outline = primary.copy(alpha = 0.35f)
    )
  } else {
    lightColorScheme(
      primary = primary,
      onPrimary = Color.White,
      secondary = secondary,
      onSecondary = Color.White,
      background = bg,
      onBackground = onSurface,
      surface = surface,
      onSurface = onSurface,
      surfaceVariant = surface,
      outline = primary.copy(alpha = 0.35f)
    )
  }
}

@Composable
fun AuStoryBuilderTheme(
  themePreset: ThemePreset = ThemePreset.SOFT_PASTEL,
  customConfig: ThemeCustomConfig = ThemeCustomConfig(),
  content: @Composable () -> Unit
) {
  val colorScheme = when (themePreset) {
    ThemePreset.SOFT_PASTEL -> SoftPastelColorScheme
    ThemePreset.DARK_AESTHETIC -> DarkAestheticColorScheme
    ThemePreset.MINIMAL_MONOCHROME -> MinimalistMonoColorScheme
    ThemePreset.CUSTOM -> createCustomColorScheme(customConfig)
  }

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}

// Retained for backward-compatibility with template and unit tests
@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = true,
  content: @Composable () -> Unit,
) {
  val colorScheme = if (darkTheme) DarkAestheticColorScheme else SoftPastelColorScheme
  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
