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
import io.github.cogdanh2k3.Mode.TargetMode
import io.github.cogdanh2k3.Mode.TimedMode
import io.github.cogdanh2k3.screens.GamePlay.GameScreen
import io.github.cogdanh2k3.utils.FontUtils

class ModeSelectScreen(val game: Main) : Screen {

    private val stage = Stage(ScreenViewport())
    private val skin = Skin()

    init {
        Gdx.input.inputProcessor = stage

        // Font
/*        val font = BitmapFont().apply {
            data.setScale(2.0f)
            color = Color.WHITE
        }*/
        val font = FontUtils.loadCustomFont(48, Color.WHITE)
        skin.add("default-font", font)

        // Label style
        val labelStyle = Label.LabelStyle(font, Color.WHITE)
        skin.add("default", labelStyle)

        // Button textures
        val normal = createButtonTexture(600, 120, Color(0.2f, 0.2f, 0.4f, 0.9f))
        val hover = createButtonTexture(600, 120, Color(0.3f, 0.3f, 0.5f, 0.95f))
        val pressed = createButtonTexture(600, 120, Color(0.1f, 0.1f, 0.3f, 0.95f))
        val locked = createButtonTexture(600, 120, Color(0.15f, 0.15f, 0.15f, 0.8f))

        // Style cho button
        val style = TextButton.TextButtonStyle().apply {
            this.font = font
            up = TextureRegionDrawable(normal)
            over = TextureRegionDrawable(hover)
            down = TextureRegionDrawable(pressed)
            fontColor = Color.WHITE
        }
        val lockedStyle = TextButton.TextButtonStyle().apply {
            this.font = font
            fontColor = Color.GRAY
            up = TextureRegionDrawable(locked)
            disabledFontColor = Color.DARK_GRAY
        }

        skin.add("default", style)

        // Layout
        val root = Table()
        root.setFillParent(true)
        stage.addActor(root)

        // Nút back
        val backButton = TextButton("< Back", style).apply {
            //label.setFontScale(1.5f)
            addListener(object : ClickListener() {
                override fun clicked(event: InputEvent?, x: Float, y: Float) {
                    game.screen = MenuScreen(game)
                }
            })
        }

        // Tiêu đề
        val titleLabel = Label("SELECT MODE", labelStyle).apply {
            setFontScale(2.2f)
        }

        // Các nút chế độ
        val classicButton = TextButton("CLASSIC MODE", style).apply {
            addListener(object : ClickListener() {
                override fun clicked(event: InputEvent?, x: Float, y: Float) {
                    //game.screen = GameScreen(game,TargetMode(targetValue = 128, targetName = "Pokemon")) // TODO: truyền mode Classic
                    game.screen = LevelSelectScreen(game)
                }
            })
        }

        val endlessButton = TextButton("CHILL MODE", style).apply {
            addListener(object : ClickListener() {
                override fun clicked(event: InputEvent?, x: Float, y: Float) {
                    game.screen = GameScreen(game, EndlessMode()) // TODO: truyền mode Endless
                }
            })
        }

        val mode3Button = TextButton("TIMING MODE", style).apply {
            addListener(object : ClickListener() {
                override fun clicked(event: InputEvent?, x: Float, y: Float) {
                    game.screen = GameScreen(game, TimedMode()) // TODO: truyền mode Endless
                }
            })
        }
        val mode4Button = TextButton("MODE 4 (LOCK)", lockedStyle).apply { isDisabled = true }

        // Sắp xếp UI
        root.top().pad(40f)
        // Hàng đầu tiên
        root.add(backButton).left().pad(20f).width(150f).height(80f) // nút nhỏ gọn
        root.add(titleLabel).expandX().center().pad(10f).colspan(1)
        root.row()
// Khoảng trống
        root.add().height(50f).colspan(2).row()

// Các nút: chiếm cả 2 cột
        root.add(classicButton).colspan(2).width(900f).height(180f).pad(20f).row()
        root.add(endlessButton).colspan(2).width(900f).height(180f).pad(20f).row()
        root.add(mode3Button).colspan(2).width(900f).height(180f).pad(20f).row()
        root.add(mode4Button).colspan(2).width(900f).height(180f).pad(20f).row()
    }

    private fun createButtonTexture(width: Int, height: Int, color: Color): Texture {
        val pixmap = Pixmap(width, height, Pixmap.Format.RGBA8888)
        pixmap.setColor(color)
        pixmap.fill()
        val tex = Texture(pixmap)
        pixmap.dispose()
        return tex
    }

    override fun show() {}
    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

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
    }
}
