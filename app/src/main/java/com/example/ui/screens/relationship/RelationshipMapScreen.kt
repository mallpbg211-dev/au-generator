package com.example.ui.screens.relationship

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ViewList
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CharacterItem
import com.example.data.model.RelationshipItem
import com.example.ui.theme.parseColorSafe
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

@Composable
fun RelationshipMapScreen(
  characters: List<CharacterItem>,
  relationships: List<RelationshipItem>,
  currentProjectId: String,
  onSaveRelationship: (RelationshipItem) -> Unit,
  onDeleteRelationship: (String) -> Unit
) {
  var selectedTab by remember { mutableStateOf(0) } // 0 = Canvas Map, 1 = List View
  var isAddDialogOpen by remember { mutableStateOf(false) }
  var editingRelationship by remember { mutableStateOf<RelationshipItem?>(null) }
  var relationshipToDelete by remember { mutableStateOf<RelationshipItem?>(null) }

  // Quick connect tapping state: first tapped character and second tapped character
  var selectedFirstCharId by remember { mutableStateOf<String?>(null) }

  Column(modifier = Modifier.fillMaxSize()) {
    TabRow(
      selectedTabIndex = selectedTab,
      containerColor = MaterialTheme.colorScheme.surface
    ) {
      Tab(
        selected = selectedTab == 0,
        onClick = { selectedTab = 0 },
        text = {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Hub, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Peta Visual (Node Map)")
          }
        }
      )
      Tab(
        selected = selectedTab == 1,
        onClick = { selectedTab = 1 },
        text = {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.ViewList, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Daftar Hubungan (${relationships.size})")
          }
        }
      )
    }

    Box(modifier = Modifier.fillMaxSize()) {
      if (characters.size < 2) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(24.dp)
          ) {
            Text("💞", style = MaterialTheme.typography.displaySmall)
            Text(
              text = "Minimal 2 karakter diperlukan",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = "Buka menu 'Character Builder' untuk menambahkan minimal dua idol agar bisa membuat peta relasi (OTP, rival, found family).",
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
          }
        }
      } else if (selectedTab == 0) {
        // Interactive Canvas Visual Map
        VisualNodeMapCanvas(
          characters = characters,
          relationships = relationships,
          selectedFirstCharId = selectedFirstCharId,
          onSelectChar = { charId ->
            if (selectedFirstCharId == null) {
              selectedFirstCharId = charId
            } else if (selectedFirstCharId == charId) {
              selectedFirstCharId = null
            } else {
              // Open add connection dialog between selectedFirstCharId and charId
              editingRelationship = null
              isAddDialogOpen = true
            }
          },
          onEditRelationship = { rel ->
            editingRelationship = rel
            isAddDialogOpen = true
          }
        )
      } else {
        // List View of Relationships
        RelationshipListView(
          characters = characters,
          relationships = relationships,
          onEdit = { rel ->
            editingRelationship = rel
            isAddDialogOpen = true
          },
          onDelete = { rel ->
            relationshipToDelete = rel
          }
        )
      }

      // Floating Action Button
      if (characters.size >= 2) {
        ExtendedFloatingActionButton(
          onClick = {
            editingRelationship = null
            isAddDialogOpen = true
          },
          icon = { Icon(Icons.Default.Add, contentDescription = null) },
          text = { Text("Hubungkan Idol") },
          containerColor = MaterialTheme.colorScheme.primary,
          contentColor = MaterialTheme.colorScheme.onPrimary,
          modifier = Modifier
            .align(Alignment.BottomEnd)
            .padding(20.dp)
        )
      }
    }
  }

  // Dialog Add / Edit Relationship
  if (isAddDialogOpen) {
    AddRelationshipDialog(
      initialRelationship = editingRelationship,
      characters = characters,
      projectId = currentProjectId,
      defaultFromCharId = selectedFirstCharId,
      onDismiss = {
        isAddDialogOpen = false
        selectedFirstCharId = null
      },
      onSave = { saved ->
        onSaveRelationship(saved)
        isAddDialogOpen = false
        selectedFirstCharId = null
      }
    )
  }

  // Delete Confirmation Dialog
  relationshipToDelete?.let { rel ->
    val char1 = characters.find { it.id == rel.fromCharacterId }?.stageName ?: "Karakter 1"
    val char2 = characters.find { it.id == rel.toCharacterId }?.stageName ?: "Karakter 2"

    AlertDialog(
      onDismissRequest = { relationshipToDelete = null },
      title = { Text("Hapus Hubungan?") },
      text = {
        Text("Apakah kamu yakin ingin menghapus hubungan '${rel.tag}' antara $char1 dan $char2?")
      },
      confirmButton = {
        TextButton(
          onClick = {
            onDeleteRelationship(rel.id)
            relationshipToDelete = null
          }
        ) {
          Text("Hapus", color = MaterialTheme.colorScheme.error)
        }
      },
      dismissButton = {
        TextButton(onClick = { relationshipToDelete = null }) {
          Text("Batal")
        }
      }
    )
  }
}

