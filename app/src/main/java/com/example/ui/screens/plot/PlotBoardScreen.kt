package com.example.ui.screens.plot

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.ViewKanban
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CardStatus
import com.example.data.model.CharacterItem
import com.example.data.model.PlotCardItem
import com.example.data.model.PlotStage
import kotlinx.coroutines.launch

@Composable
fun PlotBoardScreen(
  plotCards: List<PlotCardItem>,
  characters: List<CharacterItem>,
  currentProjectId: String,
  onSaveCard: (PlotCardItem) -> Unit,
  onDeleteCard: (String) -> Unit
) {
  var isTimelineView by remember { mutableStateOf(false) }
  var isAddDialogOpen by remember { mutableStateOf(false) }
  var editingCard by remember { mutableStateOf<PlotCardItem?>(null) }
  var cardToDelete by remember { mutableStateOf<PlotCardItem?>(null) }
  var targetStageForNewCard by remember { mutableStateOf(PlotStage.OUTLINE) }

  val stages = PlotStage.values()
  val pagerState = rememberPagerState(initialPage = 0, pageCount = { stages.size })
  val coroutineScope = rememberCoroutineScope()

  Box(modifier = Modifier.fillMaxSize()) {
    Column(modifier = Modifier.fillMaxSize()) {
      // Header & View Toggle
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "Plot Board & Adegan",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = "${plotCards.size} Adegan terdaftar di cerita ini",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }

        IconButton(onClick = { isTimelineView = !isTimelineView }) {
          Icon(
            imageVector = if (isTimelineView) Icons.Default.ViewKanban else Icons.Default.Timeline,
            contentDescription = "Ganti Tampilan",
            tint = MaterialTheme.colorScheme.primary
          )
        }
      }

      if (isTimelineView) {
        // Chronological Timeline View
        TimelineSequenceView(
          plotCards = plotCards.sortedBy { it.sequenceNumber },
          characters = characters,
          onEdit = { card ->
            editingCard = card
            isAddDialogOpen = true
          },
          onDelete = { card -> cardToDelete = card }
        )
      } else {
        // Kanban Columns View with Horizontal Pager & Tabs
        ScrollableTabRow(
          selectedTabIndex = pagerState.currentPage,
          edgePadding = 16.dp,
          containerColor = MaterialTheme.colorScheme.surface
        ) {
          stages.forEachIndexed { index, stage ->
            val count = plotCards.count { it.stage == stage }
            Tab(
              selected = pagerState.currentPage == index,
              onClick = { coroutineScope.launch { pagerState.animateScrollToPage(index) } },
              text = {
                Text(
                  text = "${stage.title} ($count)",
                  fontWeight = if (pagerState.currentPage == index) FontWeight.Bold else FontWeight.Normal
                )
              }
            )
          }
        }

        HorizontalPager(
          state = pagerState,
          modifier = Modifier.fillMaxSize()
        ) { pageIndex ->
          val currentStage = stages[pageIndex]
          val cardsInStage = plotCards
            .filter { it.stage == currentStage }
            .sortedBy { it.sequenceNumber }

          KanbanColumnView(
            stage = currentStage,
            cards = cardsInStage,
            characters = characters,
            onAddCard = {
              targetStageForNewCard = currentStage
              editingCard = null
              isAddDialogOpen = true
            },
            onEditCard = { card ->
              editingCard = card
              isAddDialogOpen = true
            },
            onDeleteCard = { card -> cardToDelete = card },
            onMoveStage = { card, forward ->
              val nextOrdinal = if (forward) card.stage.ordinal + 1 else card.stage.ordinal - 1
              if (nextOrdinal in stages.indices) {
                onSaveCard(card.copy(stage = stages[nextOrdinal]))
              }
            },
            onToggleStatus = { card ->
              val nextStatus = when (card.status) {
                CardStatus.DRAFT -> CardStatus.REVISI
                CardStatus.REVISI -> CardStatus.SELESAI
                CardStatus.SELESAI -> CardStatus.DRAFT
              }
              onSaveCard(card.copy(status = nextStatus))
            }
          )
        }
      }
    }

    // FAB to add new scene
    ExtendedFloatingActionButton(
      onClick = {
        editingCard = null
        targetStageForNewCard = stages[pagerState.currentPage]
        isAddDialogOpen = true
      },
      icon = { Icon(Icons.Default.Add, contentDescription = null) },
      text = { Text("Tambah Adegan") },
      containerColor = MaterialTheme.colorScheme.primary,
      contentColor = MaterialTheme.colorScheme.onPrimary,
      modifier = Modifier
        .align(Alignment.BottomEnd)
        .padding(20.dp)
    )
  }

  // Dialog Add / Edit Scene
  if (isAddDialogOpen) {
    AddSceneDialog(
      initialCard = editingCard,
      characters = characters,
      projectId = currentProjectId,
      defaultStage = targetStageForNewCard,
      onDismiss = { isAddDialogOpen = false },
      onSave = { saved ->
        onSaveCard(saved)
        isAddDialogOpen = false
      }
    )
  }

  // Delete Confirmation Dialog
  cardToDelete?.let { card ->
    AlertDialog(
      onDismissRequest = { cardToDelete = null },
      title = { Text("Hapus Adegan?") },
      text = {
        Text("Apakah kamu yakin ingin menghapus '${card.title}' dari Plot Board?")
      },
      confirmButton = {
        TextButton(
          onClick = {
            onDeleteCard(card.id)
            cardToDelete = null
          }
        ) {
          Text("Hapus", color = MaterialTheme.colorScheme.error)
        }
      },
      dismissButton = {
        TextButton(onClick = { cardToDelete = null }) {
          Text("Batal")
        }
      }
    )
  }
}

