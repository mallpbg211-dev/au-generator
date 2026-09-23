package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
  entities = [
    ProjectEntity::class,
    CharacterEntity::class,
    RelationshipEntity::class,
    PlotCardEntity::class
  ],
  version = 1,
  exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AuDatabase : RoomDatabase() {
  abstract fun projectDao(): ProjectDao
  abstract fun characterDao(): CharacterDao
  abstract fun relationshipDao(): RelationshipDao
  abstract fun plotCardDao(): PlotCardDao

  companion object {
    @Volatile
    private var INSTANCE: AuDatabase? = null

    fun getDatabase(context: Context): AuDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          AuDatabase::class.java,
          "au_story_builder.db"
        ).fallbackToDestructiveMigration().build()
        INSTANCE = instance
        instance
      }
    }
  }
}
