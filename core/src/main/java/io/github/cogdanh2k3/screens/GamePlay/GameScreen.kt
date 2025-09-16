package io.github.cogdanh2k3.screens.GamePlay

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.GlyphLayout
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.input.GestureDetector
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.viewport.ExtendViewport
import io.github.cogdanh2k3.Main
import io.github.cogdanh2k3.Mode.GameMode
import io.github.cogdanh2k3.Mode.TargetMode
import io.github.cogdanh2k3.game.Board
import io.github.cogdanh2k3.game.GameManager
import io.github.cogdanh2k3.utils.FontUtils
import io.github.cogdanh2k3.utils.InputHandler
import kotlin.math.abs

class GameScreen(val game: Main, val mode: GameMode) : ScreenAdapter() {

    private val camera = OrthographicCamera()
    // Sử dụng ExtendViewport để tự động scale theo tỷ lệ màn hình
    // Min size cho portrait, max size cho landscape
    private val viewport = ExtendViewport(480f, 800f, 600f, 1000f, camera)
    private val batch = SpriteBatch()
    private val shapeRenderer = ShapeRenderer()

    // Fonts với sizing responsive
    private val titleFont = FontUtils.loadCustomFont(12, Color.WHITE).apply {
        data.setScale(getScaleFactor() * 2.0f)
        color = Color(0.45f, 0.42f, 0.39f, 1f)
    }
/*    private val titleFont = BitmapFont().apply {
        data.setScale(getScaleFactor() * 2.0f)
        color = Color(0.45f, 0.42f, 0.39f, 1f)
    }*/
    private val scoreFont = FontUtils.loadCustomFont(12, Color.WHITE).apply {
        data.setScale(getScaleFactor() * 1.5f)
        color = Color.WHITE
    }
    private val labelFont = FontUtils.loadCustomFont(12, Color.WHITE).apply {
        data.setScale(getScaleFactor() * 0.8f)
        color = Color(0.7f, 0.7f, 0.7f, 1f)
    }
    private val buttonFont = FontUtils.loadCustomFont(12, Color.WHITE).apply {
        data.setScale(getScaleFactor() * 1.0f)
        color = Color.WHITE
    }
    private val instructionFont = FontUtils.loadCustomFont(12, Color.WHITE).apply {
        data.setScale(getScaleFactor() * 0.7f)
        color = Color(0.6f, 0.6f, 0.6f, 1f)
    }

    // Game objects
    private val board = Board(4)
    private val manager = GameManager(board,mode)

    private var score = 0
    private var highScore = 0
    private var displayScore = 0f
    private var displayHighScore = 0f

    private var scoreAnimation = 0f
    private var backgroundHue = 0f

    var showEndText = false
    var endText = ""
    var endTime = 0f

    init {
        manager.spawnTile()
        manager.spawnTile()

        val gestureDetector = GestureDetector(InputHandler(manager, this))
        Gdx.input.inputProcessor = gestureDetector

        val prefs = Gdx.app.getPreferences("PicFusePrefs")
        highScore = prefs.getInteger("highscore", 0)
        displayHighScore = highScore.toFloat()

        setupBoard()
    }

    private fun getScaleFactor(): Float {
        // Scale factor dựa trên kích thước màn hình
        val screenHeight = Gdx.graphics.height.toFloat()
        return (screenHeight / 1920f).coerceAtLeast(0.5f).coerceAtMost(1.2f)
    }

    private fun setupBoard() {
        // Tính toán kích thước và vị trí board dựa trên viewport
        val availableWidth = viewport.worldWidth * 0.9f
        val availableHeight = viewport.worldHeight * 0.5f

        // Kích thước tile sao cho board vừa với màn hình
        val maxTileSize = minOf(
            (availableWidth - 5 * 8f) / 4f, // 5 padding cho 4 tiles
            (availableHeight - 5 * 8f) / 4f
        )

        board.tileSize = maxTileSize.coerceAtLeast(60f)
        board.padding = board.tileSize * 0.08f

        // Căn giữa board
        val boardWidth = 4 * board.tileSize + 3 * board.padding
        val boardHeight = 4 * board.tileSize + 3 * board.padding

        board.x = (viewport.worldWidth - boardWidth) / 2f
        board.y = (viewport.worldHeight - boardHeight) / 2f - 50f // Offset xuống một chút
    }

