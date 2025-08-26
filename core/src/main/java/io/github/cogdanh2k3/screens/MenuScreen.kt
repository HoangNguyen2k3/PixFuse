package io.github.cogdanh2k3.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Timer
import com.badlogic.gdx.utils.viewport.ScreenViewport
import io.github.cogdanh2k3.Main

class MenuScreen(val game: Main) : Screen {

    private val stage = Stage(ScreenViewport())
    private val skin: Skin = Skin()
    private val background = Texture("titles/background.png")
    private val logo = Texture("titles/logo.png")

    init {
        val buttonFont = BitmapFont().apply {
            data.setScale(2.5f)
            color = Color.WHITE
        }
        skin.add("default-font", buttonFont)

        val buttonTexture = createButtonTexture(300, 80, Color(0.2f, 0.2f, 0.4f, 0.8f))
        val buttonHoverTexture = createButtonTexture(300, 80, Color(0.3f, 0.3f, 0.5f, 0.9f))
        val buttonPressedTexture = createButtonTexture(300, 80, Color(0.1f, 0.1f, 0.3f, 0.9f))

        val buttonStyle = TextButton.TextButtonStyle().apply {
            font = buttonFont
            fontColor = Color.WHITE
            overFontColor = Color(1f, 1f, 0.8f, 1f) // Màu vàng nhạt khi hover
            downFontColor = Color(0.9f, 0.9f, 0.9f, 1f)

            // Thêm background cho button
            up = TextureRegionDrawable(buttonTexture)
            over = TextureRegionDrawable(buttonHoverTexture)
            down = TextureRegionDrawable(buttonPressedTexture)
        }
        skin.add("default", buttonStyle)

        Gdx.input.inputProcessor = stage

        val table = Table()
        table.setFillParent(true)
        stage.addActor(table)

        val playButton = TextButton("PLAY GAME", skin)
        val settingsButton = TextButton("SETTINGS", skin)
        val exitButton = TextButton("EXIT", skin)

        playButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                Timer.schedule(object : Timer.Task() {
                    override fun run() {
                        game.screen = GameScreen(game)
                    }
                }, 0.2f)
            }
        })

        settingsButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                // TODO: Settings screen
            }
        })

        exitButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                Timer.schedule(object : Timer.Task() {
                    override fun run() {
                        Gdx.app.exit()
                    }
                }, 0.2f)
            }
        })

        // Layout với kích thước lớn hơn
        table.add().height(200f).row()
        table.add(playButton).pad(20f).width(400f).height(100f).row() // Tăng kích thước
        table.add(settingsButton).pad(20f).width(400f).height(100f).row()
        table.add(exitButton).pad(20f).width(400f).height(100f).row()
    }

    private fun createButtonTexture(width: Int, height: Int, color: Color): Texture {
        val pixmap = Pixmap(width, height, Pixmap.Format.RGBA8888)

        // Fill background
        pixmap.setColor(color)
        pixmap.fill()

        pixmap.setColor(Color.WHITE)
        pixmap.drawRectangle(0, 0, width, height)
        pixmap.drawRectangle(1, 1, width - 2, height - 2)

        val texture = Texture(pixmap)
        pixmap.dispose()
        return texture
    }

    override fun show() {}

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        game.batch.begin()
        game.batch.draw(background, 0f, 0f, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())

        val logoWidth = Gdx.graphics.width * 0.5f
        val logoHeight = logo.height * (logoWidth / logo.width)
        val logoX = (Gdx.graphics.width - logoWidth) / 2f
        val logoY = Gdx.graphics.height - logoHeight - 50f
        game.batch.draw(logo, logoX, logoY, logoWidth, logoHeight)

        game.batch.end()

        stage.act(delta)
        stage.draw()
    }

    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height, true)
    }

    override fun pause() {}
    override fun resume() {}
    override fun hide() {}

    override fun dispose() {
        stage.dispose()
        skin.dispose()
        background.dispose()
        logo.dispose()
    }
}
