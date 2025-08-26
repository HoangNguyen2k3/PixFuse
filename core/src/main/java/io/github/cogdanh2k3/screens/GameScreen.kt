package io.github.cogdanh2k3.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.input.GestureDetector
import com.badlogic.gdx.math.Interpolation
import io.github.cogdanh2k3.Main
import io.github.cogdanh2k3.game.Board
import io.github.cogdanh2k3.game.GameManager
import io.github.cogdanh2k3.utils.InputHandler

class GameScreen(val game: Main) : ScreenAdapter() {

    private val batch = SpriteBatch()
    private val shapeRenderer = ShapeRenderer()

    // Fonts
    private val titleFont = BitmapFont().apply {
        data.setScale(3f)
        color = Color(0.45f, 0.42f, 0.39f, 1f)
    }
    private val scoreFont = BitmapFont().apply {
        data.setScale(2f)
        color = Color.WHITE
    }
    private val labelFont = BitmapFont().apply {
        data.setScale(1.2f)
        color = Color(0.8f, 0.8f, 0.8f, 1f)
    }

    // Game objects
    private val board = Board(4)
    private val manager = GameManager(board)

    private var score = 0
    private var highScore = 0
    private var displayScore = 0f
    private var displayHighScore = 0f

    private var scoreAnimation = 0f
    private var backgroundHue = 0f

    init {
        manager.spawnTile()
        manager.spawnTile()

        val gestureDetector = GestureDetector(InputHandler(manager, this))
        Gdx.input.inputProcessor = gestureDetector

        val prefs = Gdx.app.getPreferences("PicFusePrefs")
        highScore = prefs.getInteger("highscore", 0)
        displayHighScore = highScore.toFloat()
    }

    override fun render(delta: Float) {
        if (manager.isMoved) {
            manager.update()
            val newScore = manager.score
            if (newScore > score) scoreAnimation = 1f
            score = newScore
        }

        displayScore = Interpolation.pow2Out.apply(displayScore, score.toFloat(), delta * 5f)
        if (score > highScore) {
            highScore = score
            displayHighScore = Interpolation.pow2Out.apply(displayHighScore, highScore.toFloat(), delta * 5f)
        }
        if (scoreAnimation > 0f) scoreAnimation -= delta * 3f

        backgroundHue += delta * 0.1f
        if (backgroundHue > 1f) backgroundHue -= 1f

        val bgColor = hsvToRgb(backgroundHue, 0.1f, 0.95f)
        Gdx.gl.glClearColor(bgColor.r, bgColor.g, bgColor.b, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        shapeRenderer.projectionMatrix = batch.projectionMatrix
        drawScoreBoxes()

        batch.begin()

        titleFont.draw(batch, "2048 Animals", 50f, Gdx.graphics.height - 50f)

        val scoreScale = if (scoreAnimation > 0f) 1f + scoreAnimation * 0.3f else 1f
        scoreFont.data.setScale(2f * scoreScale)
        scoreFont.draw(batch, "${displayScore.toInt()}", 520f, Gdx.graphics.height - 80f)
        scoreFont.draw(batch, "${displayHighScore.toInt()}", 670f, Gdx.graphics.height - 80f)
        scoreFont.data.setScale(2f)

        labelFont.draw(batch, "SCORE", 520f, Gdx.graphics.height - 40f)
        labelFont.draw(batch, "BEST", 670f, Gdx.graphics.height - 40f)

        labelFont.draw(batch, "HOW TO PLAY: Swipe to move tiles.", 50f, 120f)
        labelFont.draw(batch, "When two same animals touch, they evolve!", 50f, 90f)

        board.draw(batch)
        batch.end()
    }

    private fun drawScoreBoxes() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        shapeRenderer.color = Color(0.73f, 0.68f, 0.63f, 0.8f)
        drawRoundedRect(500f, Gdx.graphics.height - 120f, 140f, 80f, 8f)
        shapeRenderer.color = Color(0.73f, 0.68f, 0.63f, 0.8f)
        drawRoundedRect(650f, Gdx.graphics.height - 120f, 140f, 80f, 8f)
        shapeRenderer.end()
    }

    private fun drawRoundedRect(x: Float, y: Float, width: Float, height: Float, radius: Float) {
        shapeRenderer.rect(x + radius, y, width - 2 * radius, height)
        shapeRenderer.rect(x, y + radius, width, height - 2 * radius)
        shapeRenderer.circle(x + radius, y + radius, radius)
        shapeRenderer.circle(x + width - radius, y + radius, radius)
        shapeRenderer.circle(x + radius, y + height - radius, radius)
        shapeRenderer.circle(x + width - radius, y + height - radius, radius)
    }

    private fun hsvToRgb(h: Float, s: Float, v: Float): Color {
        val c = v * s
        val x = c * (1 - kotlin.math.abs(((h * 6) % 2) - 1))
        val m = v - c
        val (r, g, b) = when ((h * 6).toInt()) {
            0 -> Triple(c, x, 0f)
            1 -> Triple(x, c, 0f)
            2 -> Triple(0f, c, x)
            3 -> Triple(0f, x, c)
            4 -> Triple(x, 0f, c)
            else -> Triple(c, 0f, x)
        }
        return Color(r + m, g + m, b + m, 1f)
    }

    override fun pause() {
        val prefs = Gdx.app.getPreferences("PicFusePrefs")
        if (score > highScore) {
            highScore = score
            prefs.putInteger("highscore", highScore)
            prefs.flush()
        }
    }

    override fun resume() {
        val prefs = Gdx.app.getPreferences("PicFusePrefs")
        highScore = prefs.getInteger("highscore", 0)
    }

    override fun dispose() {
        batch.dispose()
        shapeRenderer.dispose()
        titleFont.dispose()
        scoreFont.dispose()
        labelFont.dispose()
        board.dispose()
    }
}
