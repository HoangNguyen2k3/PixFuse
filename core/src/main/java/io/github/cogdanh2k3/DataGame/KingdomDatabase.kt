package io.github.cogdanh2k3.DataGame

object KingdomDatabase {
    val kingdoms = listOf(
        KingdomData("Volcano Realm", "Pikachu Hắc Hóa", "Islands/fireisland.png", "Boss/pikaBoss.png", unlocked = true),
        KingdomData("Cemetery Zone", "Zombie PVZ", "Islands/pvzisland.png", "Boss/boss_pvz.png"),
        KingdomData("Robo Bay", "Mèo Máy Hắc Hóa", "Islands/iceisland.png", "Boss/boss_dora.png"),
        KingdomData("Frieza Empire", "Frieza", "Islands/dbisland.png", "Boss/boss_frize.png"),
        KingdomData("Dark Meme Sea", "Meme Cat Hắc Hóa", "Islands/catisland.png", "Boss/boss_memecat.png")
    )
}
