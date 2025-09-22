package io.github.cogdanh2k3.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import io.github.cogdanh2k3.Anim.ExplosionMerge
import io.github.cogdanh2k3.Animation
import io.github.cogdanh2k3.utils.SpriteSheetAnimation
import kotlin.math.sin

class Board(val size: Int) {
    private val grid: Array<IntArray> = Array(size) { IntArray(size) { 0 } }
    var tileImages = mapOf(
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

    // ---- Explosion animation ----
    private val explosionSheet = SpriteSheetAnimation(
        "effects/explosion.png",
        rows = 1, cols = 18,
        frameDuration = 0.05f,
        playMode = com.badlogic.gdx.graphics.g2d.Animation.PlayMode.NORMAL
    )
    data class Explosion(val x: Float, val y: Float, var time: Float = 0f)
    private val explosions = mutableListOf<Explosion>()

    fun addExplosion(row: Int, col: Int) {
        val (dx, dy) = gridToPos(row, col)
        explosions.add(Explosion(dx, dy))
    }

    // ---- Merge animation ----
    data class MergeAnim(val row: Int, val col: Int, val value: Int, var time: Float = 0f)
    private val mergeAnimations = mutableListOf<MergeAnim>()
    fun addMergeAnim(row: Int, col: Int, value: Int) {
        mergeAnimations.add(MergeAnim(row, col, value, 0f))
    }

    // Texture trắng 1x1 để fill màu
    private val whiteTexture: Texture

    var x: Float = 0f
    var y: Float = 0f
    var tileSize = 128f
    var padding = 30f
    private val animations = mutableListOf<Animation>()
    private val spawnAnimations = mutableListOf<SpawnAnim>()

    data class SpawnAnim(val value: Int, val row: Int, val col: Int, var time: Float = 0f)

    init {
        val pixmap = Pixmap(1, 1, Pixmap.Format.RGBA8888)
        pixmap.setColor(Color.WHITE)
        pixmap.fill()
        whiteTexture = Texture(pixmap)
        pixmap.dispose()
        val LEVEL_1_WALLS = arrayOf(
            Pair(1, 1),
            Pair(2, 2)
        )
        for ((x, y) in LEVEL_1_WALLS) {
            grid[x][y] = TILE_WALL
        }
    }

    fun getTile(r: Int, c: Int) = grid[r][c]
    fun setTile(r: Int, c: Int, v: Int) { grid[r][c] = v }

/*    fun getEmptyCells(): List<Pair<Int, Int>> {
        val res = mutableListOf<Pair<Int, Int>>()
        for (r in 0 until size) for (c in 0 until size) if (grid[r][c] == 0) res.add(r to c)
        return res
    }*/
    fun getEmptyCells(): List<Pair<Int, Int>> {
        val result = mutableListOf<Pair<Int, Int>>()
        for (r in 0 until size) {
            for (c in 0 until size) {
                if (grid[r][c] == 0) { // chỉ lấy ô trống, bỏ qua tường (-1)
                    result.add(Pair(r, c))
                }
            }
        }
        return result
    }
    private fun gridToPos(row: Int, col: Int): Pair<Float, Float> {
        val drawX = x + col * (tileSize + padding)
        val drawY = y + (size - 1 - row) * (tileSize + padding)
        return drawX to drawY
    }

    fun addMoveAnim(value: Int, fromR: Int, fromC: Int, toR: Int, toC: Int) {
        val (sx, sy) = gridToPos(fromR, fromC)
        val (ex, ey) = gridToPos(toR, toC)

        val distance = kotlin.math.abs(fromR - toR) + kotlin.math.abs(fromC - toC)
        val baseSpeed = 0.08f
        val duration = distance * baseSpeed

        animations.add(Animation(value, sx, sy, ex, ey, duration, 0f, toR, toC))
    }

    fun addSpawnAnim(r: Int, c: Int, value: Int) {
        spawnAnimations.add(SpawnAnim(value, r, c))
    }

    fun draw(batch: SpriteBatch) {
        val dt = Gdx.graphics.deltaTime

        // ---- 0. Vẽ viền board ----
        val boardWidth = size * tileSize + (size - 1) * padding
        val boardHeight = size * tileSize + (size - 1) * padding
        val borderThickness = 20f
        val outerWidth = boardWidth + borderThickness * 2
        val outerHeight = boardHeight + borderThickness * 2

        batch.color = Color(0.6f, 0.6f, 0.6f, 1f)
        batch.draw(whiteTexture, x - borderThickness, y - borderThickness, outerWidth, outerHeight)
        batch.color = Color(0.8f, 0.8f, 0.8f, 1f)
        batch.draw(whiteTexture, x, y, boardWidth, boardHeight)

        // ---- 1. Vẽ các ô nền (trống + tường) ----
        for (r in 0 until size) {
            for (c in 0 until size) {
                val (dx, dy) = gridToPos(r, c)
                val v = grid[r][c]

                if (v == TILE_WALL) {
                    // vẽ tường màu xám đậm
                    batch.color = Color.DARK_GRAY
                    batch.draw(whiteTexture, dx, dy, tileSize, tileSize)
                } else {
                    // ô trống bình thường
                    batch.color = Color(0.9f, 0.9f, 0.9f, 1f)
                    batch.draw(whiteTexture, dx, dy, tileSize, tileSize)
                }
            }
        }
        batch.color = Color.WHITE

        // ---- 2. Vẽ tile tĩnh ----
        for (r in 0 until size) {
            for (c in 0 until size) {
                val v = grid[r][c]
                if (v <= 0) continue // bỏ qua ô trống và tường

                if (spawnAnimations.any { it.row == r && it.col == c }) continue
                if (animations.any { it.toR == r && it.toC == c }) continue

                val (dx, dy) = gridToPos(r, c)
                var scale = 1f

                // merge animation
                val mergeAnim = mergeAnimations.find { it.row == r && it.col == c && it.value == v }
                if (mergeAnim != null) {
                    mergeAnim.time += dt
                    val t = (mergeAnim.time / 0.25f).coerceAtMost(1f)
                    scale = 1.0f + 0.2f * sin(t * Math.PI).toFloat()
                    if (t >= 1f) mergeAnimations.remove(mergeAnim)
                }

                val offset = tileSize * (1 - scale) / 2
                tileImages[v]?.let { batch.draw(it, dx + offset, dy + offset, tileSize * scale, tileSize * scale) }
            }
        }

        // ---- 3. Vẽ animation move ----
        val it = animations.iterator()
        while (it.hasNext()) {
            val anim = it.next()
            if (anim.update(dt)) it.remove()
            val (cx, cy) = anim.getCurrentPos()
            tileImages[anim.value]?.let { batch.draw(it, cx, cy, tileSize, tileSize) }
        }

        // ---- 4. Vẽ animation spawn ----
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

        // ---- 5. Vẽ explosion ----
        val itExpl = explosions.iterator()
        while (itExpl.hasNext()) {
            val e = itExpl.next()
            e.time += dt
            val frame = explosionSheet.getFrameAt(e.time, looping = false)
            val size = tileSize * 1.2f
            val offset = (size - tileSize) / 2

            batch.draw(frame, e.x - offset, e.y - offset, size, size)
            if (explosionSheet.isAnimationFinished(e.time)) {
                itExpl.remove()
            }
        }
    }

    fun dispose() {
        tileImages.values.forEach { it.dispose() }
        explosionSheet.dispose()
        whiteTexture.dispose()
    }
}
