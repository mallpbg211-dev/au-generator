package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ai.GeminiWritingService
import com.example.data.model.CharacterItem
import com.example.data.model.PlotCardItem
import com.example.data.model.ProjectItem
import com.example.data.model.RelationshipItem
import com.example.data.model.ThemeCustomConfig
import com.example.data.model.ThemePreset
import com.example.data.repository.AuStoryRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class AuStoryViewModel(application: Application) : AndroidViewModel(application) {
  private val repository = AuStoryRepository(application)
  val geminiService = GeminiWritingService()

  val themePreset: StateFlow<ThemePreset> = repository.themePreset
  val customThemeConfig: StateFlow<ThemeCustomConfig> = repository.customThemeConfig
  val allProjects: StateFlow<List<ProjectItem>> = repository.getAllProjects()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val activeProjectId: StateFlow<String?> = repository.activeProjectId

  val activeProject: StateFlow<ProjectItem?> = repository.activeProjectId
    .flatMapLatest { id ->
      if (id != null) repository.getProject(id) else flowOf(null)
    }
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

  val characters: StateFlow<List<CharacterItem>> = repository.activeProjectId
    .flatMapLatest { id ->
      if (id != null) repository.getCharacters(id) else flowOf(emptyList())
    }
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val relationships: StateFlow<List<RelationshipItem>> = repository.activeProjectId
    .flatMapLatest { id ->
      if (id != null) repository.getRelationships(id) else flowOf(emptyList())
    }
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val plotCards: StateFlow<List<PlotCardItem>> = repository.activeProjectId
    .flatMapLatest { id ->
      if (id != null) repository.getPlotCards(id) else flowOf(emptyList())
    }
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  private val _snackbarMessages = MutableSharedFlow<String>()
  val snackbarMessages: SharedFlow<String> = _snackbarMessages.asSharedFlow()

  fun showMessage(msg: String) {
    viewModelScope.launch {
      _snackbarMessages.emit(msg)
    }
  }

  // --- Themes ---
  fun setThemePreset(preset: ThemePreset) {
    repository.setThemePreset(preset)
  }

  fun updateCustomThemeConfig(config: ThemeCustomConfig) {
    repository.updateCustomTheme(config)
  }

  // --- Projects ---
  fun selectProject(projectId: String) {
    repository.setActiveProject(projectId)
  }

  fun saveProject(project: ProjectItem) {
    viewModelScope.launch {
      repository.saveProject(project)
      repository.setActiveProject(project.id)
      showMessage("Proyek '${project.title}' disimpan!")
    }
  }

  fun updateProject(project: ProjectItem) {
    viewModelScope.launch {
      repository.updateProject(project)
      showMessage("Detail proyek diperbarui")
    }
  }

  fun deleteProject(projectId: String) {
    viewModelScope.launch {
      repository.deleteProject(projectId)
      showMessage("Proyek berhasil dihapus")
    }
  }

  // --- Characters ---
  fun saveCharacter(character: CharacterItem) {
    viewModelScope.launch {
      repository.saveCharacter(character)
      showMessage("Karakter ${character.stageName} disimpan!")
    }
  }

  fun deleteCharacter(characterId: String) {
    viewModelScope.launch {
      repository.deleteCharacter(characterId)
      showMessage("Karakter dihapus")
    }
  }

  // --- Relationships ---
  fun saveRelationship(relationship: RelationshipItem) {
    viewModelScope.launch {
      repository.saveRelationship(relationship)
      showMessage("Hubungan '${relationship.tag}' disimpan!")
    }
  }

  fun deleteRelationship(relationshipId: String) {
    viewModelScope.launch {
      repository.deleteRelationship(relationshipId)
      showMessage("Hubungan dihapus")
    }
  }

  // --- Plot Cards ---
  fun savePlotCard(card: PlotCardItem) {
    viewModelScope.launch {
      repository.savePlotCard(card)
      showMessage("Adegan '${card.title}' disimpan!")
    }
  }

  fun deletePlotCard(cardId: String) {
    viewModelScope.launch {
      repository.deletePlotCard(cardId)
      showMessage("Adegan dihapus")
    }
  }

  fun applySettingTemplate(settingName: String, premise: String, tropes: List<String>) {
    val current = activeProject.value ?: return
    val updated = current.copy(
      auSettingName = settingName,
      auSettingPremise = premise,
      auSettingTropes = tropes
    )
    viewModelScope.launch {
      repository.updateProject(updated)
    }
  }
}