@Composable
fun VisualNodeMapCanvas(
  characters: List<CharacterItem>,
  relationships: List<RelationshipItem>,
  selectedFirstCharId: String?,
  onSelectChar: (String) -> Unit,
  onEditRelationship: (RelationshipItem) -> Unit
) {
  // Store node positions (in pixels)
  val nodeOffsets = remember { mutableStateMapOf<String, Offset>() }
  val density = LocalDensity.current

  BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
    val widthPx = with(density) { maxWidth.toPx() }
    val heightPx = with(density) { maxHeight.toPx() }

    val centerX = widthPx / 2f
    val centerY = heightPx / 2f
    val radius = minOf(widthPx, heightPx) * 0.35f

    // Initialize positions in a circle if not already positioned
    characters.forEachIndexed { index, char ->
      if (!nodeOffsets.containsKey(char.id)) {
        val angle = (2 * Math.PI * index / characters.size) - Math.PI / 2
        val x = centerX + (radius * cos(angle)).toFloat()
        val y = centerY + (radius * sin(angle)).toFloat()
        nodeOffsets[char.id] = Offset(x, y)
      }
    }

    // Top instruction hint
    Box(
      modifier = Modifier
        .align(Alignment.TopCenter)
        .padding(12.dp)
        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.85f), RoundedCornerShape(20.dp))
        .padding(horizontal = 14.dp, vertical = 6.dp)
    ) {
      Text(
        text = if (selectedFirstCharId == null) "💡 Geser node karakter untuk mengatur posisi, atau tap 2 karakter untuk menghubungkan"
        else "✨ Sekarang tap karakter kedua untuk menghubungkan!",
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.Medium
      )
    }

    // Draw connection lines
    Canvas(modifier = Modifier.fillMaxSize()) {
      relationships.forEach { rel ->
        val fromPos = nodeOffsets[rel.fromCharacterId]
        val toPos = nodeOffsets[rel.toCharacterId]

        if (fromPos != null && toPos != null) {
          val lineColor = parseColorSafe(rel.colorHex, Color(0xFFFF4081))
          drawLine(
            color = lineColor.copy(alpha = 0.75f),
            start = fromPos,
            end = toPos,
            strokeWidth = 5f,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 10f), 0f)
          )
        }
      }
    }

    // Draw relationship badges in between nodes
    relationships.forEach { rel ->
      val fromPos = nodeOffsets[rel.fromCharacterId]
      val toPos = nodeOffsets[rel.toCharacterId]

      if (fromPos != null && toPos != null) {
        val midX = (fromPos.x + toPos.x) / 2f
        val midY = (fromPos.y + toPos.y) / 2f
        val lineColor = parseColorSafe(rel.colorHex, Color(0xFFFF4081))

        Box(
          modifier = Modifier
            .offset { IntOffset(midX.roundToInt() - 50, midY.roundToInt() - 14) }
            .clip(RoundedCornerShape(12.dp))
            .background(lineColor)
            .clickable { onEditRelationship(rel) }
            .padding(horizontal = 8.dp, vertical = 3.dp)
        ) {
          Text(
            text = rel.tag,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
            color = Color.White,
            fontWeight = FontWeight.Bold
          )
        }
      }
    }

    // Draw character nodes
    characters.forEach { char ->
      val pos = nodeOffsets[char.id] ?: Offset(centerX, centerY)
      val isSelected = char.id == selectedFirstCharId

      Box(
        modifier = Modifier
          .offset { IntOffset((pos.x - 36).roundToInt(), (pos.y - 36).roundToInt()) }
          .pointerInput(char.id) {
            detectDragGestures { change, dragAmount ->
              change.consume()
              val current = nodeOffsets[char.id] ?: Offset(centerX, centerY)
              nodeOffsets[char.id] = Offset(current.x + dragAmount.x, current.y + dragAmount.y)
            }
          }
          .clickable { onSelectChar(char.id) }
      ) {
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          modifier = Modifier.width(72.dp)
        ) {
          Box(
            modifier = Modifier
              .size(52.dp)
              .clip(CircleShape)
              .background(
                if (isSelected) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.surface
              )
              .border(
                width = if (isSelected) 3.dp else 2.dp,
                color = if (isSelected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary,
                shape = CircleShape
              ),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = getCharacterIcon(char.avatarKey),
              contentDescription = null,
              tint = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(26.dp)
            )
          }

          Spacer(modifier = Modifier.height(2.dp))

          Text(
            text = char.stageName,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            color = MaterialTheme.colorScheme.onSurface
          )
          Text(
            text = char.groupName,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1
          )
        }
      }
    }
  }
}

