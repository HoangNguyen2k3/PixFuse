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
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.viewport.ScreenViewport
import io.github.cogdanh2k3.Main
import io.github.cogdanh2k3.Mode.EndlessMode
import io.github.cogdanh2k3.Mode.TargetMode
import io.github.cogdanh2k3.game.DataBG
import io.github.cogdanh2k3.game.LevelManager
import io.github.cogdanh2k3.screens.GamePlay.GameScreen

class LevelSelectScreen(val game: Main) : Screen {
    private val stage = Stage(ScreenViewport())
    private val skin = Skin()
    private val dataBG = DataBG()
    private lateinit var scrollPane: ScrollPane   // giữ tham chiếu tới scrollPane

    init {
        Gdx.input.inputProcessor = stage
        LevelManager.loadLevels()

        // Font
        val font = BitmapFont().apply { data.setScale(2f) }
        skin.add("default-font", font)

        // Button styles
        val unlockedTex = createButtonTexture(160, 160, Color(0.2f, 0.5f, 0.2f, 0.9f))
        val lockedTex = createButtonTexture(160, 160, Color(0.3f, 0.3f, 0.3f, 0.9f))
        val unlockedStyle = TextButton.TextButtonStyle().apply {
            this.font = font
            up = TextureRegionDrawable(unlockedTex)
        }
        val lockedStyle = TextButton.TextButtonStyle().apply {
            this.font = font
            fontColor = Color.DARK_GRAY
            up = TextureRegionDrawable(lockedTex)
        }
        skin.add("unlocked", unlockedStyle)
        skin.add("locked", lockedStyle)

        // Root table
        val root = Table()
        root.setFillParent(true)
        stage.addActor(root)

        // Scroll container
        val worldsTable = Table()
        for (world in 1..5) {
            worldsTable.add(createWorldTable(world)).pad(50f)
        }
        scrollPane = ScrollPane(worldsTable).apply {
            setScrollingDisabled(false, true) // chỉ cuộn ngang
            setSmoothScrolling(true)
        }

        root.add(scrollPane).expand().fill()
    }

    // Tính world hiện tại dựa trên scrollX
    private val currentWorld: Int
        get() {
            val pageWidth = Gdx.graphics.width.toFloat()
            val worldIndex = (scrollPane.scrollX / pageWidth).toInt()
            return (worldIndex + 1).coerceIn(1, dataBG.worldBackgrounds.size)
        }

    private fun createWorldTable(world: Int): Table {
        val table = Table()
        val levels = LevelManager.levels.filter { it.world == world }
        var idx = 0

        val rows = 3
        val cols = 5
        for (r in 0 until rows) {
            table.row().pad(20f)
            for (c in 0 until cols) {
                if (idx < levels.size) {
                    val level = levels[idx]
                    val styleName = if (level.unlocked) "unlocked" else "locked"
                    val btn = TextButton(level.id.toString(), skin, styleName)

                    if (level.unlocked) {
                        btn.addListener(object : ClickListener() {
                            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                                println("Play level ${level.id} in world $world")
                                game.screen = GameScreen(game, TargetMode(64,"Tiger"),level.id)
                            }
                        })
                    }

                    table.add(btn).width(160f).height(160f).pad(10f)
                    idx++
                }
            }
        }
        return table
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

        // vẽ background theo world hiện tại
        game.batch.begin()
        val bg = dataBG.worldBackgrounds[currentWorld]
        bg?.let {
            game.batch.draw(it, 0f, 0f, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
        }
        game.batch.end()

        stage.act(delta)
        stage.draw()
    }

    override fun resize(width: Int, height: Int) { stage.viewport.update(width, height, true) }
    override fun pause() {}
    override fun resume() {}
    override fun hide() {}
    override fun dispose() { stage.dispose(); skin.dispose() }
}