    override fun render(delta: Float) {
        if(!manager.hasWon && !manager.hasLost){
            handleInput()
            updateGame(delta)
        }else{
            if (!showEndText) {
                if (manager.hasWon) {
                    showEndText = true
                    endText = "YOU WIN"
                    endTime = 0f
                } else if (manager.hasLost) {
                    showEndText = true
                    endText = "YOU LOSE"
                    endTime = 0f
                }
            } else {
                endTime += Gdx.graphics.deltaTime
                if (endTime >= 3f) {
                    if (manager.hasWon) {
                        game.screen = WinScreen(game, score,mode)
                    } else {
                        game.screen = LoseScreen(game, score,mode)
                    }
                }
            }
        }
        drawEverything()
    }

    private fun handleInput() {
        if (Gdx.input.justTouched()) {
            val touchPoint = Vector3(Gdx.input.x.toFloat(), Gdx.input.y.toFloat(), 0f)
            viewport.unproject(touchPoint)

            // Check if pause button was clicked
            val pauseButtonX = viewport.worldWidth - getResponsiveValue(40f) - getResponsiveValue(80f)
            val pauseButtonY = viewport.worldHeight - getResponsiveValue(50f) - getResponsiveValue(15f)
            val pauseButtonWidth = getResponsiveValue(80f)
            val pauseButtonHeight = getResponsiveValue(35f)

            if (isPointInRect(touchPoint.x, touchPoint.y,
                    pauseButtonX, pauseButtonY, pauseButtonWidth, pauseButtonHeight)) {
                pauseGame()
            }
        }
    }

    private fun getResponsiveValue(baseValue: Float): Float {
        return baseValue * (viewport.worldWidth / 480f)
    }

    private fun updateGame(delta: Float) {
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

    }

