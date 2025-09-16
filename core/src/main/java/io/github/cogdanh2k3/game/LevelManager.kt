package io.github.cogdanh2k3.game

import io.github.cogdanh2k3.DataGame.LevelData

object  LevelManager {
    val levels = mutableListOf<LevelData>()

    fun loadLevels() {
        var id = 1
        for (world in 1..5) { // 5 vương quốc
            val numLevels = 15 // mỗi world 15 level
            for (i in 1..numLevels) {
                levels.add(LevelData(id, world, i, unlocked = (id == 1)))
                id++
            }
        }
    }

    fun unlockNext(levelId: Int) {
        val next = levels.find { it.id == levelId + 1 }
        next?.unlocked = true
    }
}
