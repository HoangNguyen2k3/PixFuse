package io.github.cogdanh2k3.screens.GamePlay

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.GlyphLayout
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.viewport.FitViewport
import io.github.cogdanh2k3.Main
import io.github.cogdanh2k3.screens.MenuScreen

class PauseScreen(val game: Main, private val gameScreen: GameScreen): Screen {

    private val camera = OrthographicCamera()
    private val viewport = FitViewport(800f, 600f, camera)
    private val batch = SpriteBatch()
    private val shapeRenderer = ShapeRenderer()

    // Fonts
    private val titleFont = BitmapFont().apply {
        data.setScale(3.5f)
        color = Color(0.9f, 0.95f, 1f, 1f) // pastel xanh nhạt
    }
    private val buttonFont = BitmapFont().apply {
        data.setScale(2f)
        color = Color.WHITE
    }

    // Button style
    data class Button(val text: String, var x: Float, var y: Float, var width: Float, var height: Float, val color: Color)

    private val buttons = listOf(
        Button("RESUME", 0f, 0f, 240f, 70f, Color(0.3f, 0.7f, 0.6f, 0.9f)),
        Button("MAIN MENU", 0f, 0f, 240f, 70f, Color(0.8f, 0.4f, 0.4f, 0.9f))
    )

    override fun show() {
        layoutButtons()
    }

    private fun layoutButtons() {
        val centerX = viewport.worldWidth / 2
        val centerY = viewport.worldHeight / 2
        val spacing = 90f

        buttons[0].x = centerX - buttons[0].width / 2
        buttons[0].y = centerY + spacing
        buttons[1].x = centerX - buttons[1].width / 2
        buttons[1].y = centerY - spacing
    }

    override fun render(delta: Float) {
        handleInput()

        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        camera.update()
        batch.projectionMatrix = camera.combined
        shapeRenderer.projectionMatrix = camera.combined

        // overlay mờ
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        shapeRenderer.color = Color(0f, 0f, 0f, 0.6f)
        shapeRenderer.rect(0f, 0f, viewport.worldWidth, viewport.worldHeight)

        // vẽ button
        for (btn in buttons) {
            shapeRenderer.color = btn.color
            drawRoundedRect(btn.x, btn.y, btn.width, btn.height, 20f)
        }
        shapeRenderer.end()

        // vẽ chữ
        batch.begin()

        // Title căn giữa
        val titleLayout = GlyphLayout(titleFont, "PAUSED")
        titleFont.draw(batch, titleLayout, viewport.worldWidth/2 - titleLayout.width/2, viewport.worldHeight - 30f)

        // Button text căn giữa
        for (btn in buttons) {
            val layout = GlyphLayout(buttonFont, btn.text)
            val textX = btn.x + (btn.width - layout.width) / 2
            val textY = btn.y + (btn.height + layout.height) / 2
            buttonFont.draw(batch, layout, textX, textY)
        }

        batch.end()
    }

    private fun handleInput() {
        if (Gdx.input.justTouched()) {
            val touchPoint = Vector3(Gdx.input.x.toFloat(), Gdx.input.y.toFloat(), 0f)
            viewport.unproject(touchPoint)

            if (touchPoint.overlaps(buttons[0])) resumeGame()
            if (touchPoint.overlaps(buttons[1])) goToMainMenu()
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) || Gdx.input.isKeyJustPressed(Input.Keys.BACK)) {
            resumeGame()
        }
    }

    private fun Vector3.overlaps(btn: Button): Boolean {
        return x >= btn.x && x <= btn.x + btn.width &&
            y >= btn.y && y <= btn.y + btn.height
    }

    private fun resumeGame() { game.screen = gameScreen }
    private fun goToMainMenu() {
        gameScreen.dispose()
        game.screen = MenuScreen(game)
    }

    private fun drawRoundedRect(x: Float, y: Float, width: Float, height: Float, radius: Float) {
        shapeRenderer.rect(x + radius, y, width - 2 * radius, height)
        shapeRenderer.rect(x, y + radius, width, height - 2 * radius)
        shapeRenderer.circle(x + radius, y + radius, radius)
        shapeRenderer.circle(x + width - radius, y + radius, radius)
        shapeRenderer.circle(x + radius, y + height - radius, radius)
        shapeRenderer.circle(x + width - radius, y + height - radius, radius)
    }

    override fun resize(width: Int, height: Int) { viewport.update(width, height, true) }
    override fun pause() {}
    override fun resume() {}
    override fun hide() {}
    override fun dispose() {
        batch.dispose(); shapeRenderer.dispose(); titleFont.dispose(); buttonFont.dispose()
    }
}
