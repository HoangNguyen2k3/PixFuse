package io.github.cogdanh2k3.DataGame

data class LevelData(
    val id: Int,
    val world: Int,        // thuộc vương quốc nào
    val indexInWorld: Int, // số thứ tự trong trang đó
    var unlocked: Boolean = false,
    var stars: Int = 0     // có thể lưu số sao đạt được
)
