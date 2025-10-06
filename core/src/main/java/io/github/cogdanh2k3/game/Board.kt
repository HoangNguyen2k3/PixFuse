package io.github.cogdanh2k3.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.GlyphLayout
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import io.github.cogdanh2k3.Animation
import io.github.cogdanh2k3.utils.SpriteSheetAnimation
import kotlin.math.sin
import com.badlogic.gdx.graphics.g2d.BitmapFont
import io.github.cogdanh2k3.DataGame.IntPair
import io.github.cogdanh2k3.audio.SoundId
import io.github.cogdanh2k3.audio.SoundManager
import io.github.cogdanh2k3.utils.FontUtils

class Board(val size: Int) {
    private val grid: Array<Array<Tile>> = Array(size) { Array(size) { Tile() } }
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
    val txt_font= FontUtils.loadCustomFont(30, Color.BLACK)
    private val iceTileTextures = Texture("titles/ice_tile.png")
    private val bombTileTextures = Texture("titles/bomb.png")
    private val thunderTileTexture = Texture("titles/thunder_icon.png")
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
    //------------------------ Thunder ---------------------------
    private val explosionThunderSheet = SpriteSheetAnimation(
        "effects/thunder.png",
        rows = 1, cols = 5,
        frameDuration = 0.1f,
        playMode = com.badlogic.gdx.graphics.g2d.Animation.PlayMode.NORMAL
    )
    data class ExplosionThunder(val x: Float, val y: Float, var time: Float = 0f)
    private val explosionsThunder = mutableListOf<ExplosionThunder>()

    fun addExplosionThunder(row: Int, col: Int) {
        val (dx, dy) = gridToPos(row, col)
        explosionsThunder.add(ExplosionThunder(dx, dy))
    }
    //----------Explosion Boom-----------------
    private val explosionBoomSheet = SpriteSheetAnimation(
        "effects/explosionboom.png",
        rows = 8, cols = 10,
        frameDuration = 0.01f,
        playMode = com.badlogic.gdx.graphics.g2d.Animation.PlayMode.NORMAL
    )
    data class ExplosionBoom(val x: Float, val y: Float, var time: Float = 0f)
    private val explosionsBoom = mutableListOf<ExplosionBoom>()

    fun addExplosionBoom(row: Int, col: Int) {
        val (dx, dy) = gridToPos(row, col)
        explosionsBoom.add(ExplosionBoom(dx, dy))
    }
    //ExplosionBoomBoos
    private val explosionBoombossSheet = SpriteSheetAnimation(
        "effects/explosionboom.png",
        rows = 8, cols = 10,
        frameDuration = 0.01f,
        playMode = com.badlogic.gdx.graphics.g2d.Animation.PlayMode.NORMAL
    )
    data class ExplosionBoomboos(val x: Float, val y: Float, var time: Float = 0f)
    private val explosionsBoomboss = mutableListOf<ExplosionBoomboos>()

    // 👉 Thêm hàm add theo toạ độ thật
    fun addExplosionBoomBoss(x: Float, y: Float) {
        explosionsBoomboss.add(ExplosionBoomboos(x, y))
    }
    //-------------Thaw Ice-------------------
    private val explosionThawSheet = SpriteSheetAnimation(
        "effects/ice_broken.png",
        rows = 4, cols = 4,
        frameDuration = 0.05f,
        playMode = com.badlogic.gdx.graphics.g2d.Animation.PlayMode.NORMAL
    )
    data class ExplosionIceThaw(val x: Float, val y: Float, var time: Float = 0f)
    private val explosionsIceThaw = mutableListOf<ExplosionIceThaw>()

    fun addExplosionIceThaw(row: Int, col: Int) {
        val (dx, dy) = gridToPos(row, col)
        explosionsIceThaw.add(ExplosionIceThaw(dx, dy))
    }
    // ---- Merge animation ----
    data class MergeAnim(val row: Int, val col: Int, val value: Int, var time: Float = 0f)
    private val mergeAnimations = mutableListOf<MergeAnim>()
    fun addMergeAnim(row: Int, col: Int, value: Int) {
        mergeAnimations.add(MergeAnim(row, col, value, 0f))
    }


