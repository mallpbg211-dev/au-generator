package com.example.data.ai

import com.example.BuildConfig
import com.example.data.model.CharacterItem
import com.example.data.model.GenreTonePreset
import com.example.data.model.ProjectItem
import com.example.data.model.TonePreset
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit
import kotlin.random.Random

class GeminiWritingService {

  private val client = OkHttpClient.Builder()
    .connectTimeout(60, TimeUnit.SECONDS)
    .readTimeout(60, TimeUnit.SECONDS)
    .writeTimeout(60, TimeUnit.SECONDS)
    .build()

  private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

  val hasApiKey: Boolean
    get() {
      val key = try { BuildConfig.GEMINI_API_KEY } catch (e: Throwable) { "" }
      return key.isNotBlank() && key != "MY_GEMINI_API_KEY"
    }

  suspend fun expandPremise(
    roughPremise: String,
    project: ProjectItem,
    characters: List<CharacterItem>
  ): String = withContext(Dispatchers.IO) {
    val charSummary = if (characters.isNotEmpty()) {
      characters.joinToString(", ") { "${it.stageName} (${it.groupName} - peran AU: ${it.roleInAU.ifBlank { "Tokoh" }})" }
    } else {
      "Karakter utama idol K-pop"
    }

    val vocabText = if (project.fandomVocabulary.isNotEmpty()) {
      "Sisipkan kosakata fandom khas: ${project.fandomVocabulary.joinToString(", ")}."
    } else ""

    val styleNote = if (project.customStyleDescription.isNotBlank()) {
      "Gaya khas penulis: ${project.customStyleDescription}."
    } else ""

    val prompt = """
      Kamu adalah asisten penulis AU (Alternative Universe) fanfiction K-pop profesional berbahasa Indonesia.
      Tolong kembangkan premis kasar berikut menjadi 3 opsi arah alur cerita yang menarik, emosional, dan seru untuk dibaca:
      
      Premis Kasar: "$roughPremise"
      Setting AU: ${project.auSettingName} (${project.auSettingPremise})
      Fandom: ${project.fandom}
      Karakter: $charSummary
      Gaya Bahasa (Tone): ${project.tone.title} - ${project.tone.desc}
      Genre Tone: ${project.genreTone.title} (${project.genreTone.desc})
      Sudut Pandang: ${project.pov.title}
      $vocabText
      $styleNote

      Format Output:
      Tuliskan 3 opsi terpisah dengan judul menarik, ringkasan konflik utama, dan potensi adegan puncak (klimaks):
      [Opsi 1: ...]
      [Opsi 2: ...]
      [Opsi 3: ...]
      Gunakan bahasa Indonesia yang sesuai dengan gaya bahasa yang dipilih.
    """.trimIndent()

    callGeminiOrFallback(prompt) {
      generateLocalPremiseExpansion(roughPremise, project, characters)
    }
  }

  suspend fun generateIdeaPrompt(
    project: ProjectItem,
    characters: List<CharacterItem>
  ): String = withContext(Dispatchers.IO) {
    val charNames = if (characters.isNotEmpty()) {
      characters.joinToString(" & ") { it.stageName }
    } else {
      "Dua karakter idol"
    }

    val prompt = """
      Kamu adalah generator ide alur cerita AU K-pop kreatif untuk mengatasi Writer's Block.
      Berikan 3 skenario "What If" tak terduga dan penuh percikan emosional untuk AU ini:
      
      Fandom / Karakter: ${project.fandom} ($charNames)
      Setting AU: ${project.auSettingName}
      Genre Tone: ${project.genreTone.title} (${project.genreTone.desc})
      Tone Penulisan: ${project.tone.title}
      
      Buat 3 plot twist atau insiden mendadak yang memicu ketegangan, chemistry, atau rasa penasaran pembaca.
      Format:
      1. What If #1: [Judul] - [Penjelasan kejadian dan akibatnya]
      2. What If #2: [Judul] - [Penjelasan kejadian dan akibatnya]
      3. What If #3: [Judul] - [Penjelasan kejadian dan akibatnya]
      Gunakan bahasa Indonesia yang mengalir dan menggugah emosi pembaca.
    """.trimIndent()

    callGeminiOrFallback(prompt) {
      generateLocalIdeaPrompts(project, characters)
    }
  }

