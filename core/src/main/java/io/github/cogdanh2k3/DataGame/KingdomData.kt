package io.github.cogdanh2k3.DataGame

data class KingdomData(
    val name: String,
    val bossName: String,
    val islandImage: String,
    val bossImage: String,
    var unlocked: Boolean = false
)
