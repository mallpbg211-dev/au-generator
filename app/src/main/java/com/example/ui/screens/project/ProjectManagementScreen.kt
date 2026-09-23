package com.example.ui.screens.project

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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FolderSpecial
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.example.data.model.ProjectItem
import com.example.data.model.ProjectStatus

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProjectManagementScreen(
  projects: List<ProjectItem>,
  activeProjectId: String?,
  onSelectProject: (String) -> Unit,
  onSaveProject: (ProjectItem) -> Unit,
  onDeleteProject: (String) -> Unit
) {
  var selectedStatusFilter by remember { mutableStateOf<ProjectStatus?>(null) }
  var isFormOpen by remember { mutableStateOf(false) }
  var editingProject by remember { mutableStateOf<ProjectItem?>(null) }
  var projectToDelete by remember { mutableStateOf<ProjectItem?>(null) }

  val filteredProjects = remember(projects, selectedStatusFilter) {
    if (selectedStatusFilter == null) projects
    else projects.filter { it.status == selectedStatusFilter }
  }

  val totalWords = remember(projects) { projects.sumOf { it.currentWordCount } }
  val ongoingCount = remember(projects) { projects.count { it.status == ProjectStatus.ONGOING } }

  Box(modifier = Modifier.fillMaxSize()) {
    LazyColumn(
      modifier = Modifier.fillMaxSize(),
      contentPadding = PaddingValues(16.dp, 12.dp, 16.dp, 96.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      item {
        Column {
          Text(
            text = "Organisasi Proyek AU",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = "Kelola naskah, progres kata, dan status cerita fanfiction",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }

      // Quick Stats Overview Cards
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          StatCard(
            title = "Total AU",
            value = "${projects.size}",
            subtitle = "Koleksi Naskah",
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.weight(1f)
          )
          StatCard(
            title = "Sedang Ditulis",
            value = "$ongoingCount",
            subtitle = "Status Ongoing",
            color = Color(0xFF22C55E),
            modifier = Modifier.weight(1f)
          )
          StatCard(
            title = "Kata Ditulis",
            value = "$totalWords",
            subtitle = "Total Kata",
            color = Color(0xFFEC4899),
            modifier = Modifier.weight(1f)
          )
        }
      }

      // Filter chips
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          FilterChip(
            selected = selectedStatusFilter == null,
            onClick = { selectedStatusFilter = null },
            label = { Text("Semua (${projects.size})", style = MaterialTheme.typography.labelSmall) }
          )
          ProjectStatus.values().forEach { st ->
            FilterChip(
              selected = selectedStatusFilter == st,
              onClick = { selectedStatusFilter = st },
              label = { Text(st.label, style = MaterialTheme.typography.labelSmall) }
            )
          }
        }
      }

      // Project Cards
      items(filteredProjects, key = { it.id }) { proj ->
        val isActive = proj.id == activeProjectId

        ProjectCard(
          project = proj,
          isActive = isActive,
          onSelect = { onSelectProject(proj.id) },
          onEdit = {
            editingProject = proj
            isFormOpen = true
          },
          onDelete = { projectToDelete = proj }
        )
      }
    }

    // FAB
    ExtendedFloatingActionButton(
      onClick = {
        editingProject = null
        isFormOpen = true
      },
      icon = { Icon(Icons.Default.Add, contentDescription = null) },
      text = { Text("Proyek Baru") },
      containerColor = MaterialTheme.colorScheme.primary,
      contentColor = MaterialTheme.colorScheme.onPrimary,
      modifier = Modifier
        .align(Alignment.BottomEnd)
        .padding(20.dp)
    )
  }

  // Project Form Dialog
  if (isFormOpen) {
    ProjectFormDialog(
      initialProject = editingProject,
      onDismiss = { isFormOpen = false },
      onSave = { saved ->
        onSaveProject(saved)
        isFormOpen = false
      }
    )
  }

  // Delete Confirmation Dialog
  projectToDelete?.let { proj ->
    AlertDialog(
      onDismissRequest = { projectToDelete = null },
      title = { Text("Hapus Proyek AU?") },
      text = {
        Text("Apakah kamu yakin ingin menghapus '${proj.title}'? Semua karakter, relasi, dan kartu plot di proyek ini akan ikut terhapus.")
      },
      confirmButton = {
        TextButton(
          onClick = {
            onDeleteProject(proj.id)
            projectToDelete = null
          }
        ) {
          Text("Hapus", color = MaterialTheme.colorScheme.error)
        }
      },
      dismissButton = {
        TextButton(onClick = { projectToDelete = null }) {
          Text("Batal")
        }
      }
    )
  }
}

