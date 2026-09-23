package com.example.ui.screens.project

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.GenreTonePreset
import com.example.data.model.ProjectItem
import com.example.data.model.ProjectStatus
import com.example.data.model.TonePreset
import java.util.UUID

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProjectFormDialog(
  initialProject: ProjectItem? = null,
  onDismiss: () -> Unit,
  onSave: (ProjectItem) -> Unit
) {
  var title by remember { mutableStateOf(initialProject?.title ?: "") }
  var fandom by remember { mutableStateOf(initialProject?.fandom ?: "") }
  var description by remember { mutableStateOf(initialProject?.description ?: "") }
  var status by remember { mutableStateOf(initialProject?.status ?: ProjectStatus.ONGOING) }
  var tagsText by remember { mutableStateOf(initialProject?.tags?.joinToString(", ") ?: "") }
  var dailyTargetText by remember { mutableStateOf((initialProject?.dailyWordTarget ?: 1000).toString()) }
  var currentWordsText by remember { mutableStateOf((initialProject?.currentWordCount ?: 0).toString()) }

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
          text = if (initialProject == null) "Buat Proyek AU Baru" else "Edit Detail Proyek",
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
        // Judul Cerita AU
        OutlinedTextField(
          value = title,
          onValueChange = {
            title = it
            showError = false
          },
          label = { Text("Judul Proyek AU *") },
          placeholder = { Text("Misal: Midnight Espresso & Secret Letters") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth(),
          isError = showError && title.isBlank()
        )

        // Fandom
        OutlinedTextField(
          value = fandom,
          onValueChange = { fandom = it },
          label = { Text("Fandom / Grup Idol") },
          placeholder = { Text("Misal: BTS, SEVENTEEN, aespa, NCT") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )

        // Status Proyek
        Text(
          text = "Status Pengerjaan:",
          style = MaterialTheme.typography.labelMedium,
          fontWeight = FontWeight.SemiBold
        )
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          ProjectStatus.values().forEach { st ->
            FilterChip(
              selected = status == st,
              onClick = { status = st },
              label = { Text(st.label) }
            )
          }
        }

        // Tags
        OutlinedTextField(
          value = tagsText,
          onValueChange = { tagsText = it },
          label = { Text("Tags / Genre (pisahkan koma)") },
          placeholder = { Text("Coffee Shop, Soulmate, Twitter AU, Fluff") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )

        // Target Kata & Kata Saat Ini
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          OutlinedTextField(
            value = currentWordsText,
            onValueChange = { currentWordsText = it.filter { c -> c.isDigit() } },
            label = { Text("Kata Ditulis") },
            singleLine = true,
            modifier = Modifier.weight(1f)
          )
          OutlinedTextField(
            value = dailyTargetText,
            onValueChange = { dailyTargetText = it.filter { c -> c.isDigit() } },
            label = { Text("Target Harian") },
            singleLine = true,
            modifier = Modifier.weight(1f)
          )
        }

        // Sinopsis Singkat
        OutlinedTextField(
          value = description,
          onValueChange = { description = it },
          label = { Text("Sinopsis / Catatan Cerita") },
          placeholder = { Text("Catatan singkat tentang premis AU ini...") },
          minLines = 3,
          maxLines = 5,
          modifier = Modifier.fillMaxWidth()
        )
      }
    },
    confirmButton = {
      Button(
        onClick = {
          if (title.isBlank()) {
            showError = true
            return@Button
          }

          val tagsList = tagsText.split(",").map { it.trim() }.filter { it.isNotBlank() }
          val words = currentWordsText.toIntOrNull() ?: 0
          val target = dailyTargetText.toIntOrNull() ?: 1000

          val project = initialProject?.copy(
            title = title.trim(),
            fandom = fandom.trim(),
            description = description.trim(),
            status = status,
            tags = tagsList,
            dailyWordTarget = target,
            currentWordCount = words,
            updatedAt = System.currentTimeMillis()
          ) ?: ProjectItem(
            id = UUID.randomUUID().toString(),
            title = title.trim(),
            fandom = fandom.trim(),
            description = description.trim(),
            status = status,
            tags = tagsList,
            dailyWordTarget = target,
            currentWordCount = words,
            auSettingName = "Coffee Shop AU",
            auSettingPremise = "Pertemuan hangat di kafe favorit saat hujan deras",
            tone = TonePreset.SANTAI_GAUL,
            genreTone = GenreTonePreset.FLUFF
          )
          onSave(project)
        }
      ) {
        Text("Simpan Proyek")
      }
    },
    dismissButton = {
      OutlinedButton(onClick = onDismiss) {
        Text("Batal")
      }
    }
  )
}
