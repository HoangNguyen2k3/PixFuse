package io.github.cogdanh2k3.screens.GamePlay

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.Screen
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
import io.github.cogdanh2k3.Main
import io.github.cogdanh2k3.screens.MenuScreen

class PauseScreen(val game: Main, private val gameScreen: GameScreen) : Screen {

    private val camera = OrthographicCamera()
    private val viewport = StretchViewport(800f, 1280f, camera)
    private val batch = SpriteBatch()
    private val shapeRenderer = ShapeRenderer()

    // Textures
    private val pauseTitle = Texture("effects/pause.png")
    private val pikachuTexture = Texture("UI/pikachu.png")

    // Font
    private val buttonFont = BitmapFont().apply {
        data.setScale(2f)
        color = Color.WHITE
    }

    // Buttons
    private val resumeBtn = Rectangle()
    private val menuBtn = Rectangle()

    // Colors
    private val resumeColor = Color(0.3f, 0.7f, 0.6f, 0.9f)
    private val menuColor = Color(0.8f, 0.4f, 0.4f, 0.9f)
    private val borderColor = Color.BLACK

    override fun show() {
        layoutButtons()
    }

    private fun layoutButtons() {
        val centerX = viewport.worldWidth / 2f
        val centerY = viewport.worldHeight / 2f
        val spacing = 50f
        val btnWidth = 300f
        val btnHeight = 80f

        resumeBtn.set(centerX - btnWidth / 2f, centerY + spacing, btnWidth, btnHeight)
        menuBtn.set(centerX - btnWidth / 2f, centerY - spacing, btnWidth, btnHeight)
    }

    override fun render(delta: Float) {
        handleInput()

        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        viewport.apply()
        camera.update()
        batch.projectionMatrix = camera.combined
        shapeRenderer.projectionMatrix = camera.combined

        val worldW = viewport.worldWidth
        val worldH = viewport.worldHeight

        // ===== GRADIENT BACKGROUND =====
        val steps = 80
        val stepH = worldH / steps
        val bottom = Color(1f, 0.85f, 0.6f, 1f)
        val top = Color(0.7f, 0.95f, 0.95f, 1f)

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        for (i in 0 until steps) {
            val t = i.toFloat() / (steps - 1)
            shapeRenderer.color = lerpColor(bottom, top, t)
            shapeRenderer.rect(0f, i * stepH, worldW, stepH)
        }
        shapeRenderer.end()

        // ===== DRAW TITLE & PIKACHU =====
        batch.begin()

        // --- Title “Pause” ---
        val aspectTitle = pauseTitle.width.toFloat() / pauseTitle.height
        val desiredTitleW = worldW * 0.7f
        val titleW = desiredTitleW
        val titleH = desiredTitleW / aspectTitle
        val titleX = (worldW - titleW) / 2f
        val titleY = worldH - titleH - 160f // 🔽 hạ xuống một chút cho cân đối
        batch.draw(pauseTitle, titleX, titleY, titleW, titleH)

        // --- Pikachu bottom ---
        val desiredH = worldH * 0.22f
        val aspect = pikachuTexture.width.toFloat() / pikachuTexture.height.toFloat()
        val pikaW = desiredH * aspect
        val pikaX = (worldW - pikaW) / 2f
        val pikaY = 0f
        batch.draw(pikachuTexture, pikaX, pikaY, pikaW, desiredH)

        batch.end()

        // ===== DRAW BUTTONS =====
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        shapeRenderer.color = resumeColor
        shapeRenderer.rect(resumeBtn.x, resumeBtn.y, resumeBtn.width, resumeBtn.height)
        shapeRenderer.color = menuColor
        shapeRenderer.rect(menuBtn.x, menuBtn.y, menuBtn.width, menuBtn.height)
        shapeRenderer.end()

        // Borders
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
        shapeRenderer.color = borderColor
        shapeRenderer.rect(resumeBtn.x, resumeBtn.y, resumeBtn.width, resumeBtn.height)
        shapeRenderer.rect(menuBtn.x, menuBtn.y, menuBtn.width, menuBtn.height)
        shapeRenderer.end()

        // ===== DRAW TEXT =====
        batch.begin()
        drawButtonText("RESUME", resumeBtn)
        drawButtonText("MAIN MENU", menuBtn)
        batch.end()
    }

    private fun lerpColor(a: Color, b: Color, t: Float): Color {
        val tt = t.coerceIn(0f, 1f)
        return Color(
            a.r + (b.r - a.r) * tt,
            a.g + (b.g - a.g) * tt,
            a.b + (b.b - a.b) * tt,
            a.a + (b.a - a.a) * tt
        )
    }

    private fun drawButtonText(text: String, btn: Rectangle) {
        val layout = GlyphLayout(buttonFont, text)
        val textX = btn.x + (btn.width - layout.width) / 2f
        val textY = btn.y + (btn.height + layout.height) / 2f
        buttonFont.draw(batch, layout, textX, textY)
    }

    private fun handleInput() {
        if (Gdx.input.justTouched()) {
            val touch = Vector3(Gdx.input.x.toFloat(), Gdx.input.y.toFloat(), 0f)
            viewport.unproject(touch)
            if (resumeBtn.contains(touch.x, touch.y)) resumeGame()
            if (menuBtn.contains(touch.x, touch.y)) goToMainMenu()
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) ||
            Gdx.input.isKeyJustPressed(Input.Keys.BACK)
        ) {
            resumeGame()
        }
    }

    private fun resumeGame() {
        game.screen = gameScreen
    }

    private fun goToMainMenu() {
        gameScreen.dispose()
        game.screen = MenuScreen(game)
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height, true)
        layoutButtons()
    }

    override fun pause() {}
    override fun resume() {}
    override fun hide() {}

    override fun dispose() {
        batch.dispose()
        shapeRenderer.dispose()
        buttonFont.dispose()
        pauseTitle.dispose()
        pikachuTexture.dispose()
    }
}
