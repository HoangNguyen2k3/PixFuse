package io.github.cogdanh2k3

import com.badlogic.gdx.graphics.Texture

data class Boss(
    val name: String,
    val hp: Int,
    val damageMultiplier: Int = 1,
    val texturePath: String = "" // đường dẫn hình boss nếu cần
) {
    var currentHP = hp
        internal set

    fun takeDamage(amount: Int) {
        currentHP -= amount
        if (currentHP < 0) currentHP = 0
    }

    fun isDefeated(): Boolean = currentHP <= 0

}