@Composable
fun KanbanColumnView(
  stage: PlotStage,
  cards: List<PlotCardItem>,
  characters: List<CharacterItem>,
  onAddCard: () -> Unit,
  onEditCard: (PlotCardItem) -> Unit,
  onDeleteCard: (PlotCardItem) -> Unit,
  onMoveStage: (PlotCardItem, Boolean) -> Unit,
  onToggleStatus: (PlotCardItem) -> Unit
) {
  LazyColumn(
    modifier = Modifier.fillMaxSize(),
    contentPadding = PaddingValues(16.dp, 12.dp, 16.dp, 96.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    item {
      // Column subtitle
      Text(
        text = stage.description,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(bottom = 4.dp)
      )
    }

    if (cards.isEmpty()) {
      item {
        Card(
          onClick = onAddCard,
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
          ),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Text("📝", style = MaterialTheme.typography.titleLarge)
            Text(
              text = "Belum ada adegan di babak ini",
              style = MaterialTheme.typography.bodyMedium,
              fontWeight = FontWeight.Medium
            )
            Text(
              text = "+ Tap untuk membuat kartu adegan pertama",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.primary
            )
          }
        }
      }
    } else {
      items(cards, key = { it.id }) { card ->
        SceneCard(
          card = card,
          characters = characters,
          onEdit = { onEditCard(card) },
          onDelete = { onDeleteCard(card) },
          onMoveLeft = if (card.stage.ordinal > 0) { { onMoveStage(card, false) } } else null,
          onMoveRight = if (card.stage.ordinal < PlotStage.values().size - 1) { { onMoveStage(card, true) } } else null,
          onToggleStatus = { onToggleStatus(card) }
        )
      }
    }
  }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SceneCard(
  card: PlotCardItem,
  characters: List<CharacterItem>,
  onEdit: () -> Unit,
  onDelete: () -> Unit,
  onMoveLeft: (() -> Unit)?,
  onMoveRight: (() -> Unit)?,
  onToggleStatus: () -> Unit
) {
  val statusColor = when (card.status) {
    CardStatus.DRAFT -> Color(0xFFFFA000)
    CardStatus.REVISI -> Color(0xFF29B6F6)
    CardStatus.SELESAI -> Color(0xFF66BB6A)
  }

  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surface
    )
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp)
    ) {
      // Top row: sequence # and status
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(24.dp)
              .clip(CircleShape)
              .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = "#${card.sequenceNumber}",
              style = MaterialTheme.typography.labelSmall,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.primary
            )
          }
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "Est. ~${card.estimatedWords} kata",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }

        // Status badge (clickable to cycle status)
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(statusColor.copy(alpha = 0.2f))
            .clickable { onToggleStatus() }
            .padding(horizontal = 8.dp, vertical = 3.dp)
        ) {
          Text(
            text = card.status.label,
            style = MaterialTheme.typography.labelSmall,
            color = statusColor,
            fontWeight = FontWeight.Bold
          )
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = card.title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold
      )

      if (card.summary.isNotBlank()) {
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = card.summary,
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f)
        )
      }

      // Characters involved tags
      val involved = characters.filter { card.characterIds.contains(it.id) }
      if (involved.isNotEmpty()) {
        Spacer(modifier = Modifier.height(8.dp))
        FlowRow(
          horizontalArrangement = Arrangement.spacedBy(4.dp),
          verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          involved.forEach { char ->
            SuggestionChip(
              onClick = {},
              label = { Text("👤 ${char.stageName}", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp)) },
              modifier = Modifier.height(24.dp)
            )
          }
        }
      }

      // Action row: move columns, edit, delete
      Spacer(modifier = Modifier.height(8.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row {
          onMoveLeft?.let {
            IconButton(onClick = it, modifier = Modifier.size(32.dp)) {
              Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Pindah ke babak sebelumnya", modifier = Modifier.size(16.dp))
            }
          }
          onMoveRight?.let {
            IconButton(onClick = it, modifier = Modifier.size(32.dp)) {
              Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Pindah ke babak selanjutnya", modifier = Modifier.size(16.dp))
            }
          }
        }

        Row {
          IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
            Icon(Icons.Default.Edit, contentDescription = "Edit", modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
          }
          IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
            Icon(Icons.Default.Delete, contentDescription = "Hapus", modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f))
          }
        }
      }
    }
  }
}

