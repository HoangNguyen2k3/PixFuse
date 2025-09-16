package io.github.cogdanh2k3.screens.GamePlay

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.BitmapFont
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
        data.setScale(4f)
        color = Color.WHITE
    }
    private val buttonFont = BitmapFont().apply {
        data.setScale(2f)
        color = Color.WHITE
    }

    // Button properties
    private val resumeButtonX = viewport.worldWidth / 2 - 100f
    private val resumeButtonY = viewport.worldHeight / 2 + 20f
    private val resumeButtonWidth = 200f
    private val resumeButtonHeight = 60f

    private val mainMenuButtonX = viewport.worldWidth / 2 - 100f
    private val mainMenuButtonY = viewport.worldHeight / 2 - 60f
    private val mainMenuButtonWidth = 200f
    private val mainMenuButtonHeight = 60f

    override fun show() {
        // No specific setup needed
    }

    override fun render(delta: Float) {
        handleInput()

        // Semi-transparent dark overlay background
        Gdx.gl.glClearColor(0f, 0f, 0f, 0.7f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        camera.update()
        batch.projectionMatrix = camera.combined
        shapeRenderer.projectionMatrix = camera.combined

        // Draw semi-transparent overlay
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        shapeRenderer.color = Color(0f, 0f, 0f, 0.5f)
        shapeRenderer.rect(0f, 0f, viewport.worldWidth, viewport.worldHeight)

        // Draw buttons
        drawButtons()
        shapeRenderer.end()

        batch.begin()

        // Title
        titleFont.draw(batch, "PAUSED", viewport.worldWidth / 2 - 80f, viewport.worldHeight / 2 + 150f)

        // Button texts
        buttonFont.draw(batch, "RESUME", resumeButtonX + 60f, resumeButtonY + 38f)
        buttonFont.draw(batch, "MAIN MENU", mainMenuButtonX + 40f, mainMenuButtonY + 38f)

        batch.end()
    }

    private fun handleInput() {
        if (Gdx.input.justTouched()) {
            val touchPoint = Vector3(Gdx.input.x.toFloat(), Gdx.input.y.toFloat(), 0f)
            viewport.unproject(touchPoint)

            // Check resume button
            if (touchPoint.x >= resumeButtonX && touchPoint.x <= resumeButtonX + resumeButtonWidth &&
                touchPoint.y >= resumeButtonY && touchPoint.y <= resumeButtonY + resumeButtonHeight) {
                resumeGame()
            }

            // Check main menu button
            if (touchPoint.x >= mainMenuButtonX && touchPoint.x <= mainMenuButtonX + mainMenuButtonWidth &&
                touchPoint.y >= mainMenuButtonY && touchPoint.y <= mainMenuButtonY + mainMenuButtonHeight) {
                goToMainMenu()
            }
        }

        // Allow ESC or back button to resume
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) ||
            Gdx.input.isKeyJustPressed(Input.Keys.BACK)) {
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

    private fun drawButtons() {
        // Resume button
        shapeRenderer.color = Color(0.4f, 0.7f, 0.4f, 0.8f) // Green
        drawRoundedRect(resumeButtonX, resumeButtonY, resumeButtonWidth, resumeButtonHeight, 10f)

        // Main menu button
        shapeRenderer.color = Color(0.7f, 0.4f, 0.4f, 0.8f) // Red
        drawRoundedRect(mainMenuButtonX, mainMenuButtonY, mainMenuButtonWidth, mainMenuButtonHeight, 10f)
    }

    private fun drawRoundedRect(x: Float, y: Float, width: Float, height: Float, radius: Float) {
        shapeRenderer.rect(x + radius, y, width - 2 * radius, height)
        shapeRenderer.rect(x, y + radius, width, height - 2 * radius)
        shapeRenderer.circle(x + radius, y + radius, radius)
        shapeRenderer.circle(x + width - radius, y + radius, radius)
        shapeRenderer.circle(x + radius, y + height - radius, radius)
        shapeRenderer.circle(x + width - radius, y + height - radius, radius)
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height, true)
    }

    override fun pause() {
        // Already paused
    }

    override fun resume() {
        // Handle resume if needed
    }

    override fun hide() {
        // Clean up when hidden
    }

    override fun dispose() {
        batch.dispose()
        shapeRenderer.dispose()
        titleFont.dispose()
        buttonFont.dispose()
    }
}
