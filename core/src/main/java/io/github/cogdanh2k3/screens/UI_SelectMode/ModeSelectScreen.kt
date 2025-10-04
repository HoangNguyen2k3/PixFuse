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
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.viewport.ScreenViewport
import io.github.cogdanh2k3.Main
import io.github.cogdanh2k3.Mode.EndlessMode
import io.github.cogdanh2k3.Mode.TimedMode
import io.github.cogdanh2k3.screens.GamePlay.GameScreen
import io.github.cogdanh2k3.utils.FontUtils

class ModeSelectScreen(val game: Main) : Screen {
    //private val background = Texture("titles/bg_game.png")
    private val background = Texture("UI/bg_new.png")
    private val stage = Stage(ScreenViewport())
    private val skin = Skin()

    init {
        Gdx.input.inputProcessor = stage

        // Font
        val font = FontUtils.loadCustomFont(48, Color.WHITE)
        skin.add("default-font", font)

        // Label style
        val labelStyle = Label.LabelStyle(font, Color.WHITE)
        skin.add("default", labelStyle)

        // Button styles
        val classicStyle = createButtonStyle(Color(0.1f, 0.6f, 0.9f, 0.9f), Color(0.1f, 0.7f, 1f, 0.95f), Color(0f, 0.5f, 0.8f, 0.95f), font)
        val endlessStyle = createButtonStyle(Color(0.2f, 0.8f, 0.2f, 0.9f), Color(0.3f, 0.9f, 0.3f, 0.95f), Color(0.1f, 0.7f, 0.1f, 0.95f), font)
        val timedStyle = createButtonStyle(Color(0.9f, 0.6f, 0.1f, 0.9f), Color(1f, 0.7f, 0.2f, 0.95f), Color(0.8f, 0.5f, 0f, 0.95f), font)
        val lockedStyle = createButtonStyle(
            upColor = Color(0.9f, 0.9f, 0.9f, 1f),     // trạng thái bình thường đậm hơn
            overColor = Color(0.4f, 0.4f, 0.4f, 1f),  // hover sáng hơn 1 chút
            downColor = Color(0.2f, 0.2f, 0.2f, 1f),   // nhấn đậm hơn
            font = font,
            fontColor = Color.GRAY,
            disabledFontColor = Color.DARK_GRAY
        )
        // Layout
        val root = Table()
        root.setFillParent(true)
        stage.addActor(root)

        // Nút back
        val backButton = TextButton("< Back", classicStyle).apply {
            addListener(object : ClickListener() {
                override fun clicked(event: InputEvent?, x: Float, y: Float) {
                    game.screen = MenuScreen(game)
                }
            })
        }

        // Tiêu đề
        val titleLabel = Label("SELECT MODE", labelStyle).apply { setFontScale(2.2f) }

        // Các nút chế độ
        val classicButton = TextButton("CLASSIC MODE", classicStyle).apply {
            addListener(object : ClickListener() {
                override fun clicked(event: InputEvent?, x: Float, y: Float) {
                    game.screen = LevelSelectScreen(game)
                }
            })
        }

        val endlessButton = TextButton("CHILL MODE", endlessStyle).apply {
            addListener(object : ClickListener() {
                override fun clicked(event: InputEvent?, x: Float, y: Float) {
                    game.screen = GameScreen(game, EndlessMode())
                }
            })
        }

        val timedButton = TextButton("TIMING MODE", timedStyle).apply {
            addListener(object : ClickListener() {
                override fun clicked(event: InputEvent?, x: Float, y: Float) {
                    game.screen = GameScreen(game, TimedMode())
                }
            })
        }

        val mode4Button = TextButton("MODE 4 (LOCK)", lockedStyle).apply { isDisabled = true }

        // Sắp xếp UI
        root.top().pad(40f)
        root.add(backButton).left().pad(20f).width(150f).height(80f)
        root.add(titleLabel).expandX().center().pad(10f).colspan(1)
        root.row()
        root.add().height(100f).colspan(2).row()

        root.add(classicButton).colspan(2).width(1000f).height(200f).pad(40f).row()
        root.add(endlessButton).colspan(2).width(1000f).height(200f).pad(40f).row()
        root.add(timedButton).colspan(2).width(1000f).height(200f).pad(40f).row()
        root.add(mode4Button).colspan(2).width(1000f).height(200f).pad(40f).row()
    }

    private fun createRoundedButtonTexture(width: Int, height: Int, color: Color, radius: Int = 30): Texture {
        val pixmap = Pixmap(width, height, Pixmap.Format.RGBA8888)
        pixmap.setColor(color)

        // Góc tròn
        pixmap.fillCircle(radius, radius, radius)
        pixmap.fillCircle(width - radius, radius, radius)
        pixmap.fillCircle(radius, height - radius, radius)
        pixmap.fillCircle(width - radius, height - radius, radius)

        // Kết nối các cạnh
        pixmap.fillRectangle(radius, 0, width - 2 * radius, height)
        pixmap.fillRectangle(0, radius, width, height - 2 * radius)

        val tex = Texture(pixmap)
        pixmap.dispose()
        return tex
    }

    private fun createButtonStyle(
        upColor: Color,
        overColor: Color,
        downColor: Color,
        font: BitmapFont,
        fontColor: Color = Color.WHITE,
        disabledFontColor: Color = Color.GRAY
    ): TextButton.TextButtonStyle {
        val style = TextButton.TextButtonStyle()
        style.font = font
        style.up = TextureRegionDrawable(createRoundedButtonTexture(600, 120, upColor))
        style.over = TextureRegionDrawable(createRoundedButtonTexture(600, 120, overColor))
        style.down = TextureRegionDrawable(createRoundedButtonTexture(600, 120, downColor))
        style.fontColor = fontColor
        style.disabledFontColor = disabledFontColor
        return style
    }

    override fun show() {}
    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        game.batch.begin()
        game.batch.draw(background, 0f, 0f, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
        game.batch.end()

        stage.act(delta)
        stage.draw()
    }

    override fun resize(width: Int, height: Int) { stage.viewport.update(width, height, true) }
    override fun pause() {}
    override fun resume() {}
    override fun hide() {}
    override fun dispose() {
        stage.dispose()
        skin.dispose()
        background.dispose()
    }
}
