package com.example.ui.screens.plot

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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.CardStatus
import com.example.data.model.CharacterItem
import com.example.data.model.PlotCardItem
import com.example.data.model.PlotStage

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddSceneDialog(
  initialCard: PlotCardItem? = null,
  characters: List<CharacterItem>,
  projectId: String,
  defaultStage: PlotStage = PlotStage.OUTLINE,
  onDismiss: () -> Unit,
  onSave: (PlotCardItem) -> Unit
) {
  var title by remember { mutableStateOf(initialCard?.title ?: "") }
  var summary by remember { mutableStateOf(initialCard?.summary ?: "") }
  var stage by remember { mutableStateOf(initialCard?.stage ?: defaultStage) }
  var status by remember { mutableStateOf(initialCard?.status ?: CardStatus.DRAFT) }
  var sequenceNumber by remember { mutableStateOf((initialCard?.sequenceNumber ?: 1).toString()) }
  var estimatedWords by remember { mutableStateOf((initialCard?.estimatedWords ?: 800).toString()) }
  var notes by remember { mutableStateOf(initialCard?.notes ?: "") }

  val selectedCharacterIds = remember {
    mutableStateListOf<String>().apply {
      addAll(initialCard?.characterIds ?: emptyList())
    }
  }

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
          text = if (initialCard == null) "Tambah Adegan / Bab" else "Edit Kartu Plot",
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
        // Judul Adegan
        OutlinedTextField(
          value = title,
          onValueChange = {
            title = it
            showError = false
          },
          label = { Text("Judul Adegan / Bab *") },
          placeholder = { Text("Misal: Bab 1: Pesanan Kopi yang Tertukar") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth(),
          isError = showError && title.isBlank()
        )

        // Deskripsi Singkat
        OutlinedTextField(
          value = summary,
          onValueChange = { summary = it },
          label = { Text("Deskripsi Singkat / Alur Adegan") },
          placeholder = { Text("Tuliskan poin penting apa yang terjadi di adegan ini...") },
          minLines = 3,
          maxLines = 5,
          modifier = Modifier.fillMaxWidth()
        )

        // Kolom Babak / Stage
        Text(
          text = "Kolom Babak Cerita:",
          style = MaterialTheme.typography.labelMedium,
          fontWeight = FontWeight.SemiBold
        )
        FlowRow(
          horizontalArrangement = Arrangement.spacedBy(6.dp),
          verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          PlotStage.values().forEach { s ->
            FilterChip(
              selected = stage == s,
              onClick = { stage = s },
              label = { Text(s.title, style = MaterialTheme.typography.labelSmall) }
            )
          }
        }

        // Status Kartu
        Text(
          text = "Status Pengerjaan:",
          style = MaterialTheme.typography.labelMedium,
          fontWeight = FontWeight.SemiBold
        )
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          CardStatus.values().forEach { st ->
            FilterChip(
              selected = status == st,
              onClick = { status = st },
              label = { Text(st.label) }
            )
          }
        }

        // Characters involved
        if (characters.isNotEmpty()) {
          Text(
            text = "Karakter yang Terlibat:",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold
          )
          FlowRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            characters.forEach { char ->
              val isSelected = selectedCharacterIds.contains(char.id)
              FilterChip(
                selected = isSelected,
                onClick = {
                  if (isSelected) selectedCharacterIds.remove(char.id)
                  else selectedCharacterIds.add(char.id)
                },
                label = { Text(char.stageName, style = MaterialTheme.typography.labelSmall) }
              )
            }
          }
        }

        // Urutan Timeline & Target Kata
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          OutlinedTextField(
            value = sequenceNumber,
            onValueChange = { sequenceNumber = it.filter { c -> c.isDigit() } },
            label = { Text("Urutan Bab") },
            singleLine = true,
            modifier = Modifier.weight(1f)
          )
          OutlinedTextField(
            value = estimatedWords,
            onValueChange = { estimatedWords = it.filter { c -> c.isDigit() } },
            label = { Text("Est. Kata") },
            singleLine = true,
            modifier = Modifier.weight(1f)
          )
        }

        // Catatan Penulis
        OutlinedTextField(
          value = notes,
          onValueChange = { notes = it },
          label = { Text("Catatan / Reminder Penulis") },
          placeholder = { Text("Misal: Jangan lupa tekankan suasana hujan dan aroma karamel.") },
          singleLine = true,
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

          val seq = sequenceNumber.toIntOrNull() ?: 1
          val est = estimatedWords.toIntOrNull() ?: 500

          val card = PlotCardItem(
            id = initialCard?.id ?: java.util.UUID.randomUUID().toString(),
            projectId = projectId,
            stage = stage,
            status = status,
            title = title.trim(),
            summary = summary.trim(),
            sequenceNumber = seq,
            characterIds = selectedCharacterIds.toList(),
            notes = notes.trim(),
            estimatedWords = est
          )
          onSave(card)
        }
      ) {
        Text("Simpan Adegan")
      }
    },
    dismissButton = {
      OutlinedButton(onClick = onDismiss) {
        Text("Batal")
      }
    }
  )
}
