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
import io.github.cogdanh2k3.DataGame.LevelData
import io.github.cogdanh2k3.Main
import io.github.cogdanh2k3.Mode.GameMode
import io.github.cogdanh2k3.Mode.TargetMode
import io.github.cogdanh2k3.Mode.TimedMode
import io.github.cogdanh2k3.game.Board
import io.github.cogdanh2k3.game.GameManager
import io.github.cogdanh2k3.screens.WinScreen
import io.github.cogdanh2k3.utils.FontUtils
import io.github.cogdanh2k3.utils.InputHandler
import kotlin.math.abs

class GameScreen(val game: Main, val mode: GameMode, val levelData: LevelData? = null) : ScreenAdapter() {
    public var BOARD_SIZE = if(levelData==null){4}else{levelData.sizeBoard}
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
    private var board = Board(BOARD_SIZE)
    private val manager = GameManager(board,mode,levelData)

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
        manager.InitData()
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
            (availableWidth - 5 * 8f) / BOARD_SIZE, // 5 padding cho 4 tiles
            (availableHeight - 5 * 8f) / BOARD_SIZE
        )

        board.tileSize = maxTileSize.coerceAtLeast(60f)
        board.padding = board.tileSize * 0.08f

        // Căn giữa board
        val boardWidth = BOARD_SIZE * board.tileSize + 3 * board.padding
        val boardHeight = BOARD_SIZE * board.tileSize + 3 * board.padding

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
                        game.screen = WinScreen(game, score,mode,levelData)
                    } else {
                        game.screen = LoseScreen(game, score,mode)
                    }
                }
            }
        }
        if (mode is TimedMode) {
            mode.update(delta)   // <-- giảm thời gian mỗi frame
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
        drawPauseButton()   // <--- thêm dòng này
        drawBoosterButtons();
        shapeRenderer.end()

        // ===== Draw text, board =====
        batch.begin()
        drawPauseButtonText()
        drawHeaderText()       // SCORE, BEST
        //drawScoreText()        // số điểm
        drawTargetText()       // target hoặc vô cực
        drawPauseButtonText()   // <--- và dòng này
        drawBoosterButtonText()
        board.draw(batch)      // grid
        drawInstructions()     // text hướng dẫn
        drawEndGameText()      // Win/Lose
        batch.end()
    }
    // ======= HEADER (SCORE & BEST) =======
    private fun drawHeader() {
        val headerHeight = getResponsiveValue(100f)
        val scoreBoxWidth = getResponsiveValue(120f)
        val scoreBoxHeight = headerHeight - getResponsiveValue(20f)
        val spacing = getResponsiveValue(20f)

        val totalWidth = scoreBoxWidth * 2 + spacing
        val startX = (viewport.worldWidth - totalWidth) / 2f
        val y = viewport.worldHeight - headerHeight

        // SCORE box
        shapeRenderer.color = Color(0.73f, 0.68f, 0.63f, 0.95f)
        drawRoundedRect(startX, y, scoreBoxWidth, scoreBoxHeight, 12f)

        // BEST box
        shapeRenderer.color = Color(0.85f, 0.72f, 0.3f, 0.95f)
        drawRoundedRect(startX + scoreBoxWidth + spacing, y, scoreBoxWidth, scoreBoxHeight, 12f)
    }

    private fun drawHeaderText() {
        val headerHeight = getResponsiveValue(100f)
        val scoreBoxWidth = getResponsiveValue(120f)
        val scoreBoxHeight = headerHeight - getResponsiveValue(20f)
        val spacing = getResponsiveValue(20f)

        val totalWidth = scoreBoxWidth * 2 + spacing
        val startX = (viewport.worldWidth - totalWidth) / 2f
        val y = viewport.worldHeight - headerHeight

        // SCORE label
        val scoreLabel = "SCORE"
        val scoreValue = displayScore.toInt().toString()

        val labelLayout = GlyphLayout(scoreFont, scoreLabel)
        val valueLayout = GlyphLayout(scoreFont, scoreValue)

        val scoreCenterX = startX + scoreBoxWidth / 2f
        val scoreCenterY = y + scoreBoxHeight / 2f

        // Vẽ chữ "SCORE" (ở trên)
        val scoreLabelY = scoreCenterY + valueLayout.height / 2f + getResponsiveValue(10f)
        scoreFont.draw(batch, labelLayout, scoreCenterX - labelLayout.width / 2f, scoreLabelY)

        // Vẽ số điểm (ở dưới)
        val scoreValueY = scoreCenterY - labelLayout.height / 2f
        scoreFont.draw(batch, valueLayout, scoreCenterX - valueLayout.width / 2f, scoreValueY)


        // BEST label
        val bestLabel = "BEST"
        val bestValue = displayHighScore.toInt().toString()

        val bestLabelLayout = GlyphLayout(scoreFont, bestLabel)
        val bestValueLayout = GlyphLayout(scoreFont, bestValue)

        val bestCenterX = startX + scoreBoxWidth + spacing + scoreBoxWidth / 2f
        val bestCenterY = y + scoreBoxHeight / 2f

        // Vẽ chữ "BEST" (ở trên)
        val bestLabelY = bestCenterY + bestValueLayout.height / 2f + getResponsiveValue(10f)
        scoreFont.draw(batch, bestLabelLayout, bestCenterX - bestLabelLayout.width / 2f, bestLabelY)

        // Vẽ số best score (ở dưới)
        val bestValueY = bestCenterY - bestLabelLayout.height / 2f
        scoreFont.draw(batch, bestValueLayout, bestCenterX - bestValueLayout.width / 2f, bestValueY)
    }


    private fun drawPauseButton() {
        val pauseButtonWidth = getResponsiveValue(70f)   // nhỏ lại
        val pauseButtonHeight = getResponsiveValue(30f)  // nhỏ lại
        val margin = getResponsiveValue(15f)            // cách mép
        val pauseButtonX = viewport.worldWidth - margin - pauseButtonWidth
        val pauseButtonY = viewport.worldHeight - margin - pauseButtonHeight

        // Nền
        shapeRenderer.color = Color(0.4f, 0.4f, 0.4f, 0.95f)
        drawRoundedRect(pauseButtonX, pauseButtonY, pauseButtonWidth, pauseButtonHeight, 6f)
    }

    private fun drawPauseButtonText() {
        val pauseButtonWidth = getResponsiveValue(70f)
        val pauseButtonHeight = getResponsiveValue(30f)
        val margin = getResponsiveValue(15f)
        val pauseButtonX = viewport.worldWidth - margin - pauseButtonWidth
        val pauseButtonY = viewport.worldHeight - margin - pauseButtonHeight

        val buttonText = "PAUSE"   // dùng biểu tượng pause
        val layout = GlyphLayout(buttonFont, buttonText)
        val textX = pauseButtonX + (pauseButtonWidth - layout.width) / 2f
        val textY = pauseButtonY + (pauseButtonHeight + layout.height) / 2f
        buttonFont.draw(batch, layout, textX, textY)
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

/*    // Khai báo texture ở class
    private lateinit var boosterTexture: Texture

// Trong constructor hoặc create():
    boosterTexture = Texture(Gdx.files.internal("button.png"))

    private fun drawBoosterButtons(batch: SpriteBatch) {
        val radius = getResponsiveValue(40f)
        val marginBottom = getResponsiveValue(20f)
        val spacing = getResponsiveValue(30f)

        val totalWidth = radius * 2 * 3 + spacing * 2
        val startX = (viewport.worldWidth - totalWidth) / 2f
        val centerY = marginBottom + radius

        // Kích thước vẽ (ảnh vuông nên lấy đường kính = radius*2)
        val size = radius * 2f

        // Booster 1
        batch.draw(boosterTexture, startX, centerY - radius, size, size)

        // Booster 2
        batch.draw(boosterTexture, startX + radius * 2 + spacing, centerY - radius, size, size)

        // Booster 3
        batch.draw(boosterTexture, startX + radius * 4 + spacing * 2, centerY - radius, size, size)
    }*/
    private fun drawBoosterButtons() {
        val radius = getResponsiveValue(40f)
        val marginBottom = getResponsiveValue(20f)
        val spacing = getResponsiveValue(30f)

        val totalWidth = radius * 2 * 3 + spacing * 2
        val startX = (viewport.worldWidth - totalWidth) / 2f
        val centerY = marginBottom + radius

        // Booster 1
        shapeRenderer.color = Color(0.9f, 0.5f, 0.4f, 0.9f)
        shapeRenderer.circle(startX + radius, centerY, radius)

        // Booster 2
        shapeRenderer.color = Color(0.4f, 0.8f, 0.5f, 0.9f)
        shapeRenderer.circle(startX + radius * 3 + spacing, centerY, radius)

        // Booster 3
        shapeRenderer.color = Color(0.4f, 0.6f, 0.9f, 0.9f)
        shapeRenderer.circle(startX + radius * 5 + spacing * 2, centerY, radius)
    }
    private fun drawBoosterButtonText() {
        val radius = getResponsiveValue(40f)
        val marginBottom = getResponsiveValue(20f)
        val spacing = getResponsiveValue(30f)

        val totalWidth = radius * 2 * 3 + spacing * 2
        val startX = (viewport.worldWidth - totalWidth) / 2f
        val centerY = marginBottom + radius

        val labels = listOf("B1", "B2", "B3")
        val positions = listOf(
            startX + radius,
            startX + radius * 3 + spacing,
            startX + radius * 5 + spacing * 2
        )

        for (i in 0..2) {
            val text = labels[i]
            val layout = GlyphLayout(buttonFont, text)
            val textX = positions[i] - layout.width / 2f
            val textY = centerY + layout.height / 2f
            buttonFont.draw(batch, layout, textX, textY)
        }
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