@Composable
fun RelationshipListView(
  characters: List<CharacterItem>,
  relationships: List<RelationshipItem>,
  onEdit: (RelationshipItem) -> Unit,
  onDelete: (RelationshipItem) -> Unit
) {
  if (relationships.isEmpty()) {
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(bottom = 80.dp),
      contentAlignment = Alignment.Center
    ) {
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Text("🔗", style = MaterialTheme.typography.displaySmall)
        Text(
          text = "Belum ada hubungan antar karakter",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.SemiBold
        )
        Text(
          text = "Tap tombol 'Hubungkan Idol' untuk membuat ship OTP, rivalitas, atau sahabat.",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
    }
  } else {
    LazyColumn(
      modifier = Modifier.fillMaxSize(),
      contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 96.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      items(relationships, key = { it.id }) { rel ->
        val char1 = characters.find { it.id == rel.fromCharacterId }
        val char2 = characters.find { it.id == rel.toCharacterId }

        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
          )
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            // Tag Color pill
            Box(
              modifier = Modifier
                .width(4.dp)
                .height(48.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(parseColorSafe(rel.colorHex, MaterialTheme.colorScheme.primary))
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                Text(
                  text = "${char1?.stageName ?: "Karakter"} × ${char2?.stageName ?: "Karakter"}",
                  style = MaterialTheme.typography.titleMedium,
                  fontWeight = FontWeight.Bold
                )
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(parseColorSafe(rel.colorHex, Color.Magenta).copy(alpha = 0.2f))
                    .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                  Text(
                    text = rel.tag,
                    style = MaterialTheme.typography.labelSmall,
                    color = parseColorSafe(rel.colorHex, Color.Magenta),
                    fontWeight = FontWeight.Bold
                  )
                }
              }

              Text(
                text = "${char1?.groupName ?: ""} & ${char2?.groupName ?: ""}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )

              if (rel.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = rel.notes,
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f)
                )
              }
            }

            Row {
              IconButton(onClick = { onEdit(rel) }) {
                Icon(Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.primary)
              }
              IconButton(onClick = { onDelete(rel) }) {
                Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f))
              }
            }
          }
        }
      }
    }
  }
}

fun getCharacterIcon(avatarKey: String): ImageVector {
  return when (avatarKey) {
    "star" -> Icons.Default.Star
    "heart" -> Icons.Default.Favorite
    "sparkle" -> Icons.Default.AutoAwesome
    "palette" -> Icons.Default.Palette
    "music" -> Icons.Default.MusicNote
    "face" -> Icons.Default.Face
    "person" -> Icons.Default.Person
    else -> Icons.Default.Mic
  }
}
