package com.example.ui.screens.theme_settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ThemeCustomConfig
import com.example.data.model.ThemePreset
import com.example.ui.theme.DarkAestheticBackground
import com.example.ui.theme.DarkNeonPinkSecondary
import com.example.ui.theme.DarkNeonPurplePrimary
import com.example.ui.theme.MonoAccentTertiary
import com.example.ui.theme.MonoBackground
import com.example.ui.theme.MonoPrimary
import com.example.ui.theme.PastelBackground
import com.example.ui.theme.PastelLavenderSecondary
import com.example.ui.theme.PastelPinkPrimary
import com.example.ui.theme.parseColorSafe

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeSettingsSheet(
  currentPreset: ThemePreset,
  customConfig: ThemeCustomConfig,
  onSelectPreset: (ThemePreset) -> Unit,
  onUpdateCustomConfig: (ThemeCustomConfig) -> Unit,
  onDismiss: () -> Unit
) {
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = MaterialTheme.colorScheme.surface
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 24.dp)
        .padding(bottom = 32.dp)
        .verticalScroll(rememberScrollState()),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        Icon(
          imageVector = Icons.Default.Palette,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.primary,
          modifier = Modifier.size(28.dp)
        )
        Column {
          Text(
            text = "Pengaturan Tema & Warna",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = "Pilih nuansa estetika yang menemani proses menulismu",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }

      Spacer(modifier = Modifier.height(4.dp))

      // Preset 1: Soft Pastel
      PresetThemeCard(
        title = ThemePreset.SOFT_PASTEL.title,
        description = ThemePreset.SOFT_PASTEL.description,
        isSelected = currentPreset == ThemePreset.SOFT_PASTEL,
        colors = listOf(PastelPinkPrimary, PastelLavenderSecondary, PastelBackground),
        onClick = { onSelectPreset(ThemePreset.SOFT_PASTEL) }
      )

      // Preset 2: Dark Mode Aesthetic
      PresetThemeCard(
        title = ThemePreset.DARK_AESTHETIC.title,
        description = ThemePreset.DARK_AESTHETIC.description,
        isSelected = currentPreset == ThemePreset.DARK_AESTHETIC,
        colors = listOf(DarkNeonPurplePrimary, DarkNeonPinkSecondary, DarkAestheticBackground),
        onClick = { onSelectPreset(ThemePreset.DARK_AESTHETIC) }
      )

      // Preset 3: Minimalis Monokrom
      PresetThemeCard(
        title = ThemePreset.MINIMAL_MONOCHROME.title,
        description = ThemePreset.MINIMAL_MONOCHROME.description,
        isSelected = currentPreset == ThemePreset.MINIMAL_MONOCHROME,
        colors = listOf(MonoPrimary, MonoAccentTertiary, MonoBackground),
        onClick = { onSelectPreset(ThemePreset.MINIMAL_MONOCHROME) }
      )

      // Preset 4: Custom Theme
      PresetThemeCard(
        title = ThemePreset.CUSTOM.title,
        description = ThemePreset.CUSTOM.description,
        isSelected = currentPreset == ThemePreset.CUSTOM,
        colors = listOf(
          parseColorSafe(customConfig.primaryHex, Color(0xFFC084FC)),
          parseColorSafe(customConfig.secondaryHex, Color(0xFFFF4081)),
          parseColorSafe(customConfig.backgroundHex, Color(0xFF0D0B14))
        ),
        onClick = { onSelectPreset(ThemePreset.CUSTOM) }
      )

      if (currentPreset == ThemePreset.CUSTOM) {
        CustomThemeEditor(
          config = customConfig,
          onConfigChange = onUpdateCustomConfig
        )
      }
    }
  }
}

@Composable
private fun PresetThemeCard(
  title: String,
  description: String,
  isSelected: Boolean,
  colors: List<Color>,
  onClick: () -> Unit
) {
  val borderColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
  val borderWidth = if (isSelected) 2.dp else 1.dp

  OutlinedCard(
    onClick = onClick,
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.outlinedCardColors(
      containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)
      else MaterialTheme.colorScheme.surface
    ),
    border = CardDefaults.outlinedCardBorder().copy(
      brush = androidx.compose.ui.graphics.SolidColor(borderColor),
      width = borderWidth
    )
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Column(modifier = Modifier.weight(1f)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
          )
          if (isSelected) {
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
              imageVector = Icons.Default.Check,
              contentDescription = "Dipilih",
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(18.dp)
            )
          }
        }
        Text(
          text = description,
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }

      Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        colors.forEach { color ->
          Box(
            modifier = Modifier
              .size(24.dp)
              .clip(CircleShape)
              .background(color)
              .border(1.dp, Color.White.copy(alpha = 0.5f), CircleShape)
          )
        }
      }
    }
  }
}

