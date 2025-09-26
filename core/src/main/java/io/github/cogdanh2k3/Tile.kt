package io.github.cogdanh2k3.game

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Interpolation

class Tile(
    var row: Int,
    var col: Int,
    var value: Int,
    val image: Texture,
    private val tileSize: Float,
    private val padding: Float,
    private val startX: Float,
    private val startY: Float
) {
    // Vị trí thực để vẽ
    var x: Float = calcX(col)
    var y: Float = calcY(row)

    // Vị trí đích khi move
    var targetX: Float = x
    var targetY: Float = y

    var isMerging = false
    var isDead = false

    private fun calcX(col: Int): Float = startX + col * (tileSize + padding)
    private fun calcY(row: Int): Float = startY + row * (tileSize + padding)

    fun setTarget(row: Int, col: Int) {
        this.row = row
        this.col = col
        this.targetX = calcX(col)
        this.targetY = calcY(row)
    }

    fun update(delta: Float) {
        x = Interpolation.smooth.apply(x, targetX, delta * 12f)
        y = Interpolation.smooth.apply(y, targetY, delta * 12f)
    }

    fun draw(batch: SpriteBatch) {
        batch.draw(image, x, y, tileSize, tileSize)
    }
}
