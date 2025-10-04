package io.github.cogdanh2k3.screens.GamePlay

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
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.viewport.StretchViewport
import io.github.cogdanh2k3.DataGame.SaveManager
import io.github.cogdanh2k3.Main
import io.github.cogdanh2k3.Mode.GameMode
import io.github.cogdanh2k3.screens.MenuScreen

class LoseScreen(
    private val game: Main,
    private val score: Int,
    private val mode: GameMode
) : ScreenAdapter() {

    private val camera = OrthographicCamera()
    private val viewport = StretchViewport(480f, 800f, camera)

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

    private val buttonY = 160f
    private val retryBtn = Rectangle(100f, buttonY, 120f, 60f)
    private val homeBtn = Rectangle(260f, buttonY, 120f, 60f)

    private lateinit var loseTexture: Texture

    override fun show() {
        loseTexture = Texture("UI/youlose.png")
    }

    // 🌈 Nền sáng hơn (vẫn cảm giác "thua" nhưng không tối xỉu)
    // 🌈 Nền sáng tối nhẹ (vẫn mang cảm giác thua)
    private fun drawLightDarkGradientBackground() {
        val steps = 100
        val screenW = viewport.worldWidth
        val screenH = viewport.worldHeight

        val bottom = Color(0.15f, 0.1f, 0.2f, 1f) // tím đậm
        val middle = Color(0.35f, 0.2f, 0.35f, 1f) // tím hồng
        val top = Color(0.5f, 0.35f, 0.45f, 1f)    // hồng nhạt sáng

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
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.15f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        viewport.apply()
        camera.update()
        batch.projectionMatrix = camera.combined
        shapeRenderer.projectionMatrix = camera.combined

        drawLightDarkGradientBackground()

        // Nút
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        shapeRenderer.color = Color.RED
        shapeRenderer.rect(retryBtn.x, retryBtn.y, retryBtn.width, retryBtn.height)
        shapeRenderer.color = Color.SKY
        shapeRenderer.rect(homeBtn.x, homeBtn.y, homeBtn.width, homeBtn.height)
        shapeRenderer.end()

        // Viền nút
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
        shapeRenderer.color = Color.BLACK
        shapeRenderer.rect(retryBtn.x, retryBtn.y, retryBtn.width, retryBtn.height)
        shapeRenderer.rect(homeBtn.x, homeBtn.y, homeBtn.width, homeBtn.height)
        shapeRenderer.end()

        batch.begin()

        // 🖼 YOU LOSE (giữ đúng tỉ lệ và vị trí)
        val screenWidth = viewport.worldWidth
        val screenHeight = viewport.worldHeight

        val maxWidth = screenWidth * 0.7f
        val scaleRatio = maxWidth / loseTexture.width
        val textureWidth = loseTexture.width * scaleRatio
        val textureHeight = loseTexture.height * scaleRatio

        val textureX = (screenWidth - textureWidth) / 2f
        val textureY = (screenHeight - textureHeight) / 2f + 100f

        batch.draw(loseTexture, textureX, textureY, textureWidth, textureHeight)

        // Điểm số
        infoFont.draw(batch, "Score: $score", screenWidth / 2f - 70f, textureY - 40f)

        // Text trong nút
        drawButtonText("Play Again", retryBtn)
        drawButtonText("Home", homeBtn)

        batch.end()

        // Input
        if (Gdx.input.justTouched()) {
            val touch = Vector3(Gdx.input.x.toFloat(), Gdx.input.y.toFloat(), 0f)
            viewport.unproject(touch)
            when {
                retryBtn.contains(touch.x, touch.y) -> game.screen = GameScreen(game, mode)
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
        loseTexture.dispose()
    }
}
