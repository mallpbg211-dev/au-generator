package com.example.ui.screens.templates

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BookmarkAdded
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AuTemplateCatalog
import com.example.data.model.AuTemplatePreset
import com.example.data.model.CardStatus
import com.example.data.model.PlotCardItem
import com.example.data.model.PlotStage
import com.example.data.model.ProjectItem
import java.util.UUID

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AuTemplateScreen(
  project: ProjectItem,
  onApplyTemplateToProject: (settingName: String, premise: String, tropes: List<String>) -> Unit,
  onAddCardToPlotBoard: (PlotCardItem) -> Unit,
  onShowMessage: (String) -> Unit
) {
  var showCustomDialog by remember { mutableStateOf(false) }
  var customSettingName by remember { mutableStateOf("") }
  var customPremise by remember { mutableStateOf("") }
  var customTropesText by remember { mutableStateOf("") }

  LazyColumn(
    modifier = Modifier.fillMaxSize(),
    contentPadding = PaddingValues(16.dp, 12.dp, 16.dp, 96.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    item {
      Column {
        Text(
          text = "AU Setting Templates",
          style = MaterialTheme.typography.headlineSmall,
          fontWeight = FontWeight.Bold
        )
        Text(
          text = "Pilih formula dunia fanfiction yang kamu sukai atau buat kustom",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
    }

    // Active project setting indicator card
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
          containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
        )
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .size(44.dp)
              .clip(CircleShape)
              .background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center
          ) {
            Icon(Icons.Default.BookmarkAdded, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary)
          }

          Spacer(modifier = Modifier.width(12.dp))

          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "Setting Aktif di Proyek Ini:",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
              text = project.auSettingName,
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.primary
            )
            if (project.auSettingPremise.isNotBlank()) {
              Text(
                text = project.auSettingPremise,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2
              )
            }
          }
        }
      }
    }

    // Custom AU Card Button
    item {
      OutlinedCard(
        onClick = { showCustomDialog = !showCustomDialog },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text("🎨", style = MaterialTheme.typography.titleLarge)
              Spacer(modifier = Modifier.width(10.dp))
              Column {
                Text(
                  text = "Custom AU (Tulis Sendiri dari Nol)",
                  style = MaterialTheme.typography.titleMedium,
                  fontWeight = FontWeight.Bold
                )
                Text(
                  text = "Definisikan dunia, aturan unik, dan trope ceritamu sendiri",
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }
            Icon(
              imageVector = Icons.Default.Edit,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.primary
            )
          }

          if (showCustomDialog) {
            Spacer(modifier = Modifier.height(14.dp))
            OutlinedTextField(
              value = customSettingName,
              onValueChange = { customSettingName = it },
              label = { Text("Nama Setting Kustom") },
              placeholder = { Text("Misal: Cyberpunk Seoul AU / Secret Agent AU") },
              singleLine = true,
              modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
              value = customPremise,
              onValueChange = { customPremise = it },
              label = { Text("Premis & Aturan Dunia") },
              placeholder = { Text("Jelaskan situasi awal dunia cerita ini...") },
              minLines = 3,
              maxLines = 4,
              modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
              value = customTropesText,
              onValueChange = { customTropesText = it },
              label = { Text("Trope Kunci (pisahkan dengan koma)") },
              placeholder = { Text("Fake dating, Slow burn, Mutual pining, Secret identity") },
              singleLine = true,
              modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))
            Button(
              onClick = {
                if (customSettingName.isNotBlank()) {
                  val tropesList = customTropesText.split(",").map { it.trim() }.filter { it.isNotBlank() }
                  onApplyTemplateToProject(customSettingName, customPremise, tropesList)

                  // Also add as card 1 on Plot Board
                  val plotCard = PlotCardItem(
                    id = UUID.randomUUID().toString(),
                    projectId = project.id,
                    stage = PlotStage.OUTLINE,
                    status = CardStatus.DRAFT,
                    title = "Premis Awal: $customSettingName",
                    summary = customPremise,
                    sequenceNumber = 1,
                    estimatedWords = 1000
                  )
                  onAddCardToPlotBoard(plotCard)
                  onShowMessage("Setting Kustom berhasil diterapkan ke Plot Board!")
                  showCustomDialog = false
                }
              },
              modifier = Modifier.fillMaxWidth()
            ) {
              Text("Terapkan ke Proyek & Plot Board")
            }
          }
        }
      }
    }

    item {
      Text(
        text = "Katalog Template Siap Pakai:",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(top = 8.dp)
      )
    }

    items(AuTemplateCatalog.presets, key = { it.id }) { preset ->
      val isCurrent = project.auSettingName.contains(preset.name, ignoreCase = true)

      TemplatePresetCard(
        preset = preset,
        isCurrent = isCurrent,
        onApply = {
          onApplyTemplateToProject(preset.name, preset.premise, preset.tropes)

          // Direct creation of Card 1 on Plot Board
          val plotCard = PlotCardItem(
            id = UUID.randomUUID().toString(),
            projectId = project.id,
            stage = PlotStage.OUTLINE,
            status = CardStatus.DRAFT,
            title = "Prolog: ${preset.name}",
            summary = preset.premise,
            sequenceNumber = 1,
            estimatedWords = 1200
          )
          onAddCardToPlotBoard(plotCard)
          onShowMessage("Template '${preset.name}' diterapkan & dijadikan kartu #1 di Plot Board!")
        }
      )
    }
  }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TemplatePresetCard(
  preset: AuTemplatePreset,
  isCurrent: Boolean,
  onApply: () -> Unit
) {
  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(18.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surface
    )
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(text = preset.iconEmoji, style = MaterialTheme.typography.headlineSmall)
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Text(
              text = preset.name,
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = preset.tagline,
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }

        if (isCurrent) {
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(8.dp))
              .background(MaterialTheme.colorScheme.primaryContainer)
              .padding(horizontal = 8.dp, vertical = 4.dp)
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.primary)
              Spacer(modifier = Modifier.width(4.dp))
              Text("Aktif", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
            }
          }
        }
      }

      // Premise text
      Text(
        text = preset.premise,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f)
      )

      // Key Tropes Chips
      Text(
        text = "Trope Kunci:",
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.secondary
      )
      FlowRow(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        preset.tropes.forEach { trope ->
          SuggestionChip(
            onClick = {},
            label = { Text("⚡ $trope", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp)) },
            modifier = Modifier.height(26.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(4.dp))

      // Apply button
      Button(
        onClick = onApply,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
      ) {
        Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(if (isCurrent) "Terapkan Ulang ke Plot Board" else "Gunakan Template di Plot Board")
      }
    }
  }
}
