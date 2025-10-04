package io.github.cogdanh2k3.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable
import com.badlogic.gdx.utils.viewport.ExtendViewport
import io.github.cogdanh2k3.Main
import io.github.cogdanh2k3.utils.FontUtils
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.g2d.NinePatch

class SettingScreen(val game: Main) : ScreenAdapter() {

    private val camera = OrthographicCamera()
    private val viewport = ExtendViewport(480f, 800f, camera)
    private val batch = SpriteBatch()
    private val stage = Stage(viewport, batch)

    private val background = Texture("UI/background_win.png")

    // tạo pixel trắng 1x1 thay cho file white_pixel.png
    private val whitePixel: Texture = run {
        val pm = Pixmap(1, 1, Pixmap.Format.RGBA8888)
        pm.setColor(Color.WHITE)
        pm.fill()
        val tex = Texture(pm)
        pm.dispose()
        tex
    }

    private val titleFont = FontUtils.loadCustomFont(18, Color.WHITE).apply {
        data.setScale(getScaleFactor() * 2.2f)
        color = Color(1f, 1f, 1f, 1f)
    }

    private var soundOn = true
    private var musicOn = true
    private var vibrateOn = true

    private lateinit var soundButton: TextButton
    private lateinit var musicButton: TextButton
    private lateinit var vibrateButton: TextButton
    private lateinit var backButton: TextButton

    override fun show() {
        Gdx.input.inputProcessor = stage

        val skin = Skin()

        // === Nền nút ===
        fun makeButtonBackground(color: Color): NinePatchDrawable {
            val pm = Pixmap(1, 1, Pixmap.Format.RGBA8888)
            pm.setColor(color)
            pm.fill()
            val patch = NinePatch(Texture(pm), 0, 0, 0, 0)
            pm.dispose()
            return NinePatchDrawable(patch)
        }

        // Mỗi nút 1 màu riêng, đậm hơn
        val soundStyle = TextButton.TextButtonStyle().apply {
            font = FontUtils.loadCustomFont(16, Color.WHITE)
            fontColor = Color.WHITE
            up = makeButtonBackground(Color(0.2f, 0.7f, 0.3f, 0.9f))   // xanh lá
            down = makeButtonBackground(Color(0.15f, 0.55f, 0.25f, 1f))
        }

        val musicStyle = TextButton.TextButtonStyle().apply {
            font = FontUtils.loadCustomFont(16, Color.WHITE)
            fontColor = Color.WHITE
            up = makeButtonBackground(Color(0.3f, 0.5f, 0.9f, 0.9f))   // xanh dương
            down = makeButtonBackground(Color(0.25f, 0.4f, 0.75f, 1f))
        }

        val vibrateStyle = TextButton.TextButtonStyle().apply {
            font = FontUtils.loadCustomFont(16, Color.WHITE)
            fontColor = Color.WHITE
            up = makeButtonBackground(Color(0.9f, 0.6f, 0.2f, 0.9f))   // cam
            down = makeButtonBackground(Color(0.75f, 0.45f, 0.15f, 1f))
        }

        val backStyle = TextButton.TextButtonStyle().apply {
            font = FontUtils.loadCustomFont(16, Color.WHITE)
            fontColor = Color.WHITE
            up = makeButtonBackground(Color(0.8f, 0.2f, 0.3f, 0.9f))   // đỏ
            down = makeButtonBackground(Color(0.65f, 0.15f, 0.25f, 1f))
        }

        // Các nút
        soundButton = TextButton("Sound: ON", soundStyle)
        musicButton = TextButton("Music: ON", musicStyle)
        vibrateButton = TextButton("Vibration: ON", vibrateStyle)
        backButton = TextButton("Back", backStyle)
        soundButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                soundOn = !soundOn
                soundButton.setText("Sound: ${if (soundOn) "ON" else "OFF"}")
            }
        })
        musicButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                musicOn = !musicOn
                musicButton.setText("Music: ${if (musicOn) "ON" else "OFF"}")
            }
        })
        vibrateButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                vibrateOn = !vibrateOn
                vibrateButton.setText("Vibration: ${if (vibrateOn) "ON" else "OFF"}")
            }
        })
        backButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                game.screen = MenuScreen(game)
            }
        })

        val table = Table()
        table.setFillParent(true)
        table.center()

        val pad = getResponsiveValue(20f)
        val titleLabel = Label("SETTINGS", Label.LabelStyle(titleFont, Color.WHITE))

        table.add(titleLabel).padBottom(pad * 2)
        table.row()
        table.add(soundButton).width(getResponsiveValue(260f)).height(getResponsiveValue(70f)).pad(pad)
        table.row()
        table.add(musicButton).width(getResponsiveValue(260f)).height(getResponsiveValue(70f)).pad(pad)
        table.row()
        table.add(vibrateButton).width(getResponsiveValue(260f)).height(getResponsiveValue(70f)).pad(pad)
        table.row()
        table.add(backButton).width(getResponsiveValue(220f)).height(getResponsiveValue(60f)).padTop(pad * 2)

        stage.addActor(table)
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(1f, 1f, 1f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        batch.projectionMatrix = camera.combined
        batch.begin()
        // nền gốc
        batch.draw(background, 0f, 0f, viewport.worldWidth, viewport.worldHeight)

        // phủ lớp đen trong suốt để làm mờ nền
        batch.color = Color(0f, 0f, 0f, 0.4f)
        batch.draw(whitePixel, 0f, 0f, viewport.worldWidth, viewport.worldHeight)
        batch.color = Color.WHITE
        batch.end()

        stage.act(delta)
        stage.draw()
    }

    private fun getResponsiveValue(base: Float): Float {
        return base * (viewport.worldWidth / 480f)
    }

    private fun getScaleFactor(): Float {
        val screenHeight = Gdx.graphics.height.toFloat()
        return (screenHeight / 1920f).coerceAtLeast(0.5f).coerceAtMost(1.3f)
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height, true)
    }

    override fun dispose() {
        stage.dispose()
        batch.dispose()
        background.dispose()
        whitePixel.dispose()
    }
}
