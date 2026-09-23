package com.example.ui.screens.characters

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.CharacterItem

val idolAvatarOptions = listOf(
  "microphone" to Icons.Default.Mic,
  "star" to Icons.Default.Star,
  "heart" to Icons.Default.Favorite,
  "sparkle" to Icons.Default.AutoAwesome,
  "palette" to Icons.Default.Palette,
  "music" to Icons.Default.MusicNote,
  "face" to Icons.Default.Face,
  "person" to Icons.Default.Person
)

val presetPositions = listOf(
  "Leader",
  "Main Vocal",
  "Lead Vocal",
  "Sub Vocal",
  "Main Dancer",
  "Lead Dancer",
  "Main Rapper",
  "Lead Rapper",
  "Visual / Center",
  "Maknae",
  "Producer",
  "Custom"
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CharacterFormDialog(
  initialCharacter: CharacterItem? = null,
  projectId: String,
  onDismiss: () -> Unit,
  onSave: (CharacterItem) -> Unit
) {
  var stageName by remember { mutableStateOf(initialCharacter?.stageName ?: "") }
  var realName by remember { mutableStateOf(initialCharacter?.realName ?: "") }
  var groupName by remember { mutableStateOf(initialCharacter?.groupName ?: "") }
  var selectedPosition by remember {
    mutableStateOf(
      if (initialCharacter == null) "Main Vocal"
      else if (presetPositions.contains(initialCharacter.position)) initialCharacter.position
      else "Custom"
    )
  }
  var customPositionText by remember {
    mutableStateOf(
      if (initialCharacter != null && !presetPositions.contains(initialCharacter.position)) initialCharacter.position
      else ""
    )
  }
  var gender by remember { mutableStateOf(initialCharacter?.gender ?: "Cowok") }
  var avatarKey by remember { mutableStateOf(initialCharacter?.avatarKey ?: "microphone") }
  var roleInAU by remember { mutableStateOf(initialCharacter?.roleInAU ?: "") }
  var traits by remember { mutableStateOf(initialCharacter?.traits ?: "") }

  var showError by remember { mutableStateOf(false) }

  AlertDialog(
    onDismissRequest = onDismiss,
    shape = RoundedCornerShape(24.dp),
    title = {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = if (initialCharacter == null) "Tambah Karakter Idol" else "Edit Profil Karakter",
          style = MaterialTheme.typography.titleLarge,
          fontWeight = FontWeight.Bold
        )
        IconButton(onClick = onDismiss) {
          Icon(Icons.Default.Close, contentDescription = "Tutup")
        }
      }
    },
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        // Visual Reference Avatar Selection
        Text(
          text = "Pilih Ikon Visual Karakter:",
          style = MaterialTheme.typography.labelMedium,
          fontWeight = FontWeight.SemiBold
        )
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          idolAvatarOptions.forEach { (key, icon) ->
            val isSelected = key == avatarKey
            Box(
              modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(
                  if (isSelected) MaterialTheme.colorScheme.primary
                  else MaterialTheme.colorScheme.surfaceVariant
                )
                .border(
                  width = if (isSelected) 2.dp else 1.dp,
                  color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                  shape = CircleShape
                )
                .clickable { avatarKey = key },
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = icon,
                contentDescription = key,
                tint = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(2.dp))

        // Stage Name & Real Name
        OutlinedTextField(
          value = stageName,
          onValueChange = {
            stageName = it
            showError = false
          },
          label = { Text("Nama Panggung (Stage Name) *") },
          placeholder = { Text("Misal: Jungkook, Karina, Wonwoo") },
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("stage_name_input"),
          isError = showError && stageName.isBlank()
        )

        OutlinedTextField(
          value = realName,
          onValueChange = { realName = it },
          label = { Text("Nama Asli (Real Name)") },
          placeholder = { Text("Misal: Jeon Jung-kook, Yu Ji-min") },
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("real_name_input")
        )

        // Group Name
        OutlinedTextField(
          value = groupName,
          onValueChange = { groupName = it },
          label = { Text("Grup Idol *") },
          placeholder = { Text("Misal: BTS, aespa, SEVENTEEN, NewJeans") },
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("group_name_input"),
          isError = showError && groupName.isBlank()
        )

        // Gender Selection (Cowok, Cewek, Lainnya)
        Text(
          text = "Gender Karakter:",
          style = MaterialTheme.typography.labelMedium,
          fontWeight = FontWeight.SemiBold
        )
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          listOf("Cowok", "Cewek", "Lainnya").forEach { g ->
            FilterChip(
              selected = gender == g,
              onClick = { gender = g },
              label = { Text(g) }
            )
          }
        }

        // Position Selection (Preset + Custom)
        Text(
          text = "Posisi di Grup:",
          style = MaterialTheme.typography.labelMedium,
          fontWeight = FontWeight.SemiBold
        )
        FlowRow(
          horizontalArrangement = Arrangement.spacedBy(6.dp),
          verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          presetPositions.forEach { pos ->
            FilterChip(
              selected = selectedPosition == pos,
              onClick = { selectedPosition = pos },
              label = { Text(pos, style = MaterialTheme.typography.labelSmall) }
            )
          }
        }

        if (selectedPosition == "Custom") {
          OutlinedTextField(
            value = customPositionText,
            onValueChange = { customPositionText = it },
            label = { Text("Tuliskan Posisi Kustom") },
            placeholder = { Text("Misal: Composer & Songwriter, Co-Leader") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )
        }

        // Role in this AU
        OutlinedTextField(
          value = roleInAU,
          onValueChange = { roleInAU = it },
          label = { Text("Peran / Profesi di Dunia AU") },
          placeholder = { Text("Misal: Barista shift malam, Mahasiswa seni semester akhir, Pangeran mahkota") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )

        // Custom Traits / Personality
        OutlinedTextField(
          value = traits,
          onValueChange = { traits = it },
          label = { Text("Sifat & Catatan Karakter (Traits/MBTI)") },
          placeholder = { Text("Misal: Dingin di luar tapi perhatian, perfeksionis, MBTI: INTJ, punya trauma petir, suka kucing") },
          minLines = 3,
          maxLines = 5,
          modifier = Modifier.fillMaxWidth()
        )
      }
    },
    confirmButton = {
      Button(
        modifier = Modifier.testTag("save_character_button"),
        onClick = {
          if (stageName.isBlank() || groupName.isBlank()) {
            showError = true
            return@Button
          }

          val finalPosition = if (selectedPosition == "Custom") {
            customPositionText.ifBlank { "Member" }
          } else {
            selectedPosition
          }

          val character = CharacterItem(
            id = initialCharacter?.id ?: java.util.UUID.randomUUID().toString(),
            projectId = projectId,
            stageName = stageName.trim(),
            realName = realName.trim(),
            groupName = groupName.trim(),
            position = finalPosition,
            gender = gender,
            avatarKey = avatarKey,
            roleInAU = roleInAU.trim(),
            traits = traits.trim()
          )
          onSave(character)
        }
      ) {
        Text("Simpan Karakter")
      }
    },
    dismissButton = {
      OutlinedButton(onClick = onDismiss) {
        Text("Batal")
      }
    }
  )
}
