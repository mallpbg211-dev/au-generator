package com.example.ui.screens.relationship

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.CharacterItem
import com.example.data.model.RelationshipItem
import com.example.ui.theme.parseColorSafe

val presetRelationshipTags = listOf(
  "OTP" to "#FF4081",
  "Rival" to "#FF5722",
  "Sahabat" to "#4CAF50",
  "Found Family" to "#9C27B0",
  "Poly" to "#E040FB",
  "Enemies to Lovers" to "#FF9800",
  "Secret Crush" to "#EC407A",
  "Mentor / Senior" to "#2196F3",
  "Custom" to "#7C4DFF"
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddRelationshipDialog(
  initialRelationship: RelationshipItem? = null,
  characters: List<CharacterItem>,
  projectId: String,
  defaultFromCharId: String? = null,
  defaultToCharId: String? = null,
  onDismiss: () -> Unit,
  onSave: (RelationshipItem) -> Unit
) {
  var fromId by remember {
    mutableStateOf(
      initialRelationship?.fromCharacterId
        ?: defaultFromCharId
        ?: characters.firstOrNull()?.id
        ?: ""
    )
  }
  var toId by remember {
    mutableStateOf(
      initialRelationship?.toCharacterId
        ?: defaultToCharId
        ?: characters.getOrNull(1)?.id
        ?: characters.firstOrNull()?.id
        ?: ""
    )
  }

  var selectedTag by remember {
    mutableStateOf(
      if (initialRelationship == null) "OTP"
      else if (presetRelationshipTags.any { it.first == initialRelationship.tag }) initialRelationship.tag
      else "Custom"
    )
  }
  var customTagText by remember {
    mutableStateOf(
      if (initialRelationship != null && !presetRelationshipTags.any { it.first == initialRelationship.tag }) initialRelationship.tag
      else ""
    )
  }
  var notes by remember { mutableStateOf(initialRelationship?.notes ?: "") }
  var colorHex by remember {
    mutableStateOf(
      initialRelationship?.colorHex ?: "#FF4081"
    )
  }

  var expandedFrom by remember { mutableStateOf(false) }
  var expandedTo by remember { mutableStateOf(false) }
  var errorText by remember { mutableStateOf<String?>(null) }

  val fromChar = characters.find { it.id == fromId }
  val toChar = characters.find { it.id == toId }

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
          text = if (initialRelationship == null) "Hubungkan Karakter" else "Edit Hubungan",
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
        // Karakter 1 Selector
        ExposedDropdownMenuBox(
          expanded = expandedFrom,
          onExpandedChange = { expandedFrom = !expandedFrom }
        ) {
          OutlinedTextField(
            value = fromChar?.let { "${it.stageName} (${it.groupName})" } ?: "Pilih Karakter 1",
            onValueChange = {},
            readOnly = true,
            label = { Text("Karakter Pertama") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedFrom) },
            modifier = Modifier
              .fillMaxWidth()
              .menuAnchor()
          )
          ExposedDropdownMenu(
            expanded = expandedFrom,
            onDismissRequest = { expandedFrom = false }
          ) {
            characters.forEach { char ->
              DropdownMenuItem(
                text = { Text("${char.stageName} (${char.groupName}) - ${char.gender}") },
                onClick = {
                  fromId = char.id
                  expandedFrom = false
                  errorText = null
                }
              )
            }
          }
        }

        // Swap button
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
          IconButton(
            onClick = {
              val temp = fromId
              fromId = toId
              toId = temp
            }
          ) {
            Icon(Icons.Default.SwapHoriz, contentDescription = "Tukar Karakter", tint = MaterialTheme.colorScheme.primary)
          }
        }

        // Karakter 2 Selector
        ExposedDropdownMenuBox(
          expanded = expandedTo,
          onExpandedChange = { expandedTo = !expandedTo }
        ) {
          OutlinedTextField(
            value = toChar?.let { "${it.stageName} (${it.groupName})" } ?: "Pilih Karakter 2",
            onValueChange = {},
            readOnly = true,
            label = { Text("Karakter Kedua") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedTo) },
            modifier = Modifier
              .fillMaxWidth()
              .menuAnchor()
          )
          ExposedDropdownMenu(
            expanded = expandedTo,
            onDismissRequest = { expandedTo = false }
          ) {
            characters.forEach { char ->
              DropdownMenuItem(
                text = { Text("${char.stageName} (${char.groupName}) - ${char.gender}") },
                onClick = {
                  toId = char.id
                  expandedTo = false
                  errorText = null
                }
              )
            }
          }
        }

        // Tag Hubungan
        Text(
          text = "Pilih Tag / Dinamika Hubungan:",
          style = MaterialTheme.typography.labelMedium,
          fontWeight = FontWeight.SemiBold
        )
        FlowRow(
          horizontalArrangement = Arrangement.spacedBy(6.dp),
          verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          presetRelationshipTags.forEach { (tag, tagColor) ->
            FilterChip(
              selected = selectedTag == tag,
              onClick = {
                selectedTag = tag
                colorHex = tagColor
              },
              label = { Text(tag, style = MaterialTheme.typography.labelSmall) }
            )
          }
        }

        if (selectedTag == "Custom") {
          OutlinedTextField(
            value = customTagText,
            onValueChange = { customTagText = it },
            label = { Text("Tuliskan Tag Kustom") },
            placeholder = { Text("Misal: Mantan Trainee, Rekan Duet, Roommate") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )
        }

        // Story Notes
        OutlinedTextField(
          value = notes,
          onValueChange = { notes = it },
          label = { Text("Catatan Dinamika & Cerita Hubungan") },
          placeholder = { Text("Misal: Rival sejak awal debut, sering cekcok di depan kamera tapi saling peduli di balik layar.") },
          minLines = 3,
          maxLines = 5,
          modifier = Modifier.fillMaxWidth()
        )

        errorText?.let {
          Text(text = it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }
      }
    },
    confirmButton = {
      Button(
        onClick = {
          if (fromId.isBlank() || toId.isBlank()) {
            errorText = "Pilih dua karakter untuk dihubungkan."
            return@Button
          }
          if (fromId == toId) {
            errorText = "Karakter tidak bisa dihubungkan dengan dirinya sendiri."
            return@Button
          }

          val finalTag = if (selectedTag == "Custom") {
            customTagText.ifBlank { "Hubungan Kustom" }
          } else {
            selectedTag
          }

          val relationship = RelationshipItem(
            id = initialRelationship?.id ?: java.util.UUID.randomUUID().toString(),
            projectId = projectId,
            fromCharacterId = fromId,
            toCharacterId = toId,
            tag = finalTag,
            notes = notes.trim(),
            colorHex = colorHex
          )
          onSave(relationship)
        }
      ) {
        Text("Simpan Hubungan")
      }
    },
    dismissButton = {
      OutlinedButton(onClick = onDismiss) {
        Text("Batal")
      }
    }
  )
}
