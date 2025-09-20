package io.github.cogdanh2k3.DataGame

data class GameSave(
    var level: Int = 1,
    var highest_score: Int = 0,
    var theme: String = "Pokemon",
    var levels: MutableList<LevelData> = mutableListOf() // danh sách level
)
