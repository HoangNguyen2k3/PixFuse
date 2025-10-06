package io.github.cogdanh2k3.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.GlyphLayout
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.viewport.StretchViewport  // Thêm import này (nếu chưa có)
import io.github.cogdanh2k3.Boss
import io.github.cogdanh2k3.DataGame.BossDatabase
import io.github.cogdanh2k3.DataGame.LevelData
import io.github.cogdanh2k3.DataGame.SaveManager
import io.github.cogdanh2k3.Main
import io.github.cogdanh2k3.Mode.BattleMode
import io.github.cogdanh2k3.Mode.GameMode
import io.github.cogdanh2k3.Mode.TargetMode
import io.github.cogdanh2k3.game.LevelManager
import io.github.cogdanh2k3.screens.GamePlay.GameScreen
import io.github.cogdanh2k3.utils.SpriteSheetAnimation

class WinScreen(
    private val game: Main,
    private val score: Int,
    private val mode: GameMode,
    private val levelData: LevelData? = null,
    private val index_next_boss: Int? = -1
) : ScreenAdapter() {

    private val camera = OrthographicCamera()
    private val viewport = StretchViewport(480f, 800f, camera)  // <- FIX CHÍNH: Đổi sang StretchViewport

    private val batch = SpriteBatch()
    private val shapeRenderer = ShapeRenderer()

    private val infoFont = BitmapFont().apply {
        data.setScale(2.0f)
        color = Color.WHITE
    }
    private val buttonFont = BitmapFont().apply {
        data.setScale(1.5f)
        color = Color.BLACK
    }

    // Buttons responsive hơn (dùng % height)
    private val buttonY = 260f  // Có thể tính động: viewport.worldHeight * 0.3f trong resize()
    private val nextBtn = Rectangle(100f, buttonY, 120f, 60f)
    private val homeBtn = Rectangle(260f, buttonY, 120f, 60f)

    private lateinit var fireworkAnim: SpriteSheetAnimation
    private lateinit var winTexture: Texture
    private lateinit var rocketTexture: Texture

    data class Firework(
        var x: Float,
        var y: Float,
        var targetY: Float,
        var width: Float,
        var height: Float,
        var time: Float = 0f,
        var exploded: Boolean = false,
        var color: Color = Color.WHITE
    )

    private val fireworks = mutableListOf<Firework>()

    override fun show() {
        SaveManager.gameSave.addScore(score)
        if(mode is BattleMode){
          //  if(SaveManager.gameSave.int_levelBoss<4){
                SaveManager.gameSave.int_levelBoss++;
          //  }
/*            else{
                SaveManager.gameSave.int_levelBoss=0;
            }*/
        }
        SaveManager.saveGame()
        fireworkAnim = SpriteSheetAnimation("titles/firework.png", 5, 6, 0.05f)
        winTexture = Texture("UI/youwin.png")
        rocketTexture = Texture("effects/fireworkDot.png")

        repeat(10) {
            fireworks.add(
                Firework(
                    x = MathUtils.random(50f, 430f),
                    y = 0f,
                    targetY = MathUtils.random(300f, 700f),
                    width = MathUtils.random(100f, 180f),
                    height = MathUtils.random(100f, 180f),
                    color = Color(MathUtils.random(), MathUtils.random(), MathUtils.random(), 1f)
                )
            )
        }
    }

    // Background full màn hình (giữ nguyên, nhưng giờ với Stretch sẽ full hoàn hảo)
    private fun drawVerticalGradientFull() {
        val steps = 100
        val screenW = viewport.worldWidth
        val screenH = viewport.worldHeight

        val bottom = Color(1f, 0.6f, 0.2f, 1f) // cam
        val middle = Color(1f, 0.9f, 0.5f, 1f) // vàng nhạt
        val top = Color(0.5f, 0.9f, 1f, 1f)    // xanh dương nhạt

        shapeRenderer.projectionMatrix = camera.combined

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        val stepH = screenH / steps
        for (i in 0 until steps) {
            val t = i.toFloat() / (steps - 1)
            val col = when {
                t < 0.5f -> lerpColor(bottom, middle, t / 0.5f)
                else -> lerpColor(middle, top, (t - 0.5f) / 0.5f)
            }
            shapeRenderer.color = col
            shapeRenderer.rect(0f, i * stepH, screenW, stepH + 1f)
        }
        shapeRenderer.end()
    }


    private fun lerpColor(a: Color, b: Color, t: Float): Color {
        val clamped = t.coerceIn(0f, 1f)
        return Color(
            a.r + (b.r - a.r) * clamped,
            a.g + (b.g - a.g) * clamped,
            a.b + (b.b - a.b) * clamped,
            a.a + (b.a - a.a) * clamped
        )
    }

    override fun render(delta: Float) {
        // FIX NHỎ: Clear bằng màu top của gradient (tránh đen lộ)
        Gdx.gl.glClearColor(0.5f, 0.9f, 1f, 1f)  // Màu sky blue (top)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        // Áp dụng viewport NGAY ĐẦU để UI consistent (background vẫn full nhờ setToOrtho2D)
        viewport.apply()
        camera.update()
        batch.projectionMatrix = camera.combined
        shapeRenderer.projectionMatrix = camera.combined

        // Background full
        drawVerticalGradientFull()

        // Buttons
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        shapeRenderer.color = Color.GOLD
        shapeRenderer.rect(nextBtn.x, nextBtn.y, nextBtn.width, nextBtn.height)
        shapeRenderer.color = Color.SKY
        shapeRenderer.rect(homeBtn.x, homeBtn.y, homeBtn.width, homeBtn.height)
        shapeRenderer.end()

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
        shapeRenderer.color = Color.BLACK
        shapeRenderer.rect(nextBtn.x, nextBtn.y, nextBtn.width, nextBtn.height)
        shapeRenderer.rect(homeBtn.x, homeBtn.y, homeBtn.width, homeBtn.height)
        shapeRenderer.end()

        batch.begin()

        // "YOU WIN"
        val screenWidth = viewport.worldWidth
        val screenHeight = viewport.worldHeight
        val maxWidth = screenWidth * 0.7f
        val scaleRatio = maxWidth / winTexture.width
        val textureWidth = winTexture.width * scaleRatio
        val textureHeight = winTexture.height * scaleRatio
        batch.draw(
            winTexture,
            (screenWidth - textureWidth) / 2f,
            screenHeight * 0.65f,
            textureWidth,
            textureHeight
        )

        // Score
        infoFont.draw(batch, "Score: $score", screenWidth / 2f - 70f, screenHeight * 0.55f)

        // Button labels
        drawButtonText("Next Level", nextBtn)
        drawButtonText("Home", homeBtn)

        // Fireworks (giữ nguyên)
        for (fw in fireworks) {
            if (!fw.exploded) {
                fw.y += 220 * delta
                if (fw.y >= fw.targetY) {
                    fw.exploded = true
                    fw.time = 0f
                } else {
                    batch.setColor(Color.WHITE)
                    batch.draw(rocketTexture, fw.x, fw.y, 24f, 48f)
                }
            } else {
                fw.time += delta
                if (!fireworkAnim.isAnimationFinished(fw.time)) {
                    val frame = fireworkAnim.getFrame(fw.time, false)
                    batch.setColor(Color.WHITE)
                    batch.draw(
                        frame,
                        fw.x - fw.width,
                        fw.y - fw.height,
                        fw.width * 2,
                        fw.height * 2
                    )
                } else {
                    fw.x = MathUtils.random(50f, 430f)
                    fw.y = 0f
                    fw.targetY = MathUtils.random(300f, 700f)
                    fw.exploded = false
                }
            }
        }
        batch.setColor(Color.WHITE)

        batch.end()

        // Input (giữ nguyên)
        if (Gdx.input.justTouched()) {
            val touch = Vector3(Gdx.input.x.toFloat(), Gdx.input.y.toFloat(), 0f)
            viewport.unproject(touch)
            when {
                nextBtn.contains(touch.x, touch.y) -> goNextLevel()
                homeBtn.contains(touch.x, touch.y) -> game.screen = MenuScreen(game)
            }
        }
    }

    private fun drawButtonText(text: String, button: Rectangle) {
        val layout = GlyphLayout(buttonFont, text)
        val textX = button.x + (button.width - layout.width) / 2
        val textY = button.y + (button.height + layout.height) / 2
        buttonFont.draw(batch, text, textX, textY)
    }

    private fun goNextLevel() {
        if (levelData != null) {
            val world = LevelManager.worlds.find { it.id == levelData.currentWorld }
            if (world != null) {
                val nextIndex = levelData.indexInWorld + 1
                val nextLevel = world.levels.find { it.indexInWorld == nextIndex }
                if (nextLevel != null && nextLevel.unlocked) {
                    game.screen = GameScreen(game, TargetMode(nextLevel.target), nextLevel)
                    return
                }
                val nextWorld = LevelManager.worlds.find { it.id == levelData.currentWorld + 1 }
                if (nextWorld != null && nextWorld.levels.isNotEmpty()) {
                    val firstLevel = nextWorld.levels[0]
                    if (firstLevel.unlocked) {
                        game.screen = GameScreen(game, TargetMode(firstLevel.target), firstLevel)
                        return
                    }
                }
            }
        }
        if(mode is BattleMode){
            if(index_next_boss!=null){
            val bossData = BossDatabase.getBossForLevel(index_next_boss)
            val boss = Boss(
                name = bossData.name,
                hp = bossData.hp,
                texturePath = bossData.texturePath
            )
            val mode = BattleMode(boss, bossData.turnAttackBoss)
            game.screen = GameScreen(game, mode)
                return
        }
        }
        game.screen = MenuScreen(game)
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height, true)
        // Tùy chọn: Update buttonY responsive
        // buttonY = viewport.worldHeight * 0.3f
        // nextBtn.y = buttonY
        // homeBtn.y = buttonY
    }

    override fun dispose() {
        batch.dispose()
        shapeRenderer.dispose()
        infoFont.dispose()
        buttonFont.dispose()
        fireworkAnim.dispose()
        winTexture.dispose()
        rocketTexture.dispose()
    }
}
