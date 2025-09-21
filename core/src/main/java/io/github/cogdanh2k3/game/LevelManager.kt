package io.github.cogdanh2k3.game

import io.github.cogdanh2k3.DataGame.LevelData
import io.github.cogdanh2k3.DataGame.SaveManager
import io.github.cogdanh2k3.DataGame.WorldData

object LevelManager {
    val levels = mutableListOf<LevelData>()
    val worlds = mutableListOf<WorldData>()
    fun loadLevels() {
        val currentSave = SaveManager.loadGameSave()
        levels.clear()
        worlds.clear()
        // Flatten tất cả level từ worlds -> levels
        for (world in currentSave.worlds) {
            for (level in world.levels) {
                levels.add(level)
            }
            worlds.add(world)
        }
    }

    fun unlockNext(levelId: Int) {
        // Tìm index của level hiện tại trong danh sách levels (flatten toàn bộ)
        val currentIndex = levels.indexOfFirst { it.id == levelId }
        if (currentIndex != -1 && currentIndex + 1 < levels.size) {
            val next = levels[currentIndex + 1]
            next.unlocked = true

            // Cập nhật trong save
            val gameSave = SaveManager.loadGameSave()
            gameSave.currentUnlockWorld = next.currentWorld
            gameSave.currentUnlockLevel = next.indexInWorld
            val levelInSave = gameSave.worlds
                .flatMap { it.levels }
                .find { it.id == next.id }
            levelInSave?.unlocked = true
            SaveManager.saveGame(gameSave)
        }
    }
}