    // Texture trắng 1x1 để fill màu
    private val whiteTexture: Texture
    private val labelFont = BitmapFont().apply {
        data.setScale(3f)
        color = Color.WHITE
    }
    var x: Float = 0f
    var y: Float = 0f
    var tileSize = 128f
    var padding = 30f
    private val animations = mutableListOf<Animation>()
    private val spawnAnimations = mutableListOf<SpawnAnim>()
    public var LEVEL_WALLS = listOf(
        IntPair(1, 1),
        IntPair(2, 2)
    )

    data class SpawnAnim(val value: Int, val row: Int, val col: Int, var time: Float = 0f)

    init {
        val pixmap = Pixmap(1, 1, Pixmap.Format.RGBA8888)
        pixmap.setColor(Color.WHITE)
        pixmap.fill()
        whiteTexture = Texture(pixmap)
        pixmap.dispose()
    //InitGrid()

    }
    fun InitGrid() {
        for (pair in LEVEL_WALLS) {
            grid[pair.first][pair.second].value = TILE_WALL
        }
    }
    fun spawnBoomTiles( temp: Int){
        var temp1 : Int = temp
        for (r in 0 until size) {
            for (c in 0 until size) {
                if(temp1<=0){
                    return
                }
                val t = grid[r][c]
                // Ô trống chỉ khi value == 0 và không có hiệu ứng đặc biệt
                if (t.value == 0 && t.frozen == 0 && !t.isBoom && !t.isThunder) {
                    temp1--
                    grid[r][c].value = 4
                    grid[r][c].isBoom = true
                    grid[r][c].boomCounter = 3
                }
            }
        }
    }
    //-------------------BOOSTER-----------------------
    fun hasWall(): Boolean {
        for (r in 0 until size) {
            for (c in 0 until size) {
                if (grid[r][c].value == TILE_WALL) {
                    return true
                }
            }
        }
        return false
    }
    //Lấy tile khi click vào
    fun getTileAt(x: Float, y: Float): Pair<Int, Int>? {
        for (r in 0 until size) {
            for (c in 0 until size) {
                val (dx, dy) = gridToPos(r, c)
                if (x >= dx && x <= dx + tileSize &&
                    y >= dy && y <= dy + tileSize) {
                    return Pair(r, c)
                }
            }
        }
        return null
    }
    fun clearRow(row: Int) {
        for (c in 0 until size) {
            val t = grid[row][c]
            if (t.value != TILE_WALL) {   // bỏ qua wall
                grid[row][c] = Tile()     // reset về ô trống
                addExplosionBoom(row, c)      // hiệu ứng nổ (nếu muốn)
            }
        }
    }
    //--------------------------------------------------------------------------
    fun setTile(r: Int, c: Int, value: Int, frozen: Int = 0) {
        grid[r][c].value = value
        grid[r][c].frozen = frozen
    }

    fun getTile(r: Int, c: Int): Tile = grid[r][c]

    fun isFrozen(r: Int, c: Int) = grid[r][c].frozen > 0

/*    fun getEmptyCells(): List<Pair<Int, Int>> {
        val res = mutableListOf<Pair<Int, Int>>()
        for (r in 0 until size) for (c in 0 until size) if (grid[r][c] == 0) res.add(r to c)
        return res
    }*/
/*    fun getEmptyCells(): List<Pair<Int, Int>> {
        val result = mutableListOf<Pair<Int, Int>>()
        for (r in 0 until size) {
            for (c in 0 until size) {
                if (grid[r][c].value == 0) { // chỉ lấy ô trống, bỏ qua tường (-1)
                    result.add(Pair(r, c))
                }
            }
        }
        return result
    }*/
// atomic set
fun setTile(r: Int, c: Int, tile: Tile) {
    grid[r][c] = tile
}

    // convenience overload (tạo Tile mới giữ nguyên các giá trị khác)
    fun setTile(r: Int, c: Int, value: Int, frozen: Int = 0, isBoom: Boolean = false, boomCounter: Int = 0) {
        grid[r][c].value = value
        grid[r][c].frozen = frozen
        grid[r][c].isBoom = isBoom
        grid[r][c].boomCounter = boomCounter
    }

