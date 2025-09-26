package io.github.cogdanh2k3.DataGame

import com.badlogic.gdx.physics.box2d.World

// Mỗi màn chơi
data class LevelData(
    val id: Int = 1,                 // id duy nhất
    val indexInWorld: Int = 1,       // thứ tự trong world
    var unlocked: Boolean = false,
    var stars: Int = 0,
    var target: List<Int> = emptyList(),
    var currentWorld: Int = 1,
    var sizeBoard: Int = 4,
    var wallData: List<Pair<Int, Int>> = emptyList<Pair<Int, Int>>()
)

// Một World gồm nhiều màn
data class WorldData(
    val id: Int = 1,                         // số thứ tự world
    val levels: MutableList<LevelData> = mutableListOf(),
    val target_level: Map<Int, List<Int>> = mapOf(
        1 to listOf(16),
        2 to listOf(32),
        3 to listOf(16, 32),
        4 to listOf(32, 32),
        5 to listOf(32,64),
        6 to listOf(128),
        7 to listOf(128, 128),
        8 to listOf(256),
        9 to listOf(256, 256),
        10 to listOf(256, 512),
        11 to listOf(16,128,512),
        12 to listOf(256,512),
        13 to listOf(512,512),
        14 to listOf(64,64,1024),
        15 to listOf(2048)
    ),
/*    val target_level: Map<Int, List<Int>> = mapOf(
        1 to listOf(8),
        2 to listOf(8),
        3 to listOf(8),
        4 to listOf(8),
        5 to listOf(8),
        6 to listOf(8),
        7 to listOf(8),
        8 to listOf(8),
        9 to listOf(8),
        10 to listOf(8),
        11 to listOf(8),
        12 to listOf(8),
        13 to listOf(8),
        14 to listOf(8),
        15 to listOf(8)
    ),*/
    val sizeBoard: Map<Int, Int> = mapOf(
        1 to 4,
        2 to 5,
        3 to 4,
        4 to 5,
        5 to 5,
        6 to 5,
        7 to 4,
        8 to 4,
        9 to 4,
        10 to 5,
        11 to 5,
        12 to 5,
        13 to 5,
        14 to 6,
        15 to 6
    ),
    val wallData: List<List<Pair<Int,Int>>> = listOf(
        listOf(),
        listOf(),
        listOf(Pair(2,2)),
        listOf(Pair(1,1),Pair(2,2)),
        listOf(Pair(1,1),Pair(2,2)),
        listOf(Pair(1,1),Pair(2,2)),
        listOf(Pair(1,1),Pair(2,2)),
        listOf(Pair(1,1),Pair(2,2)),
        listOf(Pair(1,1),Pair(2,2)),
        listOf(Pair(1,1),Pair(2,2)),
        listOf(Pair(1,1),Pair(2,2)),
        listOf(Pair(1,1),Pair(2,2)),
        listOf(Pair(1,1),Pair(2,2)),
        listOf(Pair(1,1),Pair(2,2)),
        listOf(Pair(1,1),Pair(2,2)),
    )
)
