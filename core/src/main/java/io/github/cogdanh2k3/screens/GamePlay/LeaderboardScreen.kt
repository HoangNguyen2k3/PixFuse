package io.github.cogdanh2k3.screens.GamePlay

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Preferences
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.g2d.NinePatch
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.ScreenUtils
import com.badlogic.gdx.utils.viewport.FitViewport
import io.github.cogdanh2k3.DataGame.SaveManager
import io.github.cogdanh2k3.Main
import io.github.cogdanh2k3.screens.MenuScreen
import io.github.cogdanh2k3.utils.FontUtils

class LeaderboardScreen(private val game: Main) : Screen {

    // ✅ Camera, viewport và stage riêng
    private val camera = OrthographicCamera()
    private val viewport = FitViewport(720f, 1280f, camera)
    private val stage = Stage(viewport)

    private val prefs: Preferences = Gdx.app.getPreferences("LeaderboardPrefs")
    private val leaderboard: MutableList<Int> = MutableList(10) { 0 }

    private lateinit var backButton: TextButton
    private lateinit var leaderboardImage: Texture
    private lateinit var gradientTexture: Texture

    override fun show() {
        Gdx.input.inputProcessor = stage

        // Gradient xanh–cam dọc
        gradientTexture = createVerticalGradient(
            Color(0.0f, 0.6f, 1f, 1f),
            Color(1f, 0.5f, 0.2f, 1f)
        )

        leaderboardImage = Texture("UI/leaderboard.png")
        val list_high_score = SaveManager.gameSave.list_high_score

        // Load điểm
        for (i in 0 until 10) {
            leaderboard[i] = prefs.getInteger("score_$i", list_high_score.getOrElse(i) { 0 })
        }

        val table = Table()
        table.setFillParent(true)
        table.top().padTop(100f)
        stage.addActor(table)

        // === Banner ===
        val banner = Image(leaderboardImage)
        table.add(banner).size(400f, 120f).padBottom(50f).row()

        // === Header ===
        val headerStyle = Label.LabelStyle(FontUtils.loadCustomFont(36, Color.YELLOW), Color.YELLOW)
        val rankHeader = Label("RANK", headerStyle)
        val scoreHeader = Label("SCORE", headerStyle)
        rankHeader.setAlignment(Align.center)
        scoreHeader.setAlignment(Align.center)

        val scoreTable = Table()
        scoreTable.background = makeRoundedBackground(Color(0f, 0f, 0f, 0.4f))
        scoreTable.add(rankHeader).width(180f).pad(10f)
        scoreTable.add(scoreHeader).width(220f).pad(10f)
        scoreTable.row()

        val scoreStyle = Label.LabelStyle(FontUtils.loadCustomFont(30, Color.WHITE), Color.WHITE)
        for (i in 0 until 10) {
            val rank = Label("${i + 1}", scoreStyle)
            val score = Label("${leaderboard[i]}", scoreStyle)
            rank.setAlignment(Align.center)
            score.setAlignment(Align.center)

            val row = Table()
            row.add(rank).width(180f).pad(8f)
            row.add(score).width(220f).pad(8f)

            val bgColor = if (i % 2 == 0)
                Color(1f, 1f, 1f, 0.05f)
            else
                Color(1f, 1f, 1f, 0.15f)
            row.background = makeRoundedBackground(bgColor)

            scoreTable.add(row).colspan(2).pad(2f).row()
        }

        table.add(scoreTable).width(500f).padBottom(60f).row()

        // === Back button ===
        val btnStyle = TextButton.TextButtonStyle().apply {
            font = FontUtils.loadCustomFont(32, Color.WHITE)
            fontColor = Color.WHITE
            up = makeButtonBackground(Color(0.05f, 0.25f, 0.5f, 1f))
            down = makeButtonBackground(Color(0.03f, 0.2f, 0.4f, 1f))
        }

        backButton = TextButton("Back to Home", btnStyle)
        backButton.addListener {
            game.screen = MenuScreen(game)
            true
        }

        table.add(backButton).width(400f).height(90f)
    }

    private fun createVerticalGradient(top: Color, bottom: Color): Texture {
        val pixmap = Pixmap(1, 256, Pixmap.Format.RGBA8888)
        for (y in 0 until 256) {
            val t = y / 255f
            val r = bottom.r + t * (top.r - bottom.r)
            val g = bottom.g + t * (top.g - bottom.g)
            val b = bottom.b + t * (top.b - bottom.b)
            val a = bottom.a + t * (top.a - bottom.a)
            pixmap.setColor(r, g, b, a)
            pixmap.drawPixel(0, y)
        }
        val texture = Texture(pixmap)
        pixmap.dispose()
        return texture
    }

    private fun makeButtonBackground(color: Color): NinePatchDrawable {
        val pm = Pixmap(1, 1, Pixmap.Format.RGBA8888)
        pm.setColor(color)
        pm.fill()
        val patch = NinePatch(Texture(pm), 0, 0, 0, 0)
        pm.dispose()
        return NinePatchDrawable(patch)
    }

    private fun makeRoundedBackground(color: Color): NinePatchDrawable {
        val pm = Pixmap(20, 20, Pixmap.Format.RGBA8888)
        pm.setColor(color)
        pm.fillCircle(10, 10, 10)
        val patch = NinePatch(Texture(pm), 10, 10, 10, 10)
        pm.dispose()
        return NinePatchDrawable(patch)
    }

    fun addScore(newScore: Int) {
        leaderboard.add(newScore)
        leaderboard.sortDescending()
        if (leaderboard.size > 10) leaderboard.removeAt(10)
        for (i in 0 until 10) prefs.putInteger("score_$i", leaderboard[i])
        prefs.flush()
    }

    private val bgBatch = SpriteBatch()

    override fun render(delta: Float) {
        ScreenUtils.clear(0f, 0f, 0f, 1f)
        bgBatch.projectionMatrix = viewport.camera.combined
        bgBatch.begin()
        bgBatch.draw(gradientTexture, 0f, 0f, viewport.worldWidth, viewport.worldHeight)
        bgBatch.end()

        stage.act(delta)
        stage.draw()
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height, true)
    }

    override fun hide() {
        // ✅ Không thay đổi world size, chỉ clear stage
        stage.clear()
    }

    override fun pause() {}
    override fun resume() {}

    override fun dispose() {
        stage.dispose()
        leaderboardImage.dispose()
        gradientTexture.dispose()
    }
}
