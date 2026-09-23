package com.example.data.model

object AuTemplateCatalog {
  val presets: List<AuTemplatePreset> = listOf(
    AuTemplatePreset(
      id = "coffee_shop",
      name = "Coffee Shop AU",
      tagline = "Hujan deras, pesanan tertukar, dan barista cuek yang hafal jadwalmu.",
      premise = "Sebuah kedai kopi kecil di sudut Hongdae yang tenang. Pelanggan setia yang selalu memesan Iced Americano ekstra shot tanpa senyum, diam-diam selalu diperhatikan oleh barista berambut pirang yang diam-diam menggambar doodle di cup kertasnya setiap hari hujan.",
      tropes = listOf(
        "Meet-cute karena pesanan salah ambil",
        "Secret message on coffee cup",
        "Rainstorm shelter (terjebak hujan bareng)",
        "Grumpy customer x Soft observant barista",
        "Playful cafe regular dynamics"
      ),
      iconEmoji = "☕",
      suggestedTone = GenreTonePreset.FLUFF
    ),
    AuTemplatePreset(
      id = "college_rivals",
      name = "College AU: Studio Rivals",
      tagline = "Senior arsitektur perfeksionis x maba musik energik yang sewa studio bareng.",
      premise = "Karena pemotongan anggaran kampus, mahasiswa jurusan Arsitektur dan Seni Musik terpaksa berbagi gedung studio lama. Ketegangan berbuah persaingan sengit saat miniatur maket tak sengaja terbentur case gitar, memicu perang memo tempel sebelum berujung malam-malam begadang bersama.",
      tropes = listOf(
        "Enemies to lovers",
        "Forced proximity (studio bareng)",
        "Midnight coffee runs & all-nighters",
        "Saling sindir lewat playlist Spotify",
        "Academic rivals with hidden mutual respect"
      ),
      iconEmoji = "🎓",
      suggestedTone = GenreTonePreset.CRACK
    ),
    AuTemplatePreset(
      id = "soulmate_timer",
      name = "Soulmate AU: The Ticking Counter",
      tagline = "Timer merah menyala di pergelangan tangan menghitung mundur menuju takdir.",
      premise = "Setiap orang lahir dengan angka countdown di pergelangan tangan yang menghitung mundur detik hingga mereka menatap mata belahan jiwa. Menjelang 00:00:00, keduanya terjebak di tengah kerumunan konser indie bawah tanah saat listrik mendadak padam.",
      tropes = listOf(
        "Soulmate countdown timer",
        "Red string of fate",
        "Misunderstanding & false alarms",
        "Sudden blackout meet-cute",
        "Angst to fluff resolution"
      ),
      iconEmoji = "⏳",
      suggestedTone = GenreTonePreset.HURT_COMFORT
    ),
    AuTemplatePreset(
      id = "idol_secret_dating",
      name = "Idol & Secret Dating AU",
      tagline = "Rival di puncak chart musik, kekasih rahasia di tangga darurat stasiun TV.",
      premise = "Dua member dari grup idol paling bersaing di industri K-pop diam-diam menjalin hubungan selama dua tahun. Di depan kamera mereka berpura-pura dingin dan menjaga jarak profesional, namun di tangga darurat lantai 4 gedung stasiun TV saat jam rekaman subuh, tangan mereka saling menggenggam erat.",
      tropes = listOf(
        "Secret dating in showbiz",
        "Disguise hoodies & late night van rides",
        "Rival group onstage tension",
        "Coded messages on music broadcast speeches",
        "High risk of dispatch / sasaeng exposure"
      ),
      iconEmoji = "🎤",
      suggestedTone = GenreTonePreset.ANGST
    ),
    AuTemplatePreset(
      id = "royalty_historical",
      name = "Joseon Royalty AU",
      tagline = "Pangeran yang menyamar kabur dari istana x tabib rakyat yang membangkang.",
      premise = "Putra mahkota menyelinap keluar dari dinding istana berhiaskan giok untuk menyelidiki persekongkolan menteri korup. Di pasar malam Hanyang, ia terluka dan dirawat oleh tabib muda yang sinis terhadap kebangsawanan namun memiliki hati yang tulus melindungi rakyat jelata.",
      tropes = listOf(
        "Royalty incognito",
        "Social class divide",
        "Political intrigue & palace coup",
        "Midnight sword fight & moonlit roof",
        "Fierce loyalty & sacrifice"
      ),
      iconEmoji = "👑",
      suggestedTone = GenreTonePreset.ANGST
    ),
    AuTemplatePreset(
      id = "supernatural_fantasy",
      name = "Supernatural & Modern Fantasy AU",
      tagline = "Dunia malam Seoul di mana idol diam-diam mengendalikan sihir elemen.",
      premise = "Di balik agensi hiburan papan atas, terdapat faksi penjaga rahasia berdarah mistis. Karakter utama baru menyadari bahwa suara nyanyiannya memiliki resonansi sihir kuno yang diburu oleh klan bayangan, sementara bodyguard pribadinya adalah guardian serigala berkamuflase.",
      tropes = listOf(
        "Hidden magical society",
        "Elemental resonance",
        "Protector x Protected dynamic",
        "Ancient prophecy in modern Seoul",
        "High stakes urban fantasy battles"
      ),
      iconEmoji = "🔮",
      suggestedTone = GenreTonePreset.HURT_COMFORT
    )
  )
}
