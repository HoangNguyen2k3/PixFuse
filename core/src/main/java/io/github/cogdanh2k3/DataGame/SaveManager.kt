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

    fun loadGameSave(): GameSave {
        val file: FileHandle = Gdx.files.local(SAVE_FILE)

        return if (file.exists()) {
            // Đọc file lưu
            json.fromJson(GameSave::class.java, file)
        } else {
            // Tạo save mặc định
            val newSave = createDefaultGameSave()
            saveGame(newSave)
            newSave
        }
    }

    fun saveGame(gameSave: GameSave) {
        val file: FileHandle = Gdx.files.local(SAVE_FILE)
        file.writeString(json.prettyPrint(gameSave), false)
    }

    // Khởi tạo save mặc định
    private fun createDefaultGameSave(): GameSave {
        val worlds = mutableListOf<WorldData>()
        var idCounter = 1

        // Ví dụ tạo 3 world, mỗi world 10 màn
        for (worldId in 1..5) {
            val levels = mutableListOf<LevelData>()
            for (index in 1..15) {
                levels.add(
                    LevelData(
                        id = idCounter++,
                        indexInWorld = index,
                        unlocked = (worldId == 1 && index == 1), // chỉ mở màn 1
                        stars = 0,
                        target = emptyList(),
                        currentWorld = worldId
                    )
                )
            }
            worlds.add(WorldData(id = worldId, levels = levels))
        }

        return GameSave(
            currentLevel = 1,
            currentUnlockLevel = 1,
            currentUnlockWorld = 1,
            highestScore = 0,
            theme = "Pokemon",
            worlds = worlds
        )
    }
}