  suspend fun generateCustomWriting(
    userInstruction: String,
    project: ProjectItem,
    characters: List<CharacterItem>
  ): String = withContext(Dispatchers.IO) {
    val charSummary = characters.joinToString(", ") { "${it.stageName} (${it.roleInAU.ifBlank { "member" }})" }

    val prompt = """
      Kamu adalah asisten penulis AU (Alternative Universe) fanfiction K-pop berbahasa Indonesia.
      Tolong tuliskan draf tulisan sesuai instruksi berikut:
      
      Instruksi: "$userInstruction"
      Setting AU: ${project.auSettingName}
      Fandom: ${project.fandom}
      Karakter Terkait: $charSummary
      Gaya Bahasa: ${project.tone.title} (${project.tone.desc})
      Genre Tone: ${project.genreTone.title}
      POV: ${project.pov.title}
      Gaya Kustom: ${project.customStyleDescription}
      
      Tuliskan adegan/dialog yang mendalam, berkarakter, dan mengalir natural dalam bahasa Indonesia.
    """.trimIndent()

    callGeminiOrFallback(prompt) {
      generateLocalCustomSnippet(userInstruction, project, characters)
    }
  }

  private suspend fun callGeminiOrFallback(
    prompt: String,
    fallbackGenerator: () -> String
  ): String {
    val apiKey = try { BuildConfig.GEMINI_API_KEY } catch (e: Throwable) { "" }
    if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
      return fallbackGenerator()
    }