@Composable
fun StatCard(
  title: String,
  value: String,
  subtitle: String,
  color: Color,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier,
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
  ) {
    Column(
      modifier = Modifier.padding(12.dp),
      verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
      Text(title, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
      Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = color)
      Text(subtitle, style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
  }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProjectCard(
  project: ProjectItem,
  isActive: Boolean,
  onSelect: () -> Unit,
  onEdit: () -> Unit,
  onDelete: () -> Unit
) {
  val statusColor = when (project.status) {
    ProjectStatus.DRAFT -> Color(0xFF94A3B8)
    ProjectStatus.ONGOING -> Color(0xFF22C55E)
    ProjectStatus.HIATUS -> Color(0xFFF59E0B)
    ProjectStatus.TAMAT, ProjectStatus.SELESAI -> Color(0xFF6366F1)
  }

  val progress = if (project.dailyWordTarget > 0) {
    (project.currentWordCount.toFloat() / project.dailyWordTarget.toFloat()).coerceIn(0f, 1f)
  } else 0f

  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(18.dp),
    colors = CardDefaults.cardColors(
      containerColor = if (isActive) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)
      else MaterialTheme.colorScheme.surface
    ),
    border = if (isActive) CardDefaults.outlinedCardBorder().copy(
      brush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.primary),
      width = 2.dp
    ) else null
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
          Box(
            modifier = Modifier
              .size(36.dp)
              .clip(CircleShape)
              .background(if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Book,
              contentDescription = null,
              tint = if (isActive) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(18.dp)
            )
          }

          Spacer(modifier = Modifier.width(10.dp))

          Column {
            Text(
              text = project.title,
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold
            )
            if (project.fandom.isNotBlank()) {
              Text(
                text = "Fandom: ${project.fandom}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.secondary,
                fontWeight = FontWeight.SemiBold
              )
            }
          }
        }

        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(statusColor.copy(alpha = 0.2f))
            .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
          Text(
            text = project.status.label,
            style = MaterialTheme.typography.labelSmall,
            color = statusColor,
            fontWeight = FontWeight.Bold
          )
        }
      }

      if (project.description.isNotBlank()) {
        Text(
          text = project.description,
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          maxLines = 2
        )
      }

      // Word Count Progress
      Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text(
            text = "Progres: ${project.currentWordCount} / ${project.dailyWordTarget} kata",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Text(
            text = "${(progress * 100).toInt()}%",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
          )
        }
        LinearProgressIndicator(
          progress = { progress },
          modifier = Modifier
            .fillMaxWidth()
            .height(6.dp)
            .clip(RoundedCornerShape(3.dp)),
          color = MaterialTheme.colorScheme.primary,
          trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
      }

      // Tags
      if (project.tags.isNotEmpty()) {
        FlowRow(
          horizontalArrangement = Arrangement.spacedBy(6.dp),
          verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          project.tags.forEach { tag ->
            SuggestionChip(
              onClick = {},
              label = { Text("#$tag", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp)) },
              modifier = Modifier.height(24.dp)
            )
          }
        }
      }

      // Actions row
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        if (isActive) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Check, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Sedang Aktif", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
          }
        } else {
          Button(
            onClick = onSelect,
            shape = RoundedCornerShape(10.dp)
          ) {
            Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Buka Proyek Ini")
          }
        }

        Row {
          IconButton(onClick = onEdit) {
            Icon(Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.primary)
          }
          IconButton(onClick = onDelete) {
            Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f))
          }
        }
      }
    }
  }
}
