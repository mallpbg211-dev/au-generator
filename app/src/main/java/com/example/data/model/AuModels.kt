package com.example.data.model

import java.util.UUID

enum class ProjectStatus(val label: String) {
  DRAFT("Draft"),
  ONGOING("Ongoing"),
  HIATUS("Hiatus"),
  TAMAT("Tamat"),
  SELESAI("Selesai")
}

enum class PlotStage(val title: String, val description: String) {
  OUTLINE("Ide & Outline", "Catatan awal, premis, dan ide kasar cerita"),
  BABAK_1("Babak 1 (Awal)", "Pengenalan karakter, latar, dan pemicu masalah"),
  BABAK_2("Babak 2 (Konflik)", "Ketegangan meningkat, rahasia terbongkar, titik balik"),
  BABAK_3("Babak 3 (Klimaks & Akhir)", "Konfrontasi puncak, resolusi, dan ending")
}

enum class CardStatus(val label: String) {
  DRAFT("Draft"),
  REVISI("Revisi"),
  SELESAI("Selesai")
}

enum class TonePreset(val title: String, val desc: String) {
  FORMAL("Formal & Sastra", "Bahasa baku terstruktur, narasi elegan dan mendalam"),
  SANTAI_GAUL("Santai & Gaul (AU Twitter)", "Dialog cepat natural khas anak muda, selingan istilah pop"),
  PUITIS("Puitis & Atmosferik", "Penuh metafora, deskripsi indera tajam, emosional"),
  TO_THE_POINT("To-The-Point & Cepat", "Alur lincah, minim deskripsi bertele-tele, fokus aksi")
}

enum class GenreTonePreset(val title: String, val desc: String, val badgeColorHex: String) {
  FLUFF("Fluff", "Manis, gemas, wholesome, bikin senyum sendiri", "#FF80AB"),
  ANGST("Angst", "Menyakitkan, perpisahan, luka batin, air mata", "#90A4AE"),
  CRACK("Crack & Humor", "Kocak absurd, banter receh, kekacauan tak terduga", "#FFD54F"),
  HURT_COMFORT("Hurt / Comfort", "Penyembuhan luka, pelukan hangat setelah badai", "#81C784")
}

enum class PovPreset(val title: String, val sample: String) {
  FIRST_PERSON("Orang Pertama (POV 1)", "Menggunakan 'Aku / Saya'"),
  THIRD_PERSON("Orang Ketiga (POV 3)", "Menggunakan 'Dia / Mereka / Nama Tokoh'")
}

enum class ThemePreset(val title: String, val description: String) {
  SOFT_PASTEL("Soft Pastel", "Pink pastel, lavender lembut, dan putih bersih"),
  DARK_AESTHETIC("Dark Mode Aesthetic", "Hitam obsidian, navy malam, dengan aksen neon ungu-pink"),
  MINIMAL_MONOCHROME("Minimalis Monokrom", "Abu-abu slate, monokrom modern dengan aksen mawar tegas"),
  CUSTOM("Custom Theme", "Bebas pilih warna primer, aksen, dan background")
}

data class ThemeCustomConfig(
  val primaryHex: String = "#C084FC",
  val secondaryHex: String = "#F43F5E",
  val backgroundHex: String = "#0B0813",
  val surfaceHex: String = "#171326",
  val isDark: Boolean = true
)

data class ProjectItem(
  val id: String = UUID.randomUUID().toString(),
  val title: String,
  val fandom: String,
  val status: ProjectStatus = ProjectStatus.DRAFT,
  val tags: List<String> = emptyList(),
  val dailyWordTarget: Int = 1000,
  val currentWordCount: Int = 0,
  val description: String = "",
  val auSettingName: String = "Coffee Shop AU",
  val auSettingPremise: String = "",
  val auSettingTropes: List<String> = emptyList(),
  val tone: TonePreset = TonePreset.SANTAI_GAUL,
  val genreTone: GenreTonePreset = GenreTonePreset.FLUFF,
  val pov: PovPreset = PovPreset.THIRD_PERSON,
  val customStyleDescription: String = "",
  val fandomVocabulary: List<String> = listOf("bias", "comeback", "dorm", "trainee", "fansign"),
  val createdAt: Long = System.currentTimeMillis(),
  val updatedAt: Long = System.currentTimeMillis()
)

data class CharacterItem(
  val id: String = UUID.randomUUID().toString(),
  val projectId: String,
  val stageName: String,
  val realName: String,
  val groupName: String,
  val position: String,
  val gender: String, // "Cowok", "Cewek", "Lainnya"
  val avatarKey: String = "heart", // idol avatar icon key
  val roleInAU: String = "",
  val traits: String = "",
  val createdAt: Long = System.currentTimeMillis()
)

data class RelationshipItem(
  val id: String = UUID.randomUUID().toString(),
  val projectId: String,
  val fromCharacterId: String,
  val toCharacterId: String,
  val tag: String, // "OTP", "Rival", "Sahabat", "Found Family", "Poly", "Enemies to Lovers", "Secret Crush", "Kustom"
  val notes: String = "",
  val colorHex: String = "#FF4081"
)

data class PlotCardItem(
  val id: String = UUID.randomUUID().toString(),
  val projectId: String,
  val stage: PlotStage = PlotStage.OUTLINE,
  val status: CardStatus = CardStatus.DRAFT,
  val title: String,
  val summary: String,
  val sequenceNumber: Int = 1,
  val characterIds: List<String> = emptyList(),
  val notes: String = "",
  val estimatedWords: Int = 500,
  val createdAt: Long = System.currentTimeMillis()
)

data class AuTemplatePreset(
  val id: String,
  val name: String,
  val tagline: String,
  val premise: String,
  val tropes: List<String>,
  val iconEmoji: String,
  val suggestedTone: GenreTonePreset = GenreTonePreset.FLUFF
)
