package io.github.cogdanh2k3.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import io.github.cogdanh2k3.Animation

class Board(val size: Int) {
    private val grid: Array<IntArray> = Array(size) { IntArray(size) { 0 } }

    private val tileImages = mapOf(
        2 to Texture("titles/pikachu_2.png"),
        4 to Texture("titles/charmander_4.png"),
        8 to Texture("titles/bullbasaur_8.png"),
        16 to Texture("titles/squirtle_16.png"),
        32 to Texture("titles/eevee_32.png"),
        64 to Texture("titles/jigglypuff_64.png"),
        128 to Texture("titles/snorlax_128.png"),
        256 to Texture("titles/dratini_256.png"),
        512 to Texture("titles/pidgey_512.png"),
        1024 to Texture("titles/abra_1024.png"),
        2048 to Texture("titles/venonat_2048.png"),
    )

    var x: Float = 0f
    var y: Float = 0f
    var tileSize = 128f
    var padding = 30f

    private val animations = mutableListOf<Animation>()
    private val spawnAnimations = mutableListOf<SpawnAnim>()

    data class SpawnAnim(val value: Int, val row: Int, val col: Int, var time: Float = 0f)

    fun getTile(r: Int, c: Int) = grid[r][c]
    fun setTile(r: Int, c: Int, v: Int) { grid[r][c] = v }

    fun getEmptyCells(): List<Pair<Int, Int>> {
        val res = mutableListOf<Pair<Int, Int>>()
        for (r in 0 until size) for (c in 0 until size) if (grid[r][c] == 0) res.add(r to c)
        return res
    }

    private fun gridToPos(row: Int, col: Int): Pair<Float, Float> {
        val drawX = x + col * (tileSize + padding)
        val drawY = y + (size - 1 - row) * (tileSize + padding)
        return drawX to drawY
    }

    fun addMoveAnim(value: Int, fromR: Int, fromC: Int, toR: Int, toC: Int) {
        val (sx, sy) = gridToPos(fromR, fromC)
        val (ex, ey) = gridToPos(toR, toC)
        animations.add(Animation(value, sx, sy, ex, ey, 0.15f, 0f, toR, toC))
    }

    fun addSpawnAnim(r: Int, c: Int, value: Int) {
        spawnAnimations.add(SpawnAnim(value, r, c))
    }

    fun draw(batch: SpriteBatch) {
        val dt = Gdx.graphics.deltaTime
// 1. Vẽ tile gốc nhưng bỏ qua tile đang spawn
/*        for (r in 0 until size) {
            for (c in 0 until size) {
                val v = grid[r][c]
                if (v == 0) continue

                // Nếu tile này đang có spawn animation thì skip
                if (spawnAnimations.any { it.row == r && it.col == c && it.value == v }) continue

                val (dx, dy) = gridToPos(r, c)
                tileImages[v]?.let { batch.draw(it, dx, dy, tileSize, tileSize) }
            }
        }*/
        for (r in 0 until size) {
            for (c in 0 until size) {
                val v = grid[r][c]
                if (v == 0) continue

                // skip nếu tile đang spawn
                if (spawnAnimations.any { it.row == r && it.col == c && it.value == v }) continue

                // skip nếu tile đang move đến ô này
                if (animations.any { it.value == v && it.toR == r && it.toC == c }) continue

                val (dx, dy) = gridToPos(r, c)
                tileImages[v]?.let { batch.draw(it, dx, dy, tileSize, tileSize) }
            }
        }
        // Vẽ animation move
        val it = animations.iterator()
        while (it.hasNext()) {
            val anim = it.next()
            if (anim.update(dt)) it.remove()
            val (cx, cy) = anim.getCurrentPos()
            tileImages[anim.value]?.let { batch.draw(it, cx, cy, tileSize, tileSize) }
        }

        // Vẽ animation spawn (scale-in)
        val itSpawn = spawnAnimations.iterator()
        while (itSpawn.hasNext()) {
            val s = itSpawn.next()
            s.time += dt
            val progress = (s.time / 0.25f).coerceAtMost(1f)
            val scale = 0.5f + 0.5f * progress
            val (dx, dy) = gridToPos(s.row, s.col)
            val offset = tileSize * (1 - scale) / 2
            tileImages[s.value]?.let {
                batch.draw(it, dx + offset, dy + offset, tileSize * scale, tileSize * scale)
            }
            if (progress >= 1f) itSpawn.remove()
        }
    }

    fun dispose() {
        tileImages.values.forEach { it.dispose() }
    }
}
