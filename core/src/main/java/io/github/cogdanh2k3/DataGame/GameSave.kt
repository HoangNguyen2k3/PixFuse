package io.github.cogdanh2k3.DataGame

// Save tổng
data class GameSave(
    var currentLevel: Int = 1,
    var currentUnlockLevel: Int = 1,
    var currentUnlockWorld: Int = 1,// màn hiện tại
    var highestScore: Int = 0,
    var theme: String = "Pokemon",
    var worlds: MutableList<WorldData> = mutableListOf()
)
