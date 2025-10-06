package io.github.cogdanh2k3.DataGame

// Save tổng
data class GameSave(
    var currentLevel: Int = 1,
    var currentUnlockLevel: Int = 1,
    var currentUnlockWorld: Int = 1,
    var highestScore: Int = 0,
    var theme: String = "Pokemon",
    var worlds: MutableList<WorldData> = mutableListOf(),
    var list_high_score: MutableList<Int> = mutableListOf(),
    var bool_music: Boolean = true,
    var bool_sound: Boolean = true,
    var bool_vibration: Boolean = true,
    var int_levelBoss : Int = 0
) {
    fun addScore(score: Int) {
        if (!list_high_score.contains(score)) {
            list_high_score.add(score)
            list_high_score.sortDescending()
            SaveManager.saveGame()
        }
    }

}