@Composable
private fun CustomThemeEditor(
  config: ThemeCustomConfig,
  onConfigChange: (ThemeCustomConfig) -> Unit
) {
  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
    )
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      Text(
        text = "Kustomisasi Palet Warna Bebas",
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary
      )

      // Curated Aesthetic Palettes to quickly apply
      Text(
        text = "Preset Palet Cepat:",
        style = MaterialTheme.typography.labelMedium
      )
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        AestheticPaletteChip("Sunset Seoul", "#FF7043", "#FF4081", "#120B1A", "#1D1328", true) { p, s, bg, sf, dark ->
          onConfigChange(ThemeCustomConfig(p, s, bg, sf, dark))
        }
        AestheticPaletteChip("Cyber Cyan", "#00E5FF", "#D500F9", "#080F1A", "#101D30", true) { p, s, bg, sf, dark ->
          onConfigChange(ThemeCustomConfig(p, s, bg, sf, dark))
        }
      }
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        AestheticPaletteChip("Mint Milk", "#26A69A", "#80CBC4", "#F0F7F6", "#FFFFFF", false) { p, s, bg, sf, dark ->
          onConfigChange(ThemeCustomConfig(p, s, bg, sf, dark))
        }
        AestheticPaletteChip("Cherry Velvet", "#D81B60", "#AD1457", "#1A0A12", "#2B101E", true) { p, s, bg, sf, dark ->
          onConfigChange(ThemeCustomConfig(p, s, bg, sf, dark))
        }
      }

      // Dark Mode Switch for Custom
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Text(
          text = "Dasar Tampilan Gelap (Dark Mode)",
          style = MaterialTheme.typography.bodyMedium
        )
        Switch(
          checked = config.isDark,
          onCheckedChange = { isDark ->
            val newBg = if (isDark) "#0D0B14" else "#F8F9FA"
            val newSurface = if (isDark) "#171424" else "#FFFFFF"
            onConfigChange(config.copy(isDark = isDark, backgroundHex = newBg, surfaceHex = newSurface))
          }
        )
      }

      // Primary Color Swatch Selection
      ColorRowPicker(
        label = "Warna Utama (Primary)",
        selectedHex = config.primaryHex,
        options = listOf("#C084FC", "#FF4081", "#00E5FF", "#FFB300", "#4CAF50", "#E91E63", "#6366F1"),
        onSelect = { onConfigChange(config.copy(primaryHex = it)) }
      )

      // Secondary Color Swatch Selection
      ColorRowPicker(
        label = "Warna Aksen (Secondary)",
        selectedHex = config.secondaryHex,
        options = listOf("#F43F5E", "#A855F7", "#38BDF8", "#F59E0B", "#10B981", "#EC4899", "#8B5CF6"),
        onSelect = { onConfigChange(config.copy(secondaryHex = it)) }
      )
    }
  }
}

@Composable
private fun AestheticPaletteChip(
  name: String,
  p: String,
  s: String,
  bg: String,
  sf: String,
  dark: Boolean,
  onClick: (String, String, String, String, Boolean) -> Unit
) {
  OutlinedCard(
    onClick = { onClick(p, s, bg, sf, dark) },
    shape = RoundedCornerShape(10.dp),
    modifier = Modifier.height(38.dp)
  ) {
    Row(
      modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      Box(modifier = Modifier.size(12.dp).clip(CircleShape).background(parseColorSafe(p, Color.Magenta)))
      Box(modifier = Modifier.size(12.dp).clip(CircleShape).background(parseColorSafe(s, Color.Cyan)))
      Text(name, style = MaterialTheme.typography.labelSmall)
    }
  }
}

@Composable
private fun ColorRowPicker(
  label: String,
  selectedHex: String,
  options: List<String>,
  onSelect: (String) -> Unit
) {
  Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
    Text(
      text = label,
      style = MaterialTheme.typography.bodySmall,
      color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      options.forEach { hex ->
        val color = parseColorSafe(hex, Color.Gray)
        val isSelected = hex.equals(selectedHex, ignoreCase = true)

        Box(
          modifier = Modifier
            .size(34.dp)
            .clip(CircleShape)
            .background(color)
            .border(
              width = if (isSelected) 3.dp else 1.dp,
              color = if (isSelected) MaterialTheme.colorScheme.onSurface else Color.Transparent,
              shape = CircleShape
            )
            .clickable { onSelect(hex) },
          contentAlignment = Alignment.Center
        ) {
          if (isSelected) {
            Icon(
              imageVector = Icons.Default.Check,
              contentDescription = null,
              tint = if (color.luminance() > 0.5f) Color.Black else Color.White,
              modifier = Modifier.size(18.dp)
            )
          }
        }
      }
    }
  }
}

private fun Color.luminance(): Float {
  return (0.299f * red + 0.587f * green + 0.114f * blue)
}