    // get empty cells — chỉ những ô "thật sự trống"
/*    fun getEmptyCells(): List<Pair<Int, Int>> {
        val result = mutableListOf<Pair<Int, Int>>()
        for (r in 0 until size) {
            for (c in 0 until size) {
                val t = grid[r][c]
                // trống nghĩa là value == 0 và không phải wall, không phải boom, không đang frozen
                if (t.value == 0 && t.value != TILE_WALL && t.frozen == 0 && !t.isBoom) {
                    result.add(r to c)
                }
            }
        }
        return result
    }*/
    fun getEmptyCells(): List<Pair<Int, Int>> {
        val result = mutableListOf<Pair<Int, Int>>()
        for (r in 0 until size) {
            for (c in 0 until size) {
                val t = grid[r][c]
                // Ô trống chỉ khi value == 0 và không có hiệu ứng đặc biệt
                if (t.value == 0 && t.frozen == 0 && !t.isBoom && !t.isThunder) {
                    result.add(r to c)
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
    // --- Lấy vị trí góc dưới trái của tile (để vẽ hình) ---
    fun getTilePosition(row: Int, col: Int): Pair<Float, Float> {
        return gridToPos(row, col)
    }

    // --- Lấy vị trí trung tâm tile (thường dùng cho hiệu ứng bay, damage, v.v.) ---
    fun getTileCenter(row: Int, col: Int): Pair<Float, Float> {
        val (x, y) = gridToPos(row, col)
        return Pair(x + tileSize / 2f, y + tileSize / 2f)
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

                if (v.value == TILE_WALL) {
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
                if (v.value <= 0) continue // bỏ qua ô trống và tường

                if (spawnAnimations.any { it.row == r && it.col == c }) continue
                if (animations.any { it.toR == r && it.toC == c }) continue

                val (dx, dy) = gridToPos(r, c)
                var scale = 1f

                // merge animation
                val mergeAnim = mergeAnimations.find { it.row == r && it.col == c && it.value == v.value }
                if (mergeAnim != null) {
                    mergeAnim.time += dt
                    val t = (mergeAnim.time / 0.25f).coerceAtMost(1f)
                    scale = 1.0f + 0.2f * sin(t * Math.PI).toFloat()
                    if (t >= 1f) mergeAnimations.remove(mergeAnim)
                }

                val offset = tileSize * (1 - scale) / 2
                tileImages[v.value]?.let { batch.draw(it, dx + offset, dy + offset, tileSize * scale, tileSize * scale) }
                // nếu bị đóng băng thì vẽ overlay màu xanh nhạt
                if (v.frozen > 0) {
                    // Vẽ băng với alpha 0.7
                    batch.color = Color(1f, 1f, 1f, 0.7f)
                    batch.draw(iceTileTextures, dx, dy, tileSize, tileSize)
                    batch.color = Color.WHITE

                    // Vẽ số lượt rã đông còn lại
                    val frozenText = v.frozen.toString()
                    val layout = GlyphLayout(txt_font, frozenText)
                    val textX = dx + (tileSize - layout.width) / 2f
                    val textY = dy + (tileSize + layout.height) / 2f

                    txt_font.color = Color.BLACK  // chữ đen cho dễ nhìn
                    txt_font.draw(batch, layout, textX, textY)
                }
                if (v.isBoom) {
                    // Vẽ tile boom (màu đỏ + chữ B)
                    batch.color = Color(1f, 1f, 1f, 1f)
                    batch.draw(bombTileTextures, dx, dy, tileSize/2, tileSize/2)
                    batch.color = Color.WHITE

/*                    val boomText = "B"
                    val layout = GlyphLayout(labelFont, boomText)
                    val textX = dx + (tileSize - layout.width) / 2f
                    val textY = dy + (tileSize + layout.height) / 2f
                    labelFont.color = Color.YELLOW
                    labelFont.draw(batch, layout, textX, textY)*/

                    // Vẽ số lượt còn lại
                    val counterText = v.boomCounter.toString()
                    val layout2 = GlyphLayout(txt_font, counterText)
                    val textX2 = dx + (tileSize - layout2.width) / 2f
                    val textY2 = dy + (tileSize + layout2.height) / 2f
                    txt_font.color = Color.BLACK
                    txt_font.draw(batch, layout2, textX2, textY2)
                    continue
                }
                if (v.isThunder) {
                    // Vẽ tile Thunder (icon riêng)
                    batch.color = Color(1f, 1f, 1f, 1f)
                    batch.draw(thunderTileTexture, dx, dy, tileSize / 2, tileSize / 2)
                    batch.color = Color.WHITE

                    // Vẽ số lượt còn lại (đếm ngược thunderCounter)
                    val thunderText = v.thunderCounter.toString()
                    val layout3 = GlyphLayout(txt_font, thunderText)
                    val textX3 = dx + (tileSize - layout3.width) / 2f
                    val textY3 = dy + (tileSize + layout3.height) / 2f
                    txt_font.color = Color.BLACK
                    txt_font.draw(batch, layout3, textX3, textY3)

                    //continue // bỏ qua vẽ các lớp khác
                    continue
                }

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
        // ---- 6. Vẽ explosion bomb----
        val boomexp = explosionsBoom.iterator()
        while (boomexp.hasNext()) {
            val e = boomexp.next()
            e.time += dt
            val frame = explosionBoomSheet.getFrameAt(e.time, looping = false)
            val size = tileSize * 1.2f
            val offset = (size - tileSize) / 2

            batch.draw(frame, e.x - offset, e.y - offset, size, size)
            if (explosionBoomSheet.isAnimationFinished(e.time)) {
                boomexp.remove()
            }
        }
        // ---- 7. Vẽ ice thaw ----
        val iceThaw = explosionsIceThaw.iterator()
        while (iceThaw.hasNext()) {
            val e = iceThaw.next()
            e.time += dt
            val frame = explosionThawSheet.getFrameAt(e.time, looping = false)
            val size = tileSize * 1.2f
            val offset = (size - tileSize) / 2

            batch.draw(frame, e.x - offset, e.y - offset, size, size)
            if (explosionBoomSheet.isAnimationFinished(e.time)) {
                iceThaw.remove()
            }
        }
        //---------8. Vẽ boom thunder----------------
        val thunder = explosionsThunder.iterator()
        while (thunder.hasNext()) {
            val e = thunder.next()
            e.time += dt
            val frame = explosionThunderSheet.getFrameAt(e.time, looping = false)
            val size = tileSize * 1.2f
            val offset = (size - tileSize) / 2

            batch.draw(frame, e.x - offset, e.y - offset, size, size)
            if (explosionThunderSheet.isAnimationFinished(e.time)) {
                thunder.remove()
            }
        }
        //-------------9. Vẽ boom boss----------------
        val boomboss = explosionsBoomboss.iterator()
        while (boomboss.hasNext()) {
            val e = boomboss.next()
            e.time += dt
            val frame = explosionBoombossSheet.getFrameAt(e.time, looping = false)
            batch.draw(
                frame,
                e.x - frame.regionWidth / 2f,
                e.y - frame.regionHeight / 2f
            )
            if (explosionBoombossSheet.isAnimationFinished(e.time)) {
                boomboss.remove()
            }
        }
    }
    fun clearAllDebuffs(): Int {
        var count = 0
        for (x in 0 until size) {
            for (y in 0 until size) {
                val tile = grid[x][y]
                if(tile.value != TILE_WALL&& tile.value!=0){
                    addExplosion(x, y)                // Kiểm tra nếu tile có bất kỳ hiệu ứng bất lợi nào
                }
                if (tile.isBoom || tile.frozen > 0) {
                    // Xóa các hiệu ứng, giữ nguyên value
                    if (tile.isBoom) {
                        tile.isBoom = false
                        tile.boomCounter = 0
                    }
                    if (tile.frozen > 0) {
                        tile.frozen = 0
                    }
                    // Hiệu ứng nổ khi dọn tile
                    SoundManager.playSfx(SoundId.CLEARDEBUFF)
                    count++
                }
            }
        }
        return count
    }


    fun dispose() {
        tileImages.values.forEach { it.dispose() }
        explosionSheet.dispose()
        whiteTexture.dispose()
    }
}
