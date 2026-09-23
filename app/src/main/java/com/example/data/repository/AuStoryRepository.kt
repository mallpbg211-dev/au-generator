package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.data.local.AuDatabase
import com.example.data.local.CharacterEntity
import com.example.data.local.PlotCardEntity
import com.example.data.local.ProjectEntity
import com.example.data.local.RelationshipEntity
import com.example.data.model.CardStatus
import com.example.data.model.CharacterItem
import com.example.data.model.GenreTonePreset
import com.example.data.model.PlotCardItem
import com.example.data.model.PlotStage
import com.example.data.model.PovPreset
import com.example.data.model.ProjectItem
import com.example.data.model.ProjectStatus
import com.example.data.model.RelationshipItem
import com.example.data.model.ThemeCustomConfig
import com.example.data.model.ThemePreset
import com.example.data.model.TonePreset
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

class AuStoryRepository(private val context: Context) {
  private val database = AuDatabase.getDatabase(context)
  private val projectDao = database.projectDao()
  private val characterDao = database.characterDao()
  private val relationshipDao = database.relationshipDao()
  private val plotCardDao = database.plotCardDao()

  private val prefs: SharedPreferences =
    context.getSharedPreferences("au_builder_prefs", Context.MODE_PRIVATE)

  private val _activeProjectId = MutableStateFlow<String?>(
    prefs.getString("active_project_id", null)
  )
  val activeProjectId: StateFlow<String?> = _activeProjectId.asStateFlow()

  private val _themePreset = MutableStateFlow(
    try {
      ThemePreset.valueOf(prefs.getString("theme_preset", ThemePreset.SOFT_PASTEL.name)!!)
    } catch (e: Exception) {
      ThemePreset.SOFT_PASTEL
    }
  )
  val themePreset: StateFlow<ThemePreset> = _themePreset.asStateFlow()

  private val _customThemeConfig = MutableStateFlow(
    ThemeCustomConfig(
      primaryHex = prefs.getString("custom_primary", "#C084FC") ?: "#C084FC",
      secondaryHex = prefs.getString("custom_secondary", "#F43F5E") ?: "#F43F5E",
      backgroundHex = prefs.getString("custom_bg", "#0B0813") ?: "#0B0813",
      surfaceHex = prefs.getString("custom_surface", "#171326") ?: "#171326",
      isDark = prefs.getBoolean("custom_is_dark", true)
    )
  )
  val customThemeConfig: StateFlow<ThemeCustomConfig> = _customThemeConfig.asStateFlow()

  init {
    CoroutineScope(Dispatchers.IO).launch {
      seedInitialDataIfEmpty()
    }
  }

  // --- Themes ---
  fun setThemePreset(preset: ThemePreset) {
    _themePreset.value = preset
    prefs.edit().putString("theme_preset", preset.name).apply()
  }

  fun updateCustomTheme(config: ThemeCustomConfig) {
    _customThemeConfig.value = config
    prefs.edit()
      .putString("custom_primary", config.primaryHex)
      .putString("custom_secondary", config.secondaryHex)
      .putString("custom_bg", config.backgroundHex)
      .putString("custom_surface", config.surfaceHex)
      .putBoolean("custom_is_dark", config.isDark)
      .apply()
  }

  // --- Projects ---
  fun getAllProjects(): Flow<List<ProjectItem>> {
    return projectDao.getAllProjects().map { list -> list.map { it.toModel() } }
  }

  fun getProject(id: String): Flow<ProjectItem?> {
    return projectDao.getProjectById(id).map { it?.toModel() }
  }

  fun setActiveProject(projectId: String) {
    _activeProjectId.value = projectId
    prefs.edit().putString("active_project_id", projectId).apply()
  }

  suspend fun saveProject(project: ProjectItem) = withContext(Dispatchers.IO) {
    projectDao.insertProject(ProjectEntity.fromModel(project))
    if (_activeProjectId.value == null) {
      setActiveProject(project.id)
    }
  }

  suspend fun updateProject(project: ProjectItem) = withContext(Dispatchers.IO) {
    val updated = project.copy(updatedAt = System.currentTimeMillis())
    projectDao.updateProject(ProjectEntity.fromModel(updated))
  }

