package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.FolderSpecial
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.ViewKanban
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.ProjectItem
import com.example.ui.AuStoryViewModel
import com.example.ui.screens.characters.CharacterBuilderScreen
import com.example.ui.screens.plot.PlotBoardScreen
import com.example.ui.screens.project.ProjectManagementScreen
import com.example.ui.screens.relationship.RelationshipMapScreen
import com.example.ui.screens.style_ai.StyleAndAiScreen
import com.example.ui.screens.templates.AuTemplateScreen
import com.example.ui.screens.theme_settings.ThemeSettingsSheet
import com.example.ui.theme.AuStoryBuilderTheme
import com.example.ui.theme.MyApplicationTheme

enum class AuNavigationDestination(val label: String, val icon: ImageVector) {
  CHARACTERS("Karakter", Icons.Default.People),
  RELATIONSHIPS("Relasi", Icons.Default.Hub),
  PLOT_BOARD("Plot", Icons.Default.ViewKanban),
  TEMPLATES("Setting AU", Icons.Default.AutoStories),
  STYLE_AND_AI("Gaya & AI", Icons.Default.EditNote),
  PROJECTS("Proyek", Icons.Default.FolderSpecial)
}

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      AuStoryApp()
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuStoryApp(viewModel: AuStoryViewModel = viewModel()) {
  val themePreset by viewModel.themePreset.collectAsState()
  val customConfig by viewModel.customThemeConfig.collectAsState()

  val allProjects by viewModel.allProjects.collectAsState()
  val activeProjectId by viewModel.activeProjectId.collectAsState()
  val activeProject by viewModel.activeProject.collectAsState()

  val characters by viewModel.characters.collectAsState()
  val relationships by viewModel.relationships.collectAsState()
  val plotCards by viewModel.plotCards.collectAsState()

  var currentDestination by remember { mutableStateOf(AuNavigationDestination.CHARACTERS) }
  var showThemeSheet by remember { mutableStateOf(false) }
  var showProjectMenu by remember { mutableStateOf(false) }

  val snackbarHostState = remember { SnackbarHostState() }

  LaunchedEffect(Unit) {
    viewModel.snackbarMessages.collect { msg ->
      snackbarHostState.showSnackbar(msg)
    }
  }

  AuStoryBuilderTheme(
    themePreset = themePreset,
    customConfig = customConfig
  ) {
    Scaffold(
      modifier = Modifier.fillMaxSize(),
      snackbarHost = { SnackbarHost(snackbarHostState) },
      topBar = {
        TopAppBar(
          title = {
            Column {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = "AU Story Builder",
                  style = MaterialTheme.typography.titleMedium,
                  fontWeight = FontWeight.Bold,
                  color = MaterialTheme.colorScheme.primary
                )
              }
              // Active project indicator badge
              Box {
                Row(
                  modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .clickable { showProjectMenu = true }
                    .padding(vertical = 2.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text(
                    text = "📖 ${activeProject?.title ?: "Belum ada proyek"}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                  )
                  Text(
                    text = " ▾",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                  )
                }

                DropdownMenu(
                  expanded = showProjectMenu,
                  onDismissRequest = { showProjectMenu = false }
                ) {
                  allProjects.forEach { proj ->
                    val isCur = proj.id == activeProjectId
                    DropdownMenuItem(
                      text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                          if (isCur) {
                            Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(6.dp))
                          }
                          Text(
                            text = proj.title,
                            fontWeight = if (isCur) FontWeight.Bold else FontWeight.Normal
                          )
                        }
                      },
                      onClick = {
                        viewModel.selectProject(proj.id)
                        showProjectMenu = false
                      }
                    )
                  }
                  DropdownMenuItem(
                    text = {
                      Text(
                        text = "+ Kelola / Buat Proyek Lain",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                      )
                    },
                    onClick = {
                      showProjectMenu = false
                      currentDestination = AuNavigationDestination.PROJECTS
                    }
                  )
                }
              }
            }
          },
          actions = {
            IconButton(
              onClick = { showThemeSheet = true },
              modifier = Modifier.testTag("theme_selector_button")
            ) {
              Icon(
                imageVector = Icons.Default.Palette,
                contentDescription = "Pengaturan Tema & Warna",
                tint = MaterialTheme.colorScheme.primary
              )
            }
          },
          colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface
          )
        )
      },
      bottomBar = {
        NavigationBar(
          containerColor = MaterialTheme.colorScheme.surface,
          tonalElevation = 8.dp
        ) {
          AuNavigationDestination.values().forEach { dest ->
            val isSelected = currentDestination == dest
            NavigationBarItem(
              selected = isSelected,
              onClick = { currentDestination = dest },
              icon = {
                Icon(
                  imageVector = dest.icon,
                  contentDescription = dest.label,
                  modifier = Modifier.size(20.dp)
                )
              },
              label = {
                Text(
                  text = dest.label,
                  style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
              },
              colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                indicatorColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
              )
            )
          }
        }
      }
    ) { innerPadding ->
      Surface(
        modifier = Modifier
          .fillMaxSize()
          .padding(innerPadding),
        color = MaterialTheme.colorScheme.background
      ) {
        val currentProj = activeProject

        if (currentDestination == AuNavigationDestination.PROJECTS) {
          // Projects screen works even without an active project
          ProjectManagementScreen(
            projects = allProjects,
            activeProjectId = activeProjectId,
            onSelectProject = { viewModel.selectProject(it) },
            onSaveProject = { viewModel.saveProject(it) },
            onDeleteProject = { viewModel.deleteProject(it) }
          )
        } else if (currentProj == null) {
          // Empty state if no project selected
          NoProjectState(
            onOpenProjects = { currentDestination = AuNavigationDestination.PROJECTS }
          )
        } else {
          when (currentDestination) {
            AuNavigationDestination.CHARACTERS -> {
              CharacterBuilderScreen(
                characters = characters,
                currentProjectId = currentProj.id,
                onSaveCharacter = { viewModel.saveCharacter(it) },
                onDeleteCharacter = { viewModel.deleteCharacter(it) }
              )
            }
            AuNavigationDestination.RELATIONSHIPS -> {
              RelationshipMapScreen(
                characters = characters,
                relationships = relationships,
                currentProjectId = currentProj.id,
                onSaveRelationship = { viewModel.saveRelationship(it) },
                onDeleteRelationship = { viewModel.deleteRelationship(it) }
              )
            }
            AuNavigationDestination.PLOT_BOARD -> {
              PlotBoardScreen(
                plotCards = plotCards,
                characters = characters,
                currentProjectId = currentProj.id,
                onSaveCard = { viewModel.savePlotCard(it) },
                onDeleteCard = { viewModel.deletePlotCard(it) }
              )
            }
            AuNavigationDestination.TEMPLATES -> {
              AuTemplateScreen(
                project = currentProj,
                onApplyTemplateToProject = { settingName, premise, tropes ->
                  viewModel.applySettingTemplate(settingName, premise, tropes)
                },
                onAddCardToPlotBoard = { viewModel.savePlotCard(it) },
                onShowMessage = { viewModel.showMessage(it) }
              )
            }
            AuNavigationDestination.STYLE_AND_AI -> {
              StyleAndAiScreen(
                project = currentProj,
                characters = characters,
                geminiService = viewModel.geminiService,
                onUpdateProject = { viewModel.updateProject(it) },
                onAddCardToPlotBoard = { viewModel.savePlotCard(it) },
                onShowMessage = { viewModel.showMessage(it) }
              )
            }
            AuNavigationDestination.PROJECTS -> {
              // handled above
            }
          }
        }
      }
    }

    // Modal Bottom Sheet for Theme Settings
    if (showThemeSheet) {
      ThemeSettingsSheet(
        currentPreset = themePreset,
        customConfig = customConfig,
        onSelectPreset = {
          viewModel.setThemePreset(it)
        },
        onUpdateCustomConfig = {
          viewModel.updateCustomThemeConfig(it)
        },
        onDismiss = { showThemeSheet = false }
      )
    }
  }
}

@Composable
fun NoProjectState(onOpenProjects: () -> Unit) {
  Box(
    modifier = Modifier
      .fillMaxSize()
      .padding(24.dp),
    contentAlignment = Alignment.Center
  ) {
    Card(
      shape = RoundedCornerShape(20.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
      Column(
        modifier = Modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        Text("📖", style = MaterialTheme.typography.displayMedium)
        Text(
          text = "Belum Ada Proyek AU Dipilih",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold
        )
        Text(
          text = "Buat atau pilih salah satu proyek AU fanfiction untuk mulai merancang karakter, relasi, dan alur.",
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        androidx.compose.material3.Button(onClick = onOpenProjects) {
          Text("Buka Organisasi Proyek")
        }
      }
    }
  }
}

// Retained for backward compatibility with testing suite
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
  Text(text = "Hello $name!", modifier = modifier)
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
  MyApplicationTheme { Greeting("Android") }
}
