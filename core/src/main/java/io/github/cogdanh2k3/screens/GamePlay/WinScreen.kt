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
import com.badlogic.gdx.utils.viewport.StretchViewport
import io.github.cogdanh2k3.DataGame.GameSave
import io.github.cogdanh2k3.DataGame.LevelData
import io.github.cogdanh2k3.DataGame.SaveManager
import io.github.cogdanh2k3.Main
import io.github.cogdanh2k3.Mode.GameMode
import io.github.cogdanh2k3.Mode.TargetMode
import io.github.cogdanh2k3.game.LevelManager
import io.github.cogdanh2k3.screens.GamePlay.GameScreen
import io.github.cogdanh2k3.utils.SpriteSheetAnimation
import kotlin.math.max

class WinScreen(
    private val game: Main,
    private val score: Int,
    private val mode: GameMode,
    private val levelData: LevelData ?= null
) : ScreenAdapter() {

    private val camera = OrthographicCamera()
    private val viewport = StretchViewport(480f, 800f, camera)

    private val batch = SpriteBatch()
    private val shapeRenderer = ShapeRenderer()

    // Fonts
    private val infoFont = BitmapFont().apply {
        data.setScale(1.5f)
        color = Color.CYAN
    }
    private val buttonFont = BitmapFont().apply {
        data.setScale(1.3f)
        color = Color.BLACK
    }

    // Buttons
    private val playAgainBtn = Rectangle(100f, 280f, 120f, 60f)
    private val homeBtn = Rectangle(260f, 280f, 120f, 60f)

    // Assets
    private lateinit var fireworkAnim: SpriteSheetAnimation
    private lateinit var winTexture: Texture
    data class Firework(
        val x: Float,
        val y: Float,
        val width: Float,
        val height: Float,
        var time: Float = 0f
    )
    private val fireworks = mutableListOf<Firework>()

    override fun show() {
        fireworkAnim = SpriteSheetAnimation("titles/firework.png", 5, 6, 0.05f)
        winTexture = Texture("UI/youwin.png")
        fireworks.add(Firework(80f, 600f, MathUtils.random(100f, 300f), MathUtils.random(100f, 300f)))
        fireworks.add(Firework(300f, 400f, MathUtils.random(100f, 300f), MathUtils.random(100f, 300f)))
        fireworks.add(Firework(200f, 550f, MathUtils.random(100f, 300f), MathUtils.random(100f, 300f)))
        fireworks.add(Firework(50f, 100f, MathUtils.random(100f, 300f), MathUtils.random(100f, 300f)))
        fireworks.add(Firework(300f, 50f, MathUtils.random(100f, 300f), MathUtils.random(100f, 300f)))
    }

    private fun drawVerticalGradient() {
        val steps = 100
        val width = viewport.worldWidth
        val height = viewport.worldHeight
        val bottom = Color(1f, 0.6f, 0.2f, 1f) // dịu hơn
        val middle = Color(1f, 0.7f, 0.2f, 1f)
        val top = Color(1f, 0.9f, 0.4f, 1f)   // vàng nhạt

        shapeRenderer.projectionMatrix = camera.combined
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        val stepH = height / steps
        for (i in 0 until steps) {
            val tNormalized = i.toFloat() / (steps - 1)
            val col = if (tNormalized < 0.5f) {
                lerpColor(bottom, middle, tNormalized / 0.5f)
            } else {
                lerpColor(middle, top, (tNormalized - 0.5f) / 0.5f)
            }
            shapeRenderer.color = col
            shapeRenderer.rect(0f, i * stepH, width, stepH + 1f)
        }
        shapeRenderer.end()
    }

    private fun lerpColor(a: Color, b: Color, t: Float): Color {
        val clamped = max(0f, minOf(1f, t))
        return Color(
            a.r + (b.r - a.r) * clamped,
            a.g + (b.g - a.g) * clamped,
            a.b + (b.b - a.b) * clamped,
            a.a + (b.a - a.a) * clamped
        )
    }

    override fun render(delta: Float) {
        fireworkAnim.update(delta)

        // Clear trước
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        camera.update()
        batch.projectionMatrix = camera.combined
        shapeRenderer.projectionMatrix = camera.combined

        // --- 1. Vẽ shape ---
        // Gradient nền
        drawVerticalGradient()

        // Button nền
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        shapeRenderer.color = Color.GOLD
        shapeRenderer.rect(playAgainBtn.x, playAgainBtn.y, playAgainBtn.width, playAgainBtn.height)

        shapeRenderer.color = Color.SKY
        shapeRenderer.rect(homeBtn.x, homeBtn.y, homeBtn.width, homeBtn.height)
        shapeRenderer.end()

        // Button viền
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
        shapeRenderer.color = Color.BLACK
        shapeRenderer.rect(playAgainBtn.x, playAgainBtn.y, playAgainBtn.width, playAgainBtn.height)
        shapeRenderer.rect(homeBtn.x, homeBtn.y, homeBtn.width, homeBtn.height)
        shapeRenderer.end()

        // --- 2. Vẽ batch ---
        batch.begin()

        // YOU WIN (căn giữa)
        val screenWidth = viewport.worldWidth
        val screenHeight = viewport.worldHeight
        val scale = 0.6f
        val textureWidth = winTexture.width * scale
        val textureHeight = winTexture.height * scale
        batch.draw(
            winTexture,
            (screenWidth - textureWidth) / 2f,
            (screenWidth+screenHeight/2.5f) / 2f,
            textureWidth,
            textureHeight
        )

        // Score
        infoFont.draw(batch, "Score: $score", screenWidth / 2f - 60f, screenHeight / 2f - 10f)

        // Button text
        drawButtonText("Next Level", playAgainBtn)
        drawButtonText("Home", homeBtn)

        // Fireworks
        val frame = fireworkAnim.getFrame(true)
        for (fw in fireworks) {
            batch.draw(frame, fw.x, fw.y, fw.width, fw.height)
        }

        batch.end()

        // --- 3. Xử lý input ---
        if (Gdx.input.justTouched()) {
            val touch = Vector3(Gdx.input.x.toFloat(), Gdx.input.y.toFloat(), 0f)
            viewport.unproject(touch)
            when {
                playAgainBtn.contains(touch.x, touch.y) -> {
                    val gameSave: GameSave = SaveManager.loadGameSave()

                    if (levelData != null) {
                        val world = LevelManager.worlds.find { it.id == levelData.currentWorld }
                        if (world != null) {
                            val nextIndex = levelData.indexInWorld + 1

                            // 1. Trường hợp còn level trong world hiện tại
                            val nextLevel = world.levels.find { it.indexInWorld == nextIndex }
                            if (nextLevel != null && nextLevel.unlocked) {
                                game.screen = GameScreen(
                                    game,
                                    TargetMode(nextLevel.target),
                                    nextLevel
                                )
                            } else {
                                // 2. Nếu hết level trong world hiện tại → sang world mới
                                val nextWorld = LevelManager.worlds.find { it.id == levelData.currentWorld + 1 }
                                if (nextWorld != null && nextWorld.levels.isNotEmpty()) {
                                    val firstLevel = nextWorld.levels[0]
                                    if (firstLevel.unlocked) {
                                        game.screen = GameScreen(
                                            game,
                                            TargetMode(firstLevel.target),
                                            firstLevel
                                        )
                                    } else {
                                        // World chưa unlock → quay về menu
                                        game.screen = MenuScreen(game)
                                    }
                                } else {
                                    // 3. Không còn world nào nữa
                                    game.screen = MenuScreen(game)
                                }
                            }
                        } else {
                            // Không tìm thấy world hiện tại
                            game.screen = MenuScreen(game)
                        }
                    } else {
                        // Trường hợp không có levelData (ví dụ Endless mode)
                        val level = LevelData()
                        game.screen = GameScreen(game, mode, level)
                    }
                }


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

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height, true)
    }

    override fun dispose() {
        batch.dispose()
        shapeRenderer.dispose()
        infoFont.dispose()
        buttonFont.dispose()
        fireworkAnim.dispose()
        winTexture.dispose()
    }
}
