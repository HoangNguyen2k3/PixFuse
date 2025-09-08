package io.github.cogdanh2k3.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.input.GestureDetector
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.utils.viewport.FitViewport
import io.github.cogdanh2k3.Main
import io.github.cogdanh2k3.game.Board
import io.github.cogdanh2k3.game.GameManager
import io.github.cogdanh2k3.utils.InputHandler

class GameScreen(val game: Main) : ScreenAdapter() {

    private val camera = OrthographicCamera()
    private val viewport = FitViewport(800f, 600f, camera) // giữ tỉ lệ 800x600
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

        camera.update()
        batch.projectionMatrix = camera.combined
        shapeRenderer.projectionMatrix = camera.combined

        // Draw UI
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        drawScoreBoxes()
        shapeRenderer.end()

        batch.begin()

        // Tiêu đề
        titleFont.draw(batch, "2048 Animals", viewport.worldWidth / 2 - 150f, viewport.worldHeight - 30f)

        // Điểm số
        val scoreScale = if (scoreAnimation > 0f) 1f + scoreAnimation * 0.3f else 1f
        scoreFont.data.setScale(2f * scoreScale)
        scoreFont.draw(batch, "${displayScore.toInt()}", viewport.worldWidth / 2 - 120f, viewport.worldHeight - 70f)
        scoreFont.draw(batch, "${displayHighScore.toInt()}", viewport.worldWidth / 2 + 40f, viewport.worldHeight - 70f)
        scoreFont.data.setScale(2f)

        labelFont.draw(batch, "SCORE", viewport.worldWidth / 2 - 120f, viewport.worldHeight - 40f)
        labelFont.draw(batch, "BEST", viewport.worldWidth / 2 + 40f, viewport.worldHeight - 40f)

        // Hướng dẫn
        labelFont.draw(batch, "HOW TO PLAY: Swipe to move tiles.", 50f, 60f)
        labelFont.draw(batch, "When two same animals touch, they evolve!", 50f, 35f)

        // Board game ở giữa màn hình
        board.setPosition(
            (viewport.worldWidth - board.pixelSize) / 2,
            (viewport.worldHeight - board.pixelSize) / 2
        )
        board.draw(batch)

        batch.end()
    }

    private fun drawScoreBoxes() {
        // SCORE
        shapeRenderer.color = Color(0.73f, 0.68f, 0.63f, 0.8f)
        drawRoundedRect(viewport.worldWidth / 2 - 150f, viewport.worldHeight - 110f, 100f, 70f, 8f)

        // BEST
        shapeRenderer.color = Color(0.73f, 0.68f, 0.63f, 0.8f)
        drawRoundedRect(viewport.worldWidth / 2 + 10f, viewport.worldHeight - 110f, 100f, 70f, 8f)
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

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height, true)
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
