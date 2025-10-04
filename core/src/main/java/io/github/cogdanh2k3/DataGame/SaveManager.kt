package io.github.cogdanh2k3.DataGame

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.utils.Json
import com.badlogic.gdx.utils.JsonWriter

object SaveManager {
    private const val SAVE_FILE = "gameSave.json"
    private val json = Json().apply {
        setOutputType(JsonWriter.OutputType.json)
    }

    lateinit var gameSave: GameSave
        public set

    init {
        gameSave = loadGameSave()
    }

    fun loadGameSave(): GameSave {
        val file = Gdx.files.local(SAVE_FILE)
        Gdx.app.log("SaveManager", "📂 Bắt đầu load file save...")
        Gdx.app.log("SaveManager", "📁 Đường dẫn file: ${file.file().absolutePath}")
        Gdx.app.log("SaveManager", "📦 File type: ${file.type()}")

        if (file.exists()) {
            try {
                val raw = file.readString()
                Gdx.app.log("SaveManager", "📄 Nội dung JSON:\n$raw")

                val loaded = json.fromJson(GameSave::class.java, raw)

                // ✅ Sao chép và đảm bảo danh sách mutable
                gameSave = loaded.copy(
                    list_high_score = loaded.list_high_score?.toMutableList() ?: mutableListOf(),
                    worlds = loaded.worlds?.toMutableList() ?: mutableListOf()
                )

                // ✅ Chuyển các world.levels thành mutable list (tránh lỗi sau)

                Gdx.app.log("SaveManager", "✅ Load thành công: HighScore = ${gameSave.list_high_score}")
                return gameSave

            } catch (e: Exception) {
                Gdx.app.error("SaveManager", "❌ Parse lỗi, tạo file mới", e)
            }
        } else {
            Gdx.app.log("SaveManager", "⚠️ File không tồn tại, tạo mới.")
        }

        // 🔥 Nếu lỗi hoặc file không có — tạo save mặc định
        val newSave = createDefaultGameSave()
        gameSave = newSave
        saveGame()
        Gdx.app.log("SaveManager", "🆕 Tạo file save mặc định thành công.")
        return newSave
    }

    fun saveGame() {
        try {
            val file = Gdx.files.local(SAVE_FILE)
            val jsonText = json.prettyPrint(gameSave)
            file.writeString(jsonText, false)
            Gdx.app.log("SaveManager", "💾 Đã lưu game thành công. (${file.file().absolutePath})")
        } catch (e: Exception) {
            Gdx.app.error("SaveManager", "❌ Lỗi khi lưu game", e)
        }
    }

    private fun createDefaultGameSave(): GameSave {
        Gdx.app.log("SaveManager", "📦 Đang tạo GameSave mặc định...")

        val worlds = mutableListOf<WorldData>()
        var idCounter = 1

        for (worldId in 1..5) {
            val levels = mutableListOf<LevelData>()
            for (index in 1..15) {
                levels.add(
                    LevelData(
                        id = idCounter++,
                        indexInWorld = index,
                        unlocked = (worldId == 1 && index == 1),
                        stars = 0,
                        target = mutableListOf(),
                        currentWorld = worldId,
                        wallData = mutableListOf()
                    )
                )
            }
            worlds.add(WorldData(id = worldId, levels = levels))
        }

        val defaultSave = GameSave(
            currentLevel = 1,
            currentUnlockLevel = 1,
            currentUnlockWorld = 1,
            highestScore = 0,
            theme = "Pokemon",
            worlds = worlds,
            list_high_score = mutableListOf(),
            bool_music = true,
            bool_sound = true,
            bool_vibration = true
        )

        Gdx.app.log("SaveManager", "✅ GameSave mặc định đã được tạo.")
        return defaultSave
    }
}
