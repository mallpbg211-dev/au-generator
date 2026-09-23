package com.example.ui.screens.characters

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ViewList
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.ui.platform.testTag
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.data.model.CharacterItem

@Composable
fun CharacterBuilderScreen(
  characters: List<CharacterItem>,
  currentProjectId: String,
  onSaveCharacter: (CharacterItem) -> Unit,
  onDeleteCharacter: (String) -> Unit
) {
  var isGridView by remember { mutableStateOf(false) }
  var searchQuery by remember { mutableStateOf("") }
  var selectedGenderFilter by remember { mutableStateOf("Semua") }

  var editingCharacter by remember { mutableStateOf<CharacterItem?>(null) }
  var isFormOpen by remember { mutableStateOf(false) }
  var characterToDelete by remember { mutableStateOf<CharacterItem?>(null) }

  val filteredCharacters = remember(characters, searchQuery, selectedGenderFilter) {
    characters.filter { char ->
      val matchesQuery = searchQuery.isBlank() ||
        char.stageName.contains(searchQuery, ignoreCase = true) ||
        char.realName.contains(searchQuery, ignoreCase = true) ||
        char.groupName.contains(searchQuery, ignoreCase = true) ||
        char.roleInAU.contains(searchQuery, ignoreCase = true)

      val matchesGender = when (selectedGenderFilter) {
        "Cowok" -> char.gender == "Cowok"
        "Cewek" -> char.gender == "Cewek"
        "Lainnya" -> char.gender != "Cowok" && char.gender != "Cewek"
        else -> true
      }
      matchesQuery && matchesGender
    }
  }

  Box(modifier = Modifier.fillMaxSize()) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 16.dp)
    ) {
      Spacer(modifier = Modifier.height(12.dp))

      // Header info & Controls
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "Character Builder",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = "${characters.size} Idol terdaftar dalam cerita ini",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          IconButton(
            onClick = { isGridView = !isGridView },
            modifier = Modifier.testTag("toggle_view_button")
          ) {
            Icon(
              imageVector = if (isGridView) Icons.Default.ViewList else Icons.Default.GridView,
              contentDescription = "Ganti Tampilan",
              tint = MaterialTheme.colorScheme.primary
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Search field
      OutlinedTextField(
        value = searchQuery,
        onValueChange = { searchQuery = it },
        placeholder = { Text("Cari nama panggung, grup, atau peran AU...") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
        singleLine = true,
        modifier = Modifier
          .fillMaxWidth()
          .testTag("search_character_input"),
        shape = RoundedCornerShape(14.dp)
      )

      Spacer(modifier = Modifier.height(8.dp))

      // Gender filter chips
      Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        listOf("Semua", "Cowok", "Cewek", "Lainnya").forEach { filter ->
          FilterChip(
            selected = selectedGenderFilter == filter,
            onClick = { selectedGenderFilter = filter },
            label = { Text(filter, style = MaterialTheme.typography.labelSmall) }
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Content List or Grid
      if (filteredCharacters.isEmpty()) {
        Box(
          modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 80.dp),
          contentAlignment = Alignment.Center
        ) {
          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Text("✨", style = MaterialTheme.typography.displaySmall)
            Text(
              text = if (characters.isEmpty()) "Belum ada karakter idol di AU ini" else "Tidak ada karakter yang cocok",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.SemiBold
            )
            Text(
              text = "Tambahkan idol bias atau karakter pendukung cerita!",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
      } else if (isGridView) {
        LazyVerticalGrid(
          columns = GridCells.Fixed(2),
          modifier = Modifier.fillMaxSize(),
          contentPadding = PaddingValues(bottom = 96.dp),
          horizontalArrangement = Arrangement.spacedBy(10.dp),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          items(filteredCharacters, key = { it.id }) { char ->
            CharacterGridCard(
              character = char,
              onEdit = {
                editingCharacter = char
                isFormOpen = true
              },
              onDelete = { characterToDelete = char }
            )
          }
        }
      } else {
        LazyColumn(
          modifier = Modifier.fillMaxSize(),
          contentPadding = PaddingValues(bottom = 96.dp),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          items(filteredCharacters, key = { it.id }) { char ->
            CharacterListCard(
              character = char,
              onEdit = {
                editingCharacter = char
                isFormOpen = true
              },
              onDelete = { characterToDelete = char }
            )
          }
        }
      }
    }

    // Floating Action Button
    ExtendedFloatingActionButton(
      onClick = {
        editingCharacter = null
        isFormOpen = true
      },
      icon = { Icon(Icons.Default.Add, contentDescription = null) },
      text = { Text("Tambah Idol") },
      containerColor = MaterialTheme.colorScheme.primary,
      contentColor = MaterialTheme.colorScheme.onPrimary,
      modifier = Modifier
        .align(Alignment.BottomEnd)
        .padding(20.dp)
        .testTag("add_character_fab")
    )
  }

  // Character Form Dialog
  if (isFormOpen) {
    CharacterFormDialog(
      initialCharacter = editingCharacter,
      projectId = currentProjectId,
      onDismiss = { isFormOpen = false },
      onSave = { saved ->
        onSaveCharacter(saved)
        isFormOpen = false
      }
    )
  }

  // Delete Confirmation Dialog
  characterToDelete?.let { char ->
    AlertDialog(
      onDismissRequest = { characterToDelete = null },
      title = { Text("Hapus Karakter?") },
      text = {
        Text("Apakah kamu yakin ingin menghapus profil ${char.stageName} (${char.groupName})? Relasi karakter ini juga akan terhapus.")
      },
      confirmButton = {
        TextButton(
          onClick = {
            onDeleteCharacter(char.id)
            characterToDelete = null
          }
        ) {
          Text("Hapus", color = MaterialTheme.colorScheme.error)
        }
      },
      dismissButton = {
        TextButton(onClick = { characterToDelete = null }) {
          Text("Batal")
        }
      }
    )
  }
}

@Composable
fun CharacterListCard(
  character: CharacterItem,
  onEdit: () -> Unit,
  onDelete: () -> Unit
) {
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
      // Avatar Badge
      CharacterAvatarIcon(avatarKey = character.avatarKey, size = 48)

      Spacer(modifier = Modifier.width(14.dp))

      Column(modifier = Modifier.weight(1f)) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Text(
            text = character.stageName,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
          )
          SuggestionChip(
            onClick = {},
            label = { Text(character.groupName, style = MaterialTheme.typography.labelSmall) },
            colors = SuggestionChipDefaults.suggestionChipColors(
              containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
              labelColor = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier.height(26.dp)
          )
        }

        if (character.realName.isNotBlank()) {
          Text(
            text = character.realName,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }

        Row(
          horizontalArrangement = Arrangement.spacedBy(6.dp),
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.padding(top = 4.dp)
        ) {
          Text(
            text = "• ${character.position}",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.secondary
          )
          Text(
            text = "• ${character.gender}",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }

        if (character.roleInAU.isNotBlank()) {
          Text(
            text = "Peran AU: ${character.roleInAU}",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(top = 4.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
        }

        if (character.traits.isNotBlank()) {
          Text(
            text = character.traits,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 2.dp)
          )
        }
      }

      Column {
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

@Composable
fun CharacterGridCard(
  character: CharacterItem,
  onEdit: () -> Unit,
  onDelete: () -> Unit
) {
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
        .padding(12.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        CharacterAvatarIcon(avatarKey = character.avatarKey, size = 40)
        Row {
          IconButton(onClick = onEdit, modifier = Modifier.size(28.dp)) {
            Icon(Icons.Default.Edit, contentDescription = "Edit", modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
          }
          IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
            Icon(Icons.Default.Delete, contentDescription = "Hapus", modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f))
          }
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = character.stageName,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )

      Text(
        text = character.groupName,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.SemiBold
      )

      Text(
        text = character.position,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.secondary,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )

      if (character.roleInAU.isNotBlank()) {
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = character.roleInAU,
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.onSurface,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
          fontWeight = FontWeight.Medium
        )
      }
    }
  }
}

@Composable
fun CharacterAvatarIcon(avatarKey: String, size: Int = 40) {
  val icon: ImageVector = when (avatarKey) {
    "star" -> Icons.Default.Star
    "heart" -> Icons.Default.Favorite
    "sparkle" -> Icons.Default.AutoAwesome
    "palette" -> Icons.Default.Palette
    "music" -> Icons.Default.MusicNote
    "face" -> Icons.Default.Face
    "person" -> Icons.Default.Person
    else -> Icons.Default.Mic
  }

  Box(
    modifier = Modifier
      .size(size.dp)
      .clip(CircleShape)
      .background(MaterialTheme.colorScheme.primaryContainer),
    contentAlignment = Alignment.Center
  ) {
    Icon(
      imageVector = icon,
      contentDescription = null,
      tint = MaterialTheme.colorScheme.primary,
      modifier = Modifier.size((size * 0.55).dp)
    )
  }
}
