package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.CardStatus
import com.example.data.model.CharacterItem
import com.example.data.model.GenreTonePreset
import com.example.data.model.PlotCardItem
import com.example.data.model.PlotStage
import com.example.data.model.PovPreset
import com.example.data.model.ProjectItem
import com.example.data.model.ProjectStatus
import com.example.data.model.RelationshipItem
import com.example.data.model.TonePreset

@Entity(tableName = "projects")
data class ProjectEntity(
  @PrimaryKey val id: String,
  val title: String,
  val fandom: String,
  val status: String,
  val tags: List<String>,
  val dailyWordTarget: Int,
  val currentWordCount: Int,
  val description: String,
  val auSettingName: String,
  val auSettingPremise: String,
  val auSettingTropes: List<String>,
  val tone: String,
  val genreTone: String,
  val pov: String,
  val customStyleDescription: String,
  val fandomVocabulary: List<String>,
  val createdAt: Long,
  val updatedAt: Long
) {
  fun toModel(): ProjectItem = ProjectItem(
    id = id,
    title = title,
    fandom = fandom,
    status = try { ProjectStatus.valueOf(status) } catch (e: Exception) { ProjectStatus.DRAFT },
    tags = tags,
    dailyWordTarget = dailyWordTarget,
    currentWordCount = currentWordCount,
    description = description,
    auSettingName = auSettingName,
    auSettingPremise = auSettingPremise,
    auSettingTropes = auSettingTropes,
    tone = try { TonePreset.valueOf(tone) } catch (e: Exception) { TonePreset.SANTAI_GAUL },
    genreTone = try { GenreTonePreset.valueOf(genreTone) } catch (e: Exception) { GenreTonePreset.FLUFF },
    pov = try { PovPreset.valueOf(pov) } catch (e: Exception) { PovPreset.THIRD_PERSON },
    customStyleDescription = customStyleDescription,
    fandomVocabulary = fandomVocabulary,
    createdAt = createdAt,
    updatedAt = updatedAt
  )

  companion object {
    fun fromModel(model: ProjectItem): ProjectEntity = ProjectEntity(
      id = model.id,
      title = model.title,
      fandom = model.fandom,
      status = model.status.name,
      tags = model.tags,
      dailyWordTarget = model.dailyWordTarget,
      currentWordCount = model.currentWordCount,
      description = model.description,
      auSettingName = model.auSettingName,
      auSettingPremise = model.auSettingPremise,
      auSettingTropes = model.auSettingTropes,
      tone = model.tone.name,
      genreTone = model.genreTone.name,
      pov = model.pov.name,
      customStyleDescription = model.customStyleDescription,
      fandomVocabulary = model.fandomVocabulary,
      createdAt = model.createdAt,
      updatedAt = model.updatedAt
    )
  }
}

@Entity(tableName = "characters")
data class CharacterEntity(
  @PrimaryKey val id: String,
  val projectId: String,
  val stageName: String,
  val realName: String,
  val groupName: String,
  val position: String,
  val gender: String,
  val avatarKey: String,
  val roleInAU: String,
  val traits: String,
  val createdAt: Long
) {
  fun toModel(): CharacterItem = CharacterItem(
    id = id,
    projectId = projectId,
    stageName = stageName,
    realName = realName,
    groupName = groupName,
    position = position,
    gender = gender,
    avatarKey = avatarKey,
    roleInAU = roleInAU,
    traits = traits,
    createdAt = createdAt
  )

  companion object {
    fun fromModel(model: CharacterItem): CharacterEntity = CharacterEntity(
      id = model.id,
      projectId = model.projectId,
      stageName = model.stageName,
      realName = model.realName,
      groupName = model.groupName,
      position = model.position,
      gender = model.gender,
      avatarKey = model.avatarKey,
      roleInAU = model.roleInAU,
      traits = model.traits,
      createdAt = model.createdAt
    )
  }
}

@Entity(tableName = "relationships")
data class RelationshipEntity(
  @PrimaryKey val id: String,
  val projectId: String,
  val fromCharacterId: String,
  val toCharacterId: String,
  val tag: String,
  val notes: String,
  val colorHex: String
) {
  fun toModel(): RelationshipItem = RelationshipItem(
    id = id,
    projectId = projectId,
    fromCharacterId = fromCharacterId,
    toCharacterId = toCharacterId,
    tag = tag,
    notes = notes,
    colorHex = colorHex
  )

  companion object {
    fun fromModel(model: RelationshipItem): RelationshipEntity = RelationshipEntity(
      id = model.id,
      projectId = model.projectId,
      fromCharacterId = model.fromCharacterId,
      toCharacterId = model.toCharacterId,
      tag = model.tag,
      notes = model.notes,
      colorHex = model.colorHex
    )
  }
}

@Entity(tableName = "plot_cards")
data class PlotCardEntity(
  @PrimaryKey val id: String,
  val projectId: String,
  val stage: String,
  val status: String,
  val title: String,
  val summary: String,
  val sequenceNumber: Int,
  val characterIds: List<String>,
  val notes: String,
  val estimatedWords: Int,
  val createdAt: Long
) {
  fun toModel(): PlotCardItem = PlotCardItem(
    id = id,
    projectId = projectId,
    stage = try { PlotStage.valueOf(stage) } catch (e: Exception) { PlotStage.OUTLINE },
    status = try { CardStatus.valueOf(status) } catch (e: Exception) { CardStatus.DRAFT },
    title = title,
    summary = summary,
    sequenceNumber = sequenceNumber,
    characterIds = characterIds,
    notes = notes,
    estimatedWords = estimatedWords,
    createdAt = createdAt
  )

  companion object {
    fun fromModel(model: PlotCardItem): PlotCardEntity = PlotCardEntity(
      id = model.id,
      projectId = model.projectId,
      stage = model.stage.name,
      status = model.status.name,
      title = model.title,
      summary = model.summary,
      sequenceNumber = model.sequenceNumber,
      characterIds = model.characterIds,
      notes = model.notes,
      estimatedWords = model.estimatedWords,
      createdAt = model.createdAt
    )
  }
}