  suspend fun deleteProject(projectId: String) = withContext(Dispatchers.IO) {
    characterDao.deleteCharactersByProject(projectId)
    relationshipDao.getRelationshipsByProject(projectId)
    plotCardDao.deletePlotCardsByProject(projectId)
    projectDao.deleteProjectById(projectId)
    if (_activeProjectId.value == projectId) {
      _activeProjectId.value = null
      prefs.edit().remove("active_project_id").apply()
    }
  }

  // --- Characters ---
  fun getCharacters(projectId: String): Flow<List<CharacterItem>> {
    return characterDao.getCharactersByProject(projectId).map { list -> list.map { it.toModel() } }
  }

  suspend fun saveCharacter(character: CharacterItem) = withContext(Dispatchers.IO) {
    characterDao.insertCharacter(CharacterEntity.fromModel(character))
  }

  suspend fun updateCharacter(character: CharacterItem) = withContext(Dispatchers.IO) {
    characterDao.updateCharacter(CharacterEntity.fromModel(character))
  }

  suspend fun deleteCharacter(characterId: String) = withContext(Dispatchers.IO) {
    relationshipDao.deleteRelationshipsForCharacter(characterId)
    characterDao.deleteCharacterById(characterId)
  }

  // --- Relationships ---
  fun getRelationships(projectId: String): Flow<List<RelationshipItem>> {
    return relationshipDao.getRelationshipsByProject(projectId).map { list -> list.map { it.toModel() } }
  }

  suspend fun saveRelationship(relationship: RelationshipItem) = withContext(Dispatchers.IO) {
    relationshipDao.insertRelationship(RelationshipEntity.fromModel(relationship))
  }

  suspend fun updateRelationship(relationship: RelationshipItem) = withContext(Dispatchers.IO) {
    relationshipDao.updateRelationship(RelationshipEntity.fromModel(relationship))
  }

  suspend fun deleteRelationship(relationshipId: String) = withContext(Dispatchers.IO) {
    relationshipDao.deleteRelationshipById(relationshipId)
  }

  // --- Plot Cards ---
  fun getPlotCards(projectId: String): Flow<List<PlotCardItem>> {
    return plotCardDao.getPlotCardsByProject(projectId).map { list -> list.map { it.toModel() } }
  }

  suspend fun savePlotCard(card: PlotCardItem) = withContext(Dispatchers.IO) {
    plotCardDao.insertPlotCard(PlotCardEntity.fromModel(card))
  }

  suspend fun updatePlotCard(card: PlotCardItem) = withContext(Dispatchers.IO) {
    plotCardDao.updatePlotCard(PlotCardEntity.fromModel(card))
  }

  suspend fun deletePlotCard(cardId: String) = withContext(Dispatchers.IO) {
    plotCardDao.deletePlotCardById(cardId)
  }