    private fun drawEverything() {
        // Clear screen
        val bgColor = hsvToRgb(backgroundHue, 0.08f, 0.96f)
        Gdx.gl.glClearColor(bgColor.r, bgColor.g, bgColor.b, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        camera.update()
        batch.projectionMatrix = camera.combined
        shapeRenderer.projectionMatrix = camera.combined

        // ===== Draw shapes =====
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        drawHeader()        // Box điểm số
        drawTargetBox()     // Box target
        shapeRenderer.end()

        // ===== Draw text, board =====
        batch.begin()
        drawPauseButtonText()
        drawHeaderText()       // SCORE, BEST
        drawScoreText()        // số điểm
        drawTargetText()       // target hoặc vô cực
        board.draw(batch)      // grid
        drawInstructions()     // text hướng dẫn
        drawEndGameText()      // Win/Lose
        batch.end()
    }
    private fun drawHeader() {
        val headerHeight = getResponsiveValue(100f)
        val scoreBoxWidth = getResponsiveValue(120f)
        val scoreBoxHeight = headerHeight - getResponsiveValue(20f)
        val spacing = getResponsiveValue(20f)

        val totalWidth = scoreBoxWidth * 2 + spacing
        val startX = (viewport.worldWidth - totalWidth) / 2f
        val y = viewport.worldHeight - headerHeight

        // SCORE box
        shapeRenderer.color = Color(0.73f, 0.68f, 0.63f, 0.85f)
        drawRoundedRect(startX, y, scoreBoxWidth, scoreBoxHeight, 10f)

        // BEST box
        shapeRenderer.color = Color(0.85f, 0.72f, 0.3f, 0.85f)
        drawRoundedRect(startX + scoreBoxWidth + spacing, y, scoreBoxWidth, scoreBoxHeight, 10f)
    }

    private fun drawHeaderText() {
        val headerHeight = getResponsiveValue(100f)
        val scoreBoxWidth = getResponsiveValue(120f)
        val scoreBoxHeight = headerHeight - getResponsiveValue(20f)
        val spacing = getResponsiveValue(20f)

        val totalWidth = scoreBoxWidth * 2 + spacing
        val startX = (viewport.worldWidth - totalWidth) / 2f
        val y = viewport.worldHeight - headerHeight

        labelFont.draw(batch, "SCORE", startX + 15f, y + scoreBoxHeight - 10f)
        labelFont.draw(batch, "BEST", startX + scoreBoxWidth + spacing + 15f, y + scoreBoxHeight - 10f)
    }
    private fun drawTargetBox() {
        val targetBoxWidth = getResponsiveValue(260f)
        val targetBoxHeight = getResponsiveValue(70f)
        val x = (viewport.worldWidth - targetBoxWidth) / 2f
        val y = viewport.worldHeight - getResponsiveValue(200f)

        shapeRenderer.color = Color(0.4f, 0.6f, 0.9f, 0.85f)
        drawRoundedRect(x, y, targetBoxWidth, targetBoxHeight, 8f)
    }

    private fun drawTargetText() {
        val targetBoxWidth = getResponsiveValue(260f)
        val targetBoxHeight = getResponsiveValue(70f)
        val x = (viewport.worldWidth - targetBoxWidth) / 2f
        val y = viewport.worldHeight - getResponsiveValue(200f)

        val targetDesc = manager.mode.getTargetDescription()

        labelFont.draw(batch, "TARGET", x + 15f, y + targetBoxHeight - 10f)
        scoreFont.draw(batch, targetDesc, x + 15f, y + targetBoxHeight / 2f)
    }


    private fun drawEndGameText() {
        if (showEndText) {
            val progress = (endTime / 1f).coerceAtMost(1f)
            val scale = 1f + progress * 4f

            // áp dụng scale tạm thời
            scoreFont.data.setScale(scale)
            scoreFont.color = Color.WHITE

            // layout đã tính đúng với scale hiện tại
            val layout = GlyphLayout(scoreFont, endText)
            val textX = (viewport.worldWidth - layout.width) / 2f
            val textY = (viewport.worldHeight + layout.height) / 2f

            scoreFont.draw(batch, endText, textX, textY)

            // reset lại scale về mặc định
            scoreFont.data.setScale(getScaleFactor() * 1.5f)
        }
    }

/*    private fun drawHeader() {
        val headerHeight = getResponsiveValue(140f)
        // Header background
        shapeRenderer.color = Color(1f, 1f, 1f, 0.1f)
        shapeRenderer.rect(0f, viewport.worldHeight - headerHeight, viewport.worldWidth, headerHeight)
    }*/


    private fun drawScoreBoxes() {
        val scoreBoxWidth = getResponsiveValue(110f)
        val scoreBoxHeight = getResponsiveValue(80f)
        val scoreBoxSpacing = getResponsiveValue(20f)
        val scoreBoxY = viewport.worldHeight - getResponsiveValue(50f) - getResponsiveValue(90f)

        val totalWidth = scoreBoxWidth * 2 + scoreBoxSpacing
        val startX = (viewport.worldWidth - totalWidth) / 2f

        // Score box
        shapeRenderer.color = Color(0.73f, 0.68f, 0.63f, 0.85f)
        drawRoundedRect(startX, scoreBoxY, scoreBoxWidth, scoreBoxHeight, 8f)

        // Best score box
        shapeRenderer.color = Color(0.85f, 0.72f, 0.3f, 0.85f)
        drawRoundedRect(startX + scoreBoxWidth + scoreBoxSpacing, scoreBoxY, scoreBoxWidth, scoreBoxHeight, 8f)
    }

    private fun drawPauseButton() {
        val pauseButtonWidth = getResponsiveValue(80f)
        val pauseButtonHeight = getResponsiveValue(35f)
        val pauseButtonX = viewport.worldWidth - getResponsiveValue(40f) - pauseButtonWidth
        val pauseButtonY = viewport.worldHeight - getResponsiveValue(50f) - getResponsiveValue(15f)

        shapeRenderer.color = Color(0.5f, 0.5f, 0.5f, 0.9f)
        drawRoundedRect(pauseButtonX, pauseButtonY, pauseButtonWidth, pauseButtonHeight, 6f)
    }

/*    private fun drawHeaderText() {
        // Title - centered
        val titleText = "2048 Animals"
        val titleY = viewport.worldHeight - getResponsiveValue(50f)
        // Better centering calculation
        val titleX = viewport.worldWidth / 2f - getResponsiveValue(120f)
        titleFont.draw(batch, titleText, titleX, titleY)
    }*/

    private fun drawScoreText() {
        val scoreBoxWidth = getResponsiveValue(110f)
        val scoreBoxHeight = getResponsiveValue(80f)
        val scoreBoxSpacing = getResponsiveValue(20f)
        val scoreBoxY = viewport.worldHeight - getResponsiveValue(50f) - getResponsiveValue(90f)

        val totalWidth = scoreBoxWidth * 2 + scoreBoxSpacing
        val startX = (viewport.worldWidth - totalWidth) / 2f

        // Score labels
        labelFont.draw(batch, "SCORE", startX + 15f, scoreBoxY + scoreBoxHeight - 15f)
        labelFont.draw(batch, "BEST", startX + scoreBoxWidth + scoreBoxSpacing + 15f, scoreBoxY + scoreBoxHeight - 15f)

        // Score values with animation
        val scoreScale = if (scoreAnimation > 0f) 1f + scoreAnimation * 0.2f else 1f
        val currentScale = getScaleFactor() * 1.5f
        scoreFont.data.setScale(currentScale * scoreScale)

        // Center the score text in boxes
        val scoreText = "${displayScore.toInt()}"
        val bestText = "${displayHighScore.toInt()}"

        scoreFont.draw(batch, scoreText, startX + 15f, scoreBoxY + 35f)
        scoreFont.draw(batch, bestText, startX + scoreBoxWidth + scoreBoxSpacing + 15f, scoreBoxY + 35f)

        scoreFont.data.setScale(currentScale) // Reset scale
    }

    private fun drawPauseButtonText() {
        val pauseButtonWidth = getResponsiveValue(80f)
        val pauseButtonHeight = getResponsiveValue(35f)
        val pauseButtonX = viewport.worldWidth - getResponsiveValue(40f) - pauseButtonWidth
        val pauseButtonY = viewport.worldHeight - getResponsiveValue(50f) - getResponsiveValue(15f)

        val buttonText = "PAUSE"
        val textX = pauseButtonX + pauseButtonWidth / 2f - getResponsiveValue(20f)
        val textY = pauseButtonY + pauseButtonHeight / 2f + 6f
        buttonFont.draw(batch, buttonText, textX, textY)
    }

/*
    private fun drawInstructions() {
        val instructionY = getResponsiveValue(80f)
        val instructionLineSpacing = getResponsiveValue(20f)
        val sideMargin = getResponsiveValue(40f)

        // Instructions at bottom of screen
        instructionFont.draw(batch, "2048 ANIMALS", sideMargin, instructionY + instructionLineSpacing * 2)
        instructionFont.draw(batch, "Swipe to move tiles • Match same animals to evolve!", sideMargin, instructionY + instructionLineSpacing)
        instructionFont.draw(batch, "Try to reach the highest evolution possible!", sideMargin, instructionY)
    }
*/
private fun drawInstructions() {
    val margin = getResponsiveValue(40f)
    val lineSpacing = getResponsiveValue(25f)

    val baseY = getResponsiveValue(60f) // gần đáy màn
    instructionFont.draw(batch, "2048 ANIMALS", margin, baseY + lineSpacing * 2)
    instructionFont.draw(batch, "Swipe to move tiles • Match same animals to evolve!", margin, baseY + lineSpacing)
    instructionFont.draw(batch, "Try to reach the target!", margin, baseY)
}

    private fun pauseGame() {
        // Save current game state before pausing
        val prefs = Gdx.app.getPreferences("PicFusePrefs")
        if (score > highScore) {
            highScore = score
            prefs.putInteger("highscore", highScore)
            prefs.flush()
        }

        // Switch to pause screen
        game.screen = PauseScreen(game, this)
    }

    private fun isPointInRect(x: Float, y: Float, rectX: Float, rectY: Float, width: Float, height: Float): Boolean {
        return x >= rectX && x <= rectX + width && y >= rectY && y <= rectY + height
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
        val x = c * (1 - abs(((h * 6) % 2) - 1))
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
        // Cập nhật lại layout khi resize
        setupBoard()

        // Cập nhật lại font scaling
        val newScale = getScaleFactor()
        titleFont.data.setScale(newScale * 2.0f)
        scoreFont.data.setScale(newScale * 1.5f)
        labelFont.data.setScale(newScale * 0.8f)
        buttonFont.data.setScale(newScale * 1.0f)
        instructionFont.data.setScale(newScale * 0.7f)
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
        buttonFont.dispose()
        instructionFont.dispose()
        board.dispose()
    }
}
