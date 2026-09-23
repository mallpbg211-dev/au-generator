package com.example.ui.screens.style_ai

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.PostAdd
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ai.GeminiWritingService
import com.example.data.model.CardStatus
import com.example.data.model.CharacterItem
import com.example.data.model.GenreTonePreset
import com.example.data.model.PlotCardItem
import com.example.data.model.PlotStage
import com.example.data.model.PovPreset
import com.example.data.model.ProjectItem
import com.example.data.model.TonePreset
import com.example.ui.theme.parseColorSafe
import kotlinx.coroutines.launch
import java.util.UUID

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun StyleAndAiScreen(
  project: ProjectItem,
  characters: List<CharacterItem>,
  geminiService: GeminiWritingService,
  onUpdateProject: (ProjectItem) -> Unit,
  onAddCardToPlotBoard: (PlotCardItem) -> Unit,
  onShowMessage: (String) -> Unit
) {
  var selectedTab by remember { mutableStateOf(0) } // 0 = Gaya Bahasa, 1 = Bantuan Nulis (AI)

  // Style State
  var currentTone by remember(project.tone) { mutableStateOf(project.tone) }
  var currentGenreTone by remember(project.genreTone) { mutableStateOf(project.genreTone) }
  var currentPov by remember(project.pov) { mutableStateOf(project.pov) }
  var customStyleDescription by remember(project.customStyleDescription) { mutableStateOf(project.customStyleDescription) }

  val fandomVocabList = remember(project.fandomVocabulary) {
    mutableStateListOf<String>().apply { addAll(project.fandomVocabulary) }
  }
  var newVocabInput by remember { mutableStateOf("") }

  // AI Assistant State
  var roughPremiseInput by remember { mutableStateOf("") }
  var customAiInstruction by remember { mutableStateOf("") }
  var aiResultText by remember { mutableStateOf("") }
  var isLoadingAi by remember { mutableStateOf(false) }

  val coroutineScope = rememberCoroutineScope()
  val context = LocalContext.current

  Column(modifier = Modifier.fillMaxSize()) {
    TabRow(
      selectedTabIndex = selectedTab,
      containerColor = MaterialTheme.colorScheme.surface
    ) {
      Tab(
        selected = selectedTab == 0,
        onClick = { selectedTab = 0 },
        text = { Text("1. Gaya Bahasa & Tone") }
      )
      Tab(
        selected = selectedTab == 1,
        onClick = { selectedTab = 1 },
        text = {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.width(6.dp))
            Text("2. Bantuan Nulis AI")
          }
        }
      )
    }

    if (selectedTab == 0) {
      // TAB 1: GAYA BAHASA & TONE SETTINGS
      LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp, 12.dp, 16.dp, 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
      ) {
        item {
          Column {
            Text(
              text = "Kustomisasi Nada & Gaya Penulisan",
              style = MaterialTheme.typography.titleLarge,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = "Atur warna suara narasi dan kosakata khas yang memandu cerita ini",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }

        // 1. Preset Tone
        item {
          Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
          ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
              Text(
                text = "Tone Bahasa:",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
              )
              TonePreset.values().forEach { tone ->
                val isSelected = currentTone == tone
                OutlinedCard(
                  onClick = {
                    currentTone = tone
                    onUpdateProject(project.copy(tone = tone))
                  },
                  shape = RoundedCornerShape(12.dp),
                  colors = CardDefaults.outlinedCardColors(
                    containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
                    else MaterialTheme.colorScheme.surface
                  ),
                  modifier = Modifier.fillMaxWidth()
                ) {
                  Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Box(
                      modifier = Modifier
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                      Text(text = tone.title, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyMedium)
                      Text(text = tone.desc, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                  }
                }
              }
            }
          }
        }

        // 2. Preset Genre Tone
        item {
          Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
          ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
              Text(
                text = "Genre Tone AU:",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
              )
              GenreTonePreset.values().forEach { genre ->
                val isSelected = currentGenreTone == genre
                OutlinedCard(
                  onClick = {
                    currentGenreTone = genre
                    onUpdateProject(project.copy(genreTone = genre))
                  },
                  shape = RoundedCornerShape(12.dp),
                  colors = CardDefaults.outlinedCardColors(
                    containerColor = if (isSelected) parseColorSafe(genre.badgeColorHex, MaterialTheme.colorScheme.primary).copy(alpha = 0.2f)
                    else MaterialTheme.colorScheme.surface
                  ),
                  modifier = Modifier.fillMaxWidth()
                ) {
                  Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Box(
                      modifier = Modifier
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(parseColorSafe(genre.badgeColorHex, MaterialTheme.colorScheme.primary))
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                      Text(text = genre.title, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyMedium)
                      Text(text = genre.desc, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                  }
                }
              }
            }
          }
        }

        // 3. POV Selector
        item {
          Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
          ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
              Text(
                text = "Sudut Pandang (POV):",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
              )
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
              ) {
                PovPreset.values().forEach { pov ->
                  val isSelected = currentPov == pov
                  FilterChip(
                    selected = isSelected,
                    onClick = {
                      currentPov = pov
                      onUpdateProject(project.copy(pov = pov))
                    },
                    label = { Text(pov.title, style = MaterialTheme.typography.labelSmall) },
                    modifier = Modifier.weight(1f)
                  )
                }
              }
            }
          }
        }

        // 4. Custom Style Description
        item {
          Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
          ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
              Text(
                text = "Deskripsi Gaya Penulisan Kustom:",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
              )
              OutlinedTextField(
                value = customStyleDescription,
                onValueChange = {
                  customStyleDescription = it
                  onUpdateProject(project.copy(customStyleDescription = it))
                },
                placeholder = { Text("Contoh: Banyak interaksi lewat chat room Twitter AU, selingan deskripsi puitis waktu hujan di kafe...") },
                minLines = 3,
                maxLines = 5,
                modifier = Modifier.fillMaxWidth()
              )
            }
          }
        }

        // 5. Custom Fandom Vocabulary
        item {
          Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
          ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
              Text(
                text = "Kosakata Khas Fandom / AU:",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = "Istilah-istilah ini akan otomatis diprioritaskan saat membuat adegan & bantuan AI.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )

              FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
              ) {
                fandomVocabList.forEach { vocab ->
                  SuggestionChip(
                    onClick = {
                      fandomVocabList.remove(vocab)
                      onUpdateProject(project.copy(fandomVocabulary = fandomVocabList.toList()))
                    },
                    label = { Text("$vocab ✕", style = MaterialTheme.typography.labelSmall) }
                  )
                }
              }

              Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                OutlinedTextField(
                  value = newVocabInput,
                  onValueChange = { newVocabInput = it },
                  placeholder = { Text("Tambah kosakata (misal: fancam, loker)") },
                  singleLine = true,
                  modifier = Modifier.weight(1f)
                )
                Button(
                  onClick = {
                    if (newVocabInput.isNotBlank()) {
                      fandomVocabList.add(newVocabInput.trim())
                      onUpdateProject(project.copy(fandomVocabulary = fandomVocabList.toList()))
                      newVocabInput = ""
                    }
                  }
                ) {
                  Icon(Icons.Default.Add, contentDescription = "Tambah")
                }
              }
            }
          }
        }
      }
    } else {
      // TAB 2: BANTUAN NULIS DENGAN GEMINI AI
      LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp, 12.dp, 16.dp, 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
      ) {
        // AI Model Info Banner
        item {
          Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
              containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
            )
          ) {
            Row(
              modifier = Modifier.padding(14.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
              Box(
                modifier = Modifier
                  .size(38.dp)
                  .clip(CircleShape)
                  .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
              ) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary)
              }
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = "AI Writer Assistant (Gemini 3.5)",
                  style = MaterialTheme.typography.titleMedium,
                  fontWeight = FontWeight.Bold
                )
                Text(
                  text = if (geminiService.hasApiKey) "Terhubung ke Gemini API dengan kunci aktif"
                  else "Menggunakan Generator Kreatif Lokal (Atur GEMINI_API_KEY di Secrets untuk API live)",
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }
          }
        }

        // Feature 1: Kembangkan Premis Kasar
        item {
          Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
          ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
              Text(
                text = "✨ Kembangkan Premis Kasar",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = "Punya ide sepintas? Biarkan AI memecahnya jadi 3 opsi arah cerita sesuai tone yang kamu pilih.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
              OutlinedTextField(
                value = roughPremiseInput,
                onValueChange = { roughPremiseInput = it },
                placeholder = { Text("Misal: Mereka ketemu lagi di tangga darurat stasiun TV pas jam 2 pagi...") },
                minLines = 2,
                maxLines = 4,
                modifier = Modifier.fillMaxWidth()
              )
              Button(
                onClick = {
                  if (roughPremiseInput.isNotBlank()) {
                    isLoadingAi = true
                    coroutineScope.launch {
                      aiResultText = geminiService.expandPremise(roughPremiseInput, project, characters)
                      isLoadingAi = false
                    }
                  }
                },
                enabled = !isLoadingAi && roughPremiseInput.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
              ) {
                if (isLoadingAi) {
                  CircularProgressIndicator(modifier = Modifier.size(18.dp), color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 2.dp)
                  Spacer(modifier = Modifier.width(8.dp))
                  Text("Menyusun Opsi Alur...")
                } else {
                  Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                  Spacer(modifier = Modifier.width(8.dp))
                  Text("Generate 3 Arah Cerita")
                }
              }
            }
          }
        }

        // Feature 2: Anti Writer's Block (Generate Ide / What If)
        item {
          Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
          ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
              Text(
                text = "💡 Anti Writer's Block ('What If' Generator)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = "Buntu mau nulis apa selanjutnya? Dapatkan plot twist mendadak atau insiden pemicu emosi!",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
              OutlinedButton(
                onClick = {
                  isLoadingAi = true
                  coroutineScope.launch {
                    aiResultText = geminiService.generateIdeaPrompt(project, characters)
                    isLoadingAi = false
                  }
                },
                enabled = !isLoadingAi,
                modifier = Modifier.fillMaxWidth()
              ) {
                if (isLoadingAi) {
                  CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                  Spacer(modifier = Modifier.width(8.dp))
                  Text("Mencari Ide...")
                } else {
                  Icon(Icons.Default.Lightbulb, contentDescription = null, modifier = Modifier.size(18.dp))
                  Spacer(modifier = Modifier.width(8.dp))
                  Text("Generate Ide 'What If' Acak")
                }
              }
            }
          }
        }

        // Feature 3: Instruksi Custom ke AI
        item {
          Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
          ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
              Text(
                text = "✍️ Instruksi Penulisan Bebas",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
              )
              OutlinedTextField(
                value = customAiInstruction,
                onValueChange = { customAiInstruction = it },
                placeholder = { Text("Misal: Buat narasi pembuka saat salju pertama turun di Seoul, fokus ke rasa sepi...") },
                minLines = 2,
                maxLines = 4,
                modifier = Modifier.fillMaxWidth()
              )
              Button(
                onClick = {
                  if (customAiInstruction.isNotBlank()) {
                    isLoadingAi = true
                    coroutineScope.launch {
                      aiResultText = geminiService.generateCustomWriting(customAiInstruction, project, characters)
                      isLoadingAi = false
                    }
                  }
                },
                enabled = !isLoadingAi && customAiInstruction.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
              ) {
                if (isLoadingAi) {
                  CircularProgressIndicator(modifier = Modifier.size(18.dp), color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 2.dp)
                  Spacer(modifier = Modifier.width(8.dp))
                  Text("Menulis Draf...")
                } else {
                  Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(18.dp))
                  Spacer(modifier = Modifier.width(8.dp))
                  Text("Tuliskan untuk Saya")
                }
              }
            }
          }
        }

        // AI Result Card
        if (aiResultText.isNotBlank()) {
          item {
            Card(
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(18.dp),
              colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
              ),
              border = CardDefaults.outlinedCardBorder()
            ) {
              Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
              ) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                      text = "Hasil Bantuan AI",
                      style = MaterialTheme.typography.titleMedium,
                      fontWeight = FontWeight.Bold
                    )
                  }
                  IconButton(onClick = { aiResultText = "" }, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.Close, contentDescription = "Tutup")
                  }
                }

                Box(
                  modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                    .padding(14.dp)
                ) {
                  Text(
                    text = aiResultText,
                    style = MaterialTheme.typography.bodyMedium,
                    lineHeight = 22.sp
                  )
                }

                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                  // Copy button
                  OutlinedButton(
                    onClick = {
                      val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                      val clip = ClipData.newPlainText("AU Story Prompt", aiResultText)
                      clipboard.setPrimaryClip(clip)
                      onShowMessage("Teks berhasil disalin ke clipboard!")
                    },
                    modifier = Modifier.weight(1f)
                  ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Salin Teks")
                  }

                  // Add to Plot Board button
                  Button(
                    onClick = {
                      val newCard = PlotCardItem(
                        id = UUID.randomUUID().toString(),
                        projectId = project.id,
                        stage = PlotStage.OUTLINE,
                        status = CardStatus.DRAFT,
                        title = "Ide AI: " + aiResultText.lines().firstOrNull { it.isNotBlank() }?.take(40) ?: "Adegan Baru",
                        summary = aiResultText.take(500),
                        sequenceNumber = 1,
                        estimatedWords = 800
                      )
                      onAddCardToPlotBoard(newCard)
                      onShowMessage("Berhasil ditambahkan ke Plot Board!")
                    },
                    modifier = Modifier.weight(1f)
                  ) {
                    Icon(Icons.Default.PostAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Ke Plot Board")
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}
