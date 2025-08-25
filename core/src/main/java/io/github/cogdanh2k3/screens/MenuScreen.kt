package io.github.cogdanh2k3.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.utils.viewport.ScreenViewport
import io.github.cogdanh2k3.Main

class MenuScreen (val game: Main) : Screen {

    private val stage = Stage(ScreenViewport())
    private val skin: Skin
    private val background: Texture

    init {
        // Tạo background bằng Pixmap
        background = createGradientBackground()

        skin = Skin()
        val font = BitmapFont()
        font.data.setScale(1.5f)
        skin.add("default-font", font)

        // Tạo style cho TextButton
        val buttonStyle = TextButton.TextButtonStyle()
        buttonStyle.font = font
        skin.add("default", buttonStyle)

        Gdx.input.inputProcessor = stage

        val table = Table()
        table.setFillParent(true)
        stage.addActor(table)

        val playButton = TextButton("Play", skin)
        val exitButton = TextButton("Exit", skin)

        playButton.addListener { _ ->
            game.screen = GameScreen(game)
            true
        }

        exitButton.addListener { _ ->
            Gdx.app.exit()
            true
        }

        table.add(playButton).pad(10f).row()
        table.add(exitButton).pad(10f).row()
    }

    private fun createGradientBackground(): Texture {
        val width = 512
        val height = 512
        val pixmap = Pixmap(width, height, Pixmap.Format.RGB888)

        for (y in 0 until height) {
            val progress = y.toFloat() / height

            // Màu từ xanh navy (0, 0, 0.5) đến xanh sky (0.5, 0.7, 1)
            val r = progress * 0.5f
            val g = progress * 0.7f
            val b = 0.5f + progress * 0.5f

            pixmap.setColor(r, g, b, 1f)
            pixmap.drawLine(0, y, width - 1, y)
        }

        val texture = Texture(pixmap)
        pixmap.dispose()
        return texture
    }

//    private fun createPatternBackground(): Texture {
//        val width = 64
//        val height = 64
//        val pixmap = Pixmap(width, height, Pixmap.Format.RGB888)
//
//        for (x in 0 until width) {
//            for (y in 0 until height) {
//                val isEven = (x / 8 + y / 8) % 2 == 0
//                if (isEven) {
//                    pixmap.setColor(0.2f, 0.2f, 0.3f, 1f) // Xám đậm
//                } else {
//                    pixmap.setColor(0.3f, 0.3f, 0.4f, 1f) // Xám nhạt
//                }
//                pixmap.drawPixel(x, y)
//            }
//        }
//
//        val texture = Texture(pixmap)
//        pixmap.dispose()
//        return texture
//    }
//
//    private fun createSolidBackground(color: Color): Texture {
//        val pixmap = Pixmap(1, 1, Pixmap.Format.RGB888)
//        pixmap.setColor(color)
//        pixmap.fill()
//        val texture = Texture(pixmap)
//        pixmap.dispose()
//        return texture
//    }

    override fun show() {}

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        game.batch.begin()
        game.batch.draw(
            background,
            0f, 0f,
            Gdx.graphics.width.toFloat(),
            Gdx.graphics.height.toFloat()
        )
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
    }
}
