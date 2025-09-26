package io.github.cogdanh2k3.Anim

import io.github.cogdanh2k3.utils.SpriteSheetAnimation

data class ExplosionMerge(
    val anim: SpriteSheetAnimation,
    val x: Float,
    val y: Float
) {
    var time = 0f
}