@Composable
fun TimelineSequenceView(
  plotCards: List<PlotCardItem>,
  characters: List<CharacterItem>,
  onEdit: (PlotCardItem) -> Unit,
  onDelete: (PlotCardItem) -> Unit
) {
  if (plotCards.isEmpty()) {
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(bottom = 80.dp),
      contentAlignment = Alignment.Center
    ) {
      Text("Belum ada adegan untuk timeline", style = MaterialTheme.typography.bodyMedium)
    }
  } else {
    LazyColumn(
      modifier = Modifier.fillMaxSize(),
      contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 96.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      items(plotCards, key = { it.id }) { card ->
        Row(modifier = Modifier.fillMaxWidth()) {
          // Left timeline indicator
          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(36.dp)
          ) {
            Box(
              modifier = Modifier
                .size(30.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = "${card.sequenceNumber}",
                color = MaterialTheme.colorScheme.onPrimary,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.labelSmall
              )
            }
            Box(
              modifier = Modifier
                .width(2.dp)
                .height(80.dp)
                .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
            )
          }

          Spacer(modifier = Modifier.width(12.dp))

          // Card
          Card(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
          ) {
            Column(modifier = Modifier.padding(14.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = card.stage.title,
                  style = MaterialTheme.typography.labelSmall,
                  color = MaterialTheme.colorScheme.primary,
                  fontWeight = FontWeight.SemiBold
                )
                Text(
                  text = card.status.label,
                  style = MaterialTheme.typography.labelSmall,
                  color = MaterialTheme.colorScheme.secondary,
                  fontWeight = FontWeight.Bold
                )
              }

              Spacer(modifier = Modifier.height(4.dp))
              Text(text = card.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

              if (card.summary.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = card.summary, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
              }

              Spacer(modifier = Modifier.height(8.dp))
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
              ) {
                IconButton(onClick = { onEdit(card) }, modifier = Modifier.size(28.dp)) {
                  Icon(Icons.Default.Edit, contentDescription = "Edit", modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
                }
                IconButton(onClick = { onDelete(card) }, modifier = Modifier.size(28.dp)) {
                  Icon(Icons.Default.Delete, contentDescription = "Hapus", modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.error)
                }
              }
            }
          }
        }
      }
    }
  }
}
