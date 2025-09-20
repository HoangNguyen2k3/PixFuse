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
            // Đã có file → load
            json.fromJson(GameSave::class.java, file)
        } else {
            // Chưa có file → tạo mới
            val newSave = createDefaultGameSave()
            saveGame(newSave)
            newSave
        }
    }

    fun saveGame(gameSave: GameSave) {
        val file: FileHandle = Gdx.files.local(SAVE_FILE)
        file.writeString(json.prettyPrint(gameSave), false)
    }

    private fun createDefaultGameSave(): GameSave {
        val levels = mutableListOf<LevelData>()

        // Ví dụ khởi tạo 30 màn, chia 3 world
        for (i in 1..30) {
            levels.add(
                LevelData(
                    id = i,
                    world = (i - 1) / 10 + 1,   // mỗi world có 10 màn
                    indexInWorld = (i - 1) % 10 + 1,
                    unlocked = (i == 1),        // mở màn đầu tiên
                    stars = 0
                )
            )
        }

        return GameSave(
            level = 1,
            highest_score = 0,
            theme = "Pokemon",
            levels = levels
        )
    }
}
