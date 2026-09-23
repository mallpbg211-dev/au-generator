package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.AuDatabase
import com.example.data.local.CharacterEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class CharacterRoomTest {

  private lateinit var db: AuDatabase

  @Before
  fun createDb() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    db = Room.inMemoryDatabaseBuilder(context, AuDatabase::class.java)
      .allowMainThreadQueries()
      .build()
  }

  @After
  fun closeDb() {
    db.close()
  }

  @Test
  fun insertQueryAndDeleteCharacter() = runBlocking {
    val characterDao = db.characterDao()
    val character = CharacterEntity(
      id = "char_test_1",
      projectId = "proj_test_1",
      stageName = "Jungkook",
      realName = "Jeon Jung-kook",
      groupName = "BTS",
      position = "Main Vocal",
      gender = "Cowok",
      avatarKey = "microphone",
      roleInAU = "Barista shift malam",
      traits = "Pendiam, perhatian, MBTI: ISFP",
      createdAt = System.currentTimeMillis()
    )

    characterDao.insertCharacter(character)

    val list = characterDao.getCharactersByProject("proj_test_1").first()
    assertEquals(1, list.size)
    val fetched = list[0].toModel()
    assertEquals("Jungkook", fetched.stageName)
    assertEquals("Jeon Jung-kook", fetched.realName)
    assertEquals("BTS", fetched.groupName)
    assertEquals("Barista shift malam", fetched.roleInAU)

    characterDao.deleteCharacterById("char_test_1")
    val listAfterDelete = characterDao.getCharactersByProject("proj_test_1").first()
    assertEquals(0, listAfterDelete.size)
  }
}