    return try {
      val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"
      
      val contentPart = JSONObject().apply {
        put("text", prompt)
      }
      val partsArray = JSONArray().apply {
        put(contentPart)
      }
      val contentsObject = JSONObject().apply {
        put("parts", partsArray)
      }
      val contentsArray = JSONArray().apply {
        put(contentsObject)
      }
      val requestBodyJson = JSONObject().apply {
        put("contents", contentsArray)
        put("generationConfig", JSONObject().apply {
          put("temperature", 0.75)
          put("topP", 0.95)
        })
      }

      val request = Request.Builder()
        .url(url)
        .post(requestBodyJson.toString().toRequestBody(jsonMediaType))
        .build()

      val response = client.newCall(request).execute()
      val bodyStr = response.body?.string() ?: ""

      if (response.isSuccessful && bodyStr.isNotBlank()) {
        val root = JSONObject(bodyStr)
        val candidates = root.optJSONArray("candidates")
        val firstCandidate = candidates?.optJSONObject(0)
        val content = firstCandidate?.optJSONObject("content")
        val parts = content?.optJSONArray("parts")
        val text = parts?.optJSONObject(0)?.optString("text")
        if (!text.isNullOrBlank()) {
          return text
        }
      }
      // If error or empty response, fall back seamlessly
      fallbackGenerator()
    } catch (e: Exception) {
      fallbackGenerator()
    }
  }

  private fun generateLocalPremiseExpansion(
    premise: String,
    project: ProjectItem,
    characters: List<CharacterItem>
  ): String {
    val char1 = characters.getOrNull(0)?.stageName ?: "Karakter Utama"
    val char2 = characters.getOrNull(1)?.stageName ?: "Lawan Main"
    val tone = project.genreTone

    return when (tone) {
      GenreTonePreset.FLUFF -> """
        ✨ [Opsi 1: Manis & Canggung (Slow Burn Cozy)]
        Alur berfokus pada detail-detail kecil: pertemuan rutin yang berawal dari "$premise", berkembang menjadi rutinitas saling menunggu. $char1 mulai menyadari hal-hal favorit $char2 tanpa perlu bertanya. 
        Klimaks: Hujan deras di mana payung yang tersisa hanya satu, membuka percakapan jujur pertama mereka.

        🌸 [Opsi 2: Salah Paham yang Menggemaskan]
        $char2 mengira $char1 membencinya karena sikapnya yang kaku, padahal $char1 hanya terlalu gugup setiap berada di dekatnya. Dibantu oleh teman satu dorm, situasi-situasi kocak tercipta saat keduanya dipaksa bekerja sama.
        Klimaks: Sebuah catatan kecil yang terselip membongkar perasaan yang sebenarnya.

        ☕ [Opsi 3: Pertemuan Rahasia Malam Hari]
        Karena kesibukan masing-masing di dunia ${project.auSettingName}, waktu satu-satunya mereka bisa bertemu adalah larut malam. Saling berbagi cerita rahasia yang tak pernah mereka ceritakan ke orang lain.
        Klimaks: Pengakuan tak terduga saat keduanya menyaksikan matahari terbit bersama.
      """.trimIndent()

      GenreTonePreset.ANGST -> """
        💔 [Opsi 1: Waktu yang Salah & Rahasia Terkubur]
        Keduanya terikat pada takdir yang berlawanan di ${project.auSettingName}. $char1 menyimpan rahasia besar yang dapat menghancurkan karier atau posisi $char2.
        Klimaks: Konfrontasi di tengah hujan malam saat kenyataan pahit terkuak, meninggalkan keduanya dalam pilihan antara ambisi atau perasaan.

        🌧️ [Opsi 2: Cinta Tak Berbalas yang Terlambat Disadari]
        $char1 selalu ada di sisi $char2, namun $char2 baru menyadari betapa berartinya kehadiran itu tepat saat $char1 memutuskan untuk menyerah dan pergi menjauh.
        Klimaks: Surat perpisahan yang ditemukan di loker dorm lama, memicu penyesalan mendalam.

        🥀 [Opsi 3: Pengorbanan Demi Masa Depan]
        Demi melindungi impian $char2 dari sorotan publik dan tuntutan agensi, $char1 memilih mundur dan menjadi sosok yang disalahkan.
        Klimaks: Tatapan mata penuh luka di tengah keramaian acara penghargaan di mana mereka harus bersikap seperti orang asing.
      """.trimIndent()

      GenreTonePreset.CRACK -> """
        💥 [Opsi 1: Kekacauan Beruntun yang Berujung Viral]
        Bermula dari "$premise", terjadi serentetan kesialan berantai: salah kirim voice note ke grup fandom, koper tertukar, hingga terpaksa menyamar menggunakan hoodie terbalik.
        Klimaks: Insiden konyol di siaran langsung yang membuat penggemar heboh menciptakan teori konspirasi lucu.

        🍕 [Opsi 2: Perang Dingin Makanan & Loker]
        $char1 dan $char2 memulai aksi balas dendam receh saling menyembunyikan camilan favorit dan menempel memo sarkasme di kulkas bersama.
        Klimaks: Keduanya tak sengaja terkunci bareng di ruang latihan malam-malam tanpa sinyal HP dan hanya bersenjatakan sebotol susu pisang.

        🎉 [Opsi 3: Misi Penyamaran Paling Gagal Sedunia]
        $char1 mencoba menjadi sosok yang misterius dan keren, tapi setiap rencana berantakan karena kepolosan $char2 yang selalu menggagalkan taktiknya.
      """.trimIndent()

      GenreTonePreset.HURT_COMFORT -> """
        🩹 [Opsi 1: Tempat Berlindung dari Tekanan Dunia]
        Di tengah ekspektasi tinggi dan kelelahan mental, $char1 menemukan bahwa hanya di hadapan $char2 ia tidak perlu berpura-pura sempurna.
        Klimaks: Malam di mana $char1 akhirnya menangis dan $char2 mendengarkan tanpa menghakimi, menyeduhkan cokelat hangat.

        🌿 [Opsi 2: Belajar Percaya Kembali]
        Setelah trauma dikhianati di masa lalu, $char2 bersikap sangat defensif. $char1 dengan sabar membuktikan bahwa tidak semua orang akan meninggalkannya.
        Klimaks: Momen krisis saat $char1 memilih tetap berdiri di samping $char2 meski harus menanggung risiko besar.

        🕯️ [Opsi 3: Rekonsiliasi Luka Lama]
        Pertemuan kembali setelah bertahun-tahun berpisah dingin. Keduanya perlahan membuka luka lama dan saling memaafkan kesalahan masa muda.
      """.trimIndent()
    }
  }

  private fun generateLocalIdeaPrompts(
    project: ProjectItem,
    characters: List<CharacterItem>
  ): String {
    val char1 = characters.getOrNull(0)?.stageName ?: "Tokoh A"
    val char2 = characters.getOrNull(1)?.stageName ?: "Tokoh B"
    val setting = project.auSettingName

    return """
      💡 [Anti Writer's Block - 3 Gagasan "What If" Segar]

      1. 🎲 What If #1: "Salah Identitas di Chat Pribadi"
      Bagaimana jika $char1 mengira nomor kontak $char2 adalah nomor kurir langganan, dan mengirimkan curhatan emosional serta foto memalukan tentang bos/senior mereka? $char2 yang berniat iseng membalasnya, malah jadi ketagihan mendengarkan curhatan itu setiap malam.

      2. ⚡ What If #2: "Terjebak di Lift Saat Mati Lampu Total"
      Di malam badai besar di gedung agensi/apartemen, keduanya terjebak di lift yang macet selama 3 jam. Dalam kegelapan dan baterai HP yang tersisa 4%, satu rahasia besar yang selama ini ditutupi akhirnya terucap tanpa sengaja.

      3. 🎭 What If #3: "Pura-Pura Akur di Hadapan Kamera"
      Karena suatu kesepakatan bisnis atau tugas kolaborasi, $char1 dan $char2 yang sebenarnya saling menghindari dipaksa menunjukkan chemistry manis di depan publik. Ironisnya, sandiwara itu perlahan terasa terlalu nyata hingga batas antara akting dan perasaan asli mulai kabur.
    """.trimIndent()
  }

  private fun generateLocalCustomSnippet(
    instruction: String,
    project: ProjectItem,
    characters: List<CharacterItem>
  ): String {
    val char1 = characters.getOrNull(0)?.stageName ?: "Dia"
    val char2 = characters.getOrNull(1)?.stageName ?: "Lawan Bicara"

    return """
      📝 [Draf Hasil Penulisan AU: ${project.title}]
      Tone: ${project.tone.title} | Genre: ${project.genreTone.title}

      Aroma kopi dan rintik hujan yang menghantam kaca jendela membuat suasana malam terasa lebih senyap dari biasanya. $char1 menarik ujung tudung hoodienya lebih rendah, berusaha menyembunyikan wajah lelahnya dari pandangan orang-orang yang berlalu-lalang.

      "Masih di sini?" sebuah suara yang teramat familiar memecah lamunannya.

      $char2 meletakkan sebuah cangkir keramik hangat di hadapan $char1. Asap tipis mengepul, membawa aroma manis vanila yang sengaja dipesan tanpa gula berlebih.

      "Kupikir kamu udah balik ke dorm sejak jam sembilan tadi," gumam $char1 tanpa mengangkat pandangan, jemarinya perlahan melingkari cangkir yang hangat itu.

      $char2 tersenyum tipis—senyuman kecil yang jarang sekali ia tunjukkan di depan sorotan kamera. "Gimana bisa balik kalau tahu ada orang keras kepala yang masih sendirian di sini sambil mikirin hal-hal yang nggak perlu?"

      Keheningan kembali menyelimuti mereka berdua, namun kali ini bukan keheningan canggung. Ada pemahaman tak terucap yang mengalir di antara detak jarum jam dinding dan suara hujan di luar.
    """.trimIndent()
  }
}
