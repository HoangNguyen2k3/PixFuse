package io.github.cogdanh2k3.DataGame

import com.badlogic.gdx.graphics.Texture
data class Theme(
    val name: String,
    val images: Map<Int, Texture>
)
class DataGame {
    private val img_pokemonTheme = mapOf(
        2 to Texture("titles/Pokemon/pikachu_2.png"),
        4 to Texture("titles/Pokemon/charmander_4.png"),
        8 to Texture("titles/Pokemon/bullbasaur_8.png"),
        16 to Texture("titles/Pokemon/squirtle_16.png"),
        32 to Texture("titles/Pokemon/eevee_32.png"),
        64 to Texture("titles/Pokemon/jigglypuff_64.png"),
        128 to Texture("titles/Pokemon/snorlax_128.png"),
        256 to Texture("titles/Pokemon/dratini_256.png"),
        512 to Texture("titles/Pokemon/pidgey_512.png"),
        1024 to Texture("titles/Pokemon/abra_1024.png"),
        2048 to Texture("titles/Pokemon/venonat_2048.png"),
    )
    private val img_plantTheme = mapOf(
        2 to Texture("titles/Plant/Plant_2.png"),
        4 to Texture("titles/Plant/Plant_4.png"),
        8 to Texture("titles/Plant/Plant_8.png"),
        16 to Texture("titles/Plant/Plant_16.png"),
        32 to Texture("titles/Plant/Plant_32.png"),
        64 to Texture("titles/Plant/Plant_64.png"),
        128 to Texture("titles/Plant/Plant_128.png"),
        256 to Texture("titles/Plant/Plant_256.png"),
        512 to Texture("titles/Plant/Plant_512.png"),
        1024 to Texture("titles/Plant/Plant_1024.png"),
        2048 to Texture("titles/Plant/Plant_2048.png"),
    )
    private val img_DragonBallTheme = mapOf(
        2 to Texture("titles/DragonBall/1star.png"),
        4 to Texture("titles/DragonBall/2star.png"),
        8 to Texture("titles/DragonBall/3star.png"),
        16 to Texture("titles/DragonBall/4star.png"),
        32 to Texture("titles/DragonBall/5star.png"),
        64 to Texture("titles/DragonBall/6star.png"),
        128 to Texture("titles/DragonBall/7star.png"),
        256 to Texture("titles/DragonBall/shenlong.png"),
        512 to Texture("titles/DragonBall/goku.png"),
        1024 to Texture("titles/DragonBall/krillin.png"),
        2048 to Texture("titles/DragonBall/tien.png"),
    )
    private val img_DoraemonTheme = mapOf(
        2 to Texture("titles/Doraemon/bell.png"),
        4 to Texture("titles/Doraemon/doraemon.png"),
        8 to Texture("titles/Doraemon/dorami.png"),
        16 to Texture("titles/Doraemon/nobita.png"),
        32 to Texture("titles/Doraemon/shizuka.png"),
        64 to Texture("titles/Doraemon/suneo.png"),
        128 to Texture("titles/Doraemon/takeshi.png"),
        256 to Texture("titles/Doraemon/mother_nobita.png"),
        512 to Texture("titles/Doraemon/nobisuke_nobi_father.png"),
        1024 to Texture("titles/Doraemon/teacher.png"),
        2048 to Texture("titles/Doraemon/dekisugi.png"),
    )
    private val img_memeCatTheme = mapOf(
        2 to Texture("titles/MemeCat/angry cat.png"),
        4 to Texture("titles/MemeCat/crycat.png"),
        8 to Texture("titles/MemeCat/crying banana cat.png"),
        16 to Texture("titles/MemeCat/dog.png"),
        32 to Texture("titles/MemeCat/eee.png"),
        64 to Texture("titles/MemeCat/fat cat.png"),
        128 to Texture("titles/MemeCat/hahacat.png"),
        256 to Texture("titles/MemeCat/happyhappy.png"),
        512 to Texture("titles/MemeCat/mrfresh.png"),
        1024 to Texture("titles/MemeCat/oe.png"),
        2048 to Texture("titles/MemeCat/uia.png"),
    )
    // List theme có kèm name
    val themes = listOf(
        Theme("Pokemon", img_pokemonTheme),
        Theme("Plant", img_plantTheme),
        Theme("Doraemon",img_DoraemonTheme),
        Theme("MemeCat",img_memeCatTheme),
        Theme("DragonBall",img_DragonBallTheme),
    )
}
class DataTargetLevel{
    private val targetlevel = mapOf(
        1 to listOf(32),
        2 to listOf(64),
        3 to  listOf(32,64),
        4 to  listOf(32,128),
        5 to  listOf(256),
        6 to  listOf(512),
        7 to   listOf(128,512),
        8 to   listOf(1024),
        9 to  listOf(256,512,1024),
        10 to  listOf(256,256,512,1024),
        11 to  listOf(2048),
        12 to  listOf(2048),
        13 to  listOf(2048),
        14 to  listOf(2048),
        15 to  listOf(2048),
        )
}
