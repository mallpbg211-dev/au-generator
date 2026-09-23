package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ProjectDao {
  @Query("SELECT * FROM projects ORDER BY updatedAt DESC")
  fun getAllProjects(): Flow<List<ProjectEntity>>

  @Query("SELECT * FROM projects WHERE id = :id LIMIT 1")
  fun getProjectById(id: String): Flow<ProjectEntity?>

  @Query("SELECT * FROM projects WHERE id = :id LIMIT 1")
  suspend fun getProjectByIdDirect(id: String): ProjectEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertProject(project: ProjectEntity)

  @Update
  suspend fun updateProject(project: ProjectEntity)

  @Query("DELETE FROM projects WHERE id = :id")
  suspend fun deleteProjectById(id: String)
}

@Dao
interface CharacterDao {
  @Query("SELECT * FROM characters WHERE projectId = :projectId ORDER BY createdAt ASC")
  fun getCharactersByProject(projectId: String): Flow<List<CharacterEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertCharacter(character: CharacterEntity)

  @Update
  suspend fun updateCharacter(character: CharacterEntity)

  @Query("DELETE FROM characters WHERE id = :id")
  suspend fun deleteCharacterById(id: String)

  @Query("DELETE FROM characters WHERE projectId = :projectId")
  suspend fun deleteCharactersByProject(projectId: String)
}

@Dao
interface RelationshipDao {
  @Query("SELECT * FROM relationships WHERE projectId = :projectId")
  fun getRelationshipsByProject(projectId: String): Flow<List<RelationshipEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertRelationship(relationship: RelationshipEntity)

  @Update
  suspend fun updateRelationship(relationship: RelationshipEntity)

  @Query("DELETE FROM relationships WHERE id = :id")
  suspend fun deleteRelationshipById(id: String)

  @Query("DELETE FROM relationships WHERE fromCharacterId = :characterId OR toCharacterId = :characterId")
  suspend fun deleteRelationshipsForCharacter(characterId: String)
}

@Dao
interface PlotCardDao {
  @Query("SELECT * FROM plot_cards WHERE projectId = :projectId ORDER BY sequenceNumber ASC, createdAt ASC")
  fun getPlotCardsByProject(projectId: String): Flow<List<PlotCardEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertPlotCard(card: PlotCardEntity)

  @Update
  suspend fun updatePlotCard(card: PlotCardEntity)

  @Query("DELETE FROM plot_cards WHERE id = :id")
  suspend fun deletePlotCardById(id: String)

  @Query("DELETE FROM plot_cards WHERE projectId = :projectId")
  suspend fun deletePlotCardsByProject(projectId: String)
}