  // Seed sample data if database is brand new
  private suspend fun seedInitialDataIfEmpty() {
    val existing = projectDao.getProjectByIdDirect("demo_project_1")
    if (existing == null) {
      val demoProjectId = "demo_project_1"
      val demoProject = ProjectItem(
        id = demoProjectId,
        title = "Midnight Americano & Red String",
        fandom = "BTS",
        status = ProjectStatus.ONGOING,
        tags = listOf("BTS", "Coffee Shop", "Soulmate", "Fluff", "Twitter AU"),
        dailyWordTarget = 1500,
        currentWordCount = 3850,
        description = "AU Coffee Shop di Gangnam di mana barista dingin berambut hitam selalu menggambar doodle kucing di cup caramel latte pelanggan tetapnya.",
        auSettingName = "Coffee Shop & Soulmate AU",
        auSettingPremise = "Hujan deras Hongdae, pesanan tertukar, dan timer merah di pergelangan tangan yang menghitung mundur 10 detik.",
        auSettingTropes = listOf("Meet cute", "Secret notes", "Slow burn", "Soulmate counter"),
        tone = TonePreset.SANTAI_GAUL,
        genreTone = GenreTonePreset.FLUFF,
        pov = PovPreset.THIRD_PERSON,
        customStyleDescription = "Gaya AU Twitter lokal santai, dialog ceplas-ceplos tapi narasi deskripsi kopi puitis",
        fandomVocabulary = listOf("bias", "comeback", "dorm", "trainee", "fansign", "encore")
      )
      projectDao.insertProject(ProjectEntity.fromModel(demoProject))

      // Characters
      val char1 = CharacterItem(
        id = "char_jk",
        projectId = demoProjectId,
        stageName = "Jungkook",
        realName = "Jeon Jung-kook",
        groupName = "BTS",
        position = "Main Vocal",
        gender = "Cowok",
        avatarKey = "microphone",
        roleInAU = "Barista shift malam & mahasiswa fotografi",
        traits = "Pendiam di awal, perhatian lewat tindakan kecil, suka menggambar doodle di cup kopi, MBTI: ISFP"
      )
      val char2 = CharacterItem(
        id = "char_th",
        projectId = demoProjectId,
        stageName = "Taehyung",
        realName = "Kim Tae-hyung",
        groupName = "BTS",
        position = "Sub Vocal / Visual",
        gender = "Cowok",
        avatarKey = "palette",
        roleInAU = "Desainer grafis freelance langganan kafe",
        traits = "Hangat, ekspresif, pecinta jazz lama, sering kehilangan payung, MBTI: ENFP"
      )
      val char3 = CharacterItem(
        id = "char_jm",
        projectId = demoProjectId,
        stageName = "Jimin",
        realName = "Park Ji-min",
        groupName = "BTS",
        position = "Main Dancer",
        gender = "Cowok",
        avatarKey = "sparkle",
        roleInAU = "Pemilik kedai kopi & teman curhat",
        traits = "Pengamat tajam, suka menggoda barista dan pelanggan setianya, MBTI: ENFJ"
      )

      characterDao.insertCharacter(CharacterEntity.fromModel(char1))
      characterDao.insertCharacter(CharacterEntity.fromModel(char2))
      characterDao.insertCharacter(CharacterEntity.fromModel(char3))

      // Relationships
      val rel1 = RelationshipItem(
        id = "rel_1",
        projectId = demoProjectId,
        fromCharacterId = "char_jk",
        toCharacterId = "char_th",
        tag = "OTP / Soulmate",
        notes = "Saling curi pandang lewat kaca etalase kue, timer takdir mulai bersinar.",
        colorHex = "#FF4081"
      )
      val rel2 = RelationshipItem(
        id = "rel_2",
        projectId = demoProjectId,
        fromCharacterId = "char_jm",
        toCharacterId = "char_jk",
        tag = "Sahabat / Found Family",
        notes = "Bos kafe sekaligus kakak tempat curhat segala kegelisahan.",
        colorHex = "#9C27B0"
      )
      relationshipDao.insertRelationship(RelationshipEntity.fromModel(rel1))
      relationshipDao.insertRelationship(RelationshipEntity.fromModel(rel2))

      // Plot Cards
      val card1 = PlotCardItem(
        id = "plot_1",
        projectId = demoProjectId,
        stage = PlotStage.OUTLINE,
        status = CardStatus.SELESAI,
        title = "Prolog: Sketsa Kucing di Cup Kertas",
        summary = "Taehyung selalu memesan Americano dingin tiap jam 8 malam. Jungkook mulai menggambar ekspresi kucing berbeda tiap hari.",
        sequenceNumber = 1,
        characterIds = listOf("char_jk", "char_th"),
        estimatedWords = 1200
      )
      val card2 = PlotCardItem(
        id = "plot_2",
        projectId = demoProjectId,
        stage = PlotStage.BABAK_1,
        status = CardStatus.SELESAI,
        title = "Bab 1: Hujan Deras & Payung Kuning",
        summary = "Hujan badai mengguyur Seoul. Kafe tutup lebih awal, tapi Taehyung terjebak di teras tanpa payung. Jungkook menyodorkan payung cadangannya.",
        sequenceNumber = 2,
        characterIds = listOf("char_jk", "char_th", "char_jm"),
        estimatedWords = 2100
      )
      val card3 = PlotCardItem(
        id = "plot_3",
        projectId = demoProjectId,
        stage = PlotStage.BABAK_2,
        status = CardStatus.DRAFT,
        title = "Bab 2: Countdown 00:00:10",
        summary = "Di acara pameran foto galeri, nomor timer di pergelangan tangan keduanya mendadak bergetar dan berpendar merah terang.",
        sequenceNumber = 3,
        characterIds = listOf("char_jk", "char_th"),
        estimatedWords = 1800
      )

      plotCardDao.insertPlotCard(PlotCardEntity.fromModel(card1))
      plotCardDao.insertPlotCard(PlotCardEntity.fromModel(card2))
      plotCardDao.insertPlotCard(PlotCardEntity.fromModel(card3))

      if (_activeProjectId.value == null) {
        setActiveProject(demoProjectId)
      }
    }
  }
}
