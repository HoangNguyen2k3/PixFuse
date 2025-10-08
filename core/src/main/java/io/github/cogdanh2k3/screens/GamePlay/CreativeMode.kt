package io.github.cogdanh2k3.screens.GamePlay

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.badlogic.gdx.utils.Array
import io.github.cogdanh2k3.DataGame.SaveManager
import io.github.cogdanh2k3.Main
import io.github.cogdanh2k3.Mode.CreativeMode
import io.github.cogdanh2k3.screens.MenuScreen
import io.github.cogdanh2k3.utils.FontUtils

class CreativeModeScreen(private val game: Main) : ScreenAdapter() {

    private val camera = OrthographicCamera()
    private val viewport = ExtendViewport(480f, 800f, camera)
    private val batch = SpriteBatch()
    private val stage = Stage(viewport, batch)
    private val bg = Texture("titles/bg_game.png")

    fun applySavedTrapData(trapSliders: List<Slider>, trapTable: Table) {
        val data = listOf(
            SaveManager.gameSave.trapBoomCreativeMode,
            SaveManager.gameSave.trapWallCreativeMode,
            SaveManager.gameSave.trapIceCreativeMode,
            SaveManager.gameSave.trapThunderCreativeMode
        )
        trapSliders.forEachIndexed { i, slider ->
            slider.value = data[i].toFloat()
            val percentLabel = trapTable.cells[i * 3 + 2].actor as Label
            percentLabel.setText("${data[i]}%")
        }
    }

    fun saveTrapData(trapSliders: List<Slider>) {
        SaveManager.gameSave.trapBoomCreativeMode = trapSliders[0].value.toInt()
        SaveManager.gameSave.trapWallCreativeMode = trapSliders[1].value.toInt()
        SaveManager.gameSave.trapIceCreativeMode = trapSliders[2].value.toInt()
        SaveManager.gameSave.trapThunderCreativeMode = trapSliders[3].value.toInt()
    }

    override fun show() {
        Gdx.input.inputProcessor = stage

        val fontTitle = FontUtils.loadCustomFont(48, Color(1f, 0.85f, 0.1f, 1f))
        val fontLabel = FontUtils.loadCustomFont(22, Color.WHITE)
        val fontButton = FontUtils.loadCustomFont(22, Color.WHITE)
        val fontNote = FontUtils.loadCustomFont(16, Color.LIGHT_GRAY)

        val root = Table()
        root.setFillParent(true)
        root.pad(40f)
        stage.addActor(root)

        // ==== Tiêu đề ====
        val title = Label("Creative Mode Editor", Label.LabelStyle(fontTitle, Color.DARK_GRAY))
        title.setAlignment(Align.center)
        title.addAction(Actions.sequence(Actions.alpha(0f), Actions.fadeIn(1f)))
        root.add(title).center().padBottom(40f)
        root.row()

        // ==== Hàm tạo nền pixel ====
        fun createPixelBackground(w: Int, h: Int, base: Color, border: Color): Drawable {
            val pixmap = Pixmap(w, h, Pixmap.Format.RGBA8888)
            pixmap.setColor(base); pixmap.fill()
            pixmap.setColor(border)
            val t = 3
            for (x in 0 until w) for (i in 0 until t) {
                pixmap.drawPixel(x, i)
                pixmap.drawPixel(x, h - 1 - i)
            }
            for (y in 0 until h) for (i in 0 until t) {
                pixmap.drawPixel(i, y)
                pixmap.drawPixel(w - 1 - i, y)
            }
            val tex = Texture(pixmap)
            pixmap.dispose()
            return Image(tex).drawable
        }

        // ==== Style nút ====
        val buttonStyle = TextButton.TextButtonStyle().apply {
            font = fontButton
            fontColor = Color.WHITE
            up = createPixelBackground(180, 60, Color(0.25f, 0.25f, 0.25f, 1f), Color(1f, 0.85f, 0.1f, 1f))
            over = createPixelBackground(180, 60, Color(0.35f, 0.35f, 0.35f, 1f), Color(1f, 0.9f, 0.3f, 1f))
            down = createPixelBackground(180, 60, Color(0.15f, 0.15f, 0.15f, 1f), Color(0.8f, 0.7f, 0.2f, 1f))
        }

// ==== Ô nhập kích thước bàn cờ (chỉ 1 cạnh) ====
        fun makeField(defaultText: String): TextField {
            val style = TextField.TextFieldStyle().apply {
                font = fontLabel
                fontColor = Color.WHITE
                background = createPixelBackground(100, 40, Color(0.12f, 0.12f, 0.12f, 1f), Color(0.9f, 0.9f, 0.9f, 1f))
                cursor = createPixelBackground(2, 40, Color.WHITE, Color.WHITE)
            }
            return TextField(defaultText, style).apply { alignment = Align.center }
        }

        val gridTable = Table()
        val sizeLabel = Label("Kích thước bàn cờ:", Label.LabelStyle(fontLabel, Color.WHITE))
        val sizeField = makeField(SaveManager.gameSave.gridRowCreativeMode.toString()) // chỉ dùng 1 giá trị
        gridTable.add(sizeLabel).padRight(10f)
        gridTable.add(sizeField).width(90f)
        root.add(gridTable).center().padBottom(35f)
        root.row()

        // ==== Tỉ lệ trap ====
        val trapTable = Table()
        val trapNames = listOf("Boom", "Wall", "Ice", "Thunder")
        val trapSliders = mutableListOf<Slider>()
        trapNames.forEach { trap ->
            val label = Label("$trap:", Label.LabelStyle(fontLabel, Color.WHITE))
            val sliderStyle = Slider.SliderStyle().apply {
                background = createPixelBackground(200, 10, Color(0.15f, 0.15f, 0.15f, 1f), Color(0.5f, 0.5f, 0.5f, 1f))
                val knobDrawable = createPixelBackground(20, 35, Color(1f, 0.85f, 0.2f, 1f), Color(1f, 0.7f, 0.1f, 1f))
                knob = knobDrawable; knobOver = knobDrawable; knobDown = knobDrawable
            }
            val slider = Slider(0f, 20f, 1f, false, sliderStyle)
            val percent = Label("0%", Label.LabelStyle(fontLabel, Color.YELLOW))
            slider.addListener { _ -> percent.setText("${slider.value.toInt()}%"); false }
            trapTable.add(label).width(80f)
            trapTable.add(slider).width(220f).padRight(10f)
            trapTable.add(percent)
            trapTable.row()
            trapSliders.add(slider)
        }
        applySavedTrapData(trapSliders, trapTable)
        root.add(trapTable).center()
        root.row()

        // ==== Chọn tỉ lệ tile giá trị cao ====
        val spawnLabel = Label("Tỉ lệ tile giá trị cao:", Label.LabelStyle(fontLabel, Color.WHITE))
        val spawnSelect = SelectBox<String>(SelectBox.SelectBoxStyle().apply {
            font = fontLabel
            fontColor = Color.WHITE
            background = createPixelBackground(180, 40, Color(0.15f, 0.15f, 0.15f, 1f), Color(0.8f, 0.8f, 0.8f, 1f))
            scrollStyle = ScrollPane.ScrollPaneStyle()
            listStyle = com.badlogic.gdx.scenes.scene2d.ui.List.ListStyle(fontLabel, Color.WHITE, Color.YELLOW,
                createPixelBackground(180, 40, Color(0.2f, 0.2f, 0.2f, 1f), Color(1f, 0.85f, 0.1f, 1f))
            )
        })
        spawnSelect.setItems(Array.with("Normal", "High", "Very High"))
        spawnSelect.selected = when (SaveManager.gameSave.spawnTileRateCreativeMode) {
            1 -> "Normal"; 2 -> "High"; 3 -> "Very High"; else -> "Normal"
        }

        val spawnTable = Table()
        spawnTable.add(spawnLabel).padRight(10f)
        spawnTable.add(spawnSelect).width(180f)
        root.add(spawnTable).center().padBottom(25f)
        root.row()

        // ==== Chọn Theme ====
        val themeLabel = Label("Theme:", Label.LabelStyle(fontLabel, Color.WHITE))
        val themeSelect = SelectBox<String>(SelectBox.SelectBoxStyle().apply {
            font = fontLabel
            fontColor = Color.WHITE
            background = createPixelBackground(180, 40, Color(0.15f, 0.15f, 0.15f, 1f), Color(0.8f, 0.8f, 0.8f, 1f))
            scrollStyle = ScrollPane.ScrollPaneStyle()
            listStyle = com.badlogic.gdx.scenes.scene2d.ui.List.ListStyle(fontLabel, Color.WHITE, Color.YELLOW,
                createPixelBackground(180, 40, Color(0.2f, 0.2f, 0.2f, 1f), Color(1f, 0.85f, 0.1f, 1f))
            )
        })
        themeSelect.setItems(Array.with("Pikachu", "PVZ", "Doraemon", "MemeCat", "DragonBall"))
        themeSelect.selected = SaveManager.gameSave.themeCreativeMode.ifEmpty { "Pikachu" }

        val themeTable = Table()
        themeTable.add(themeLabel).padRight(10f)
        themeTable.add(themeSelect).width(180f)
        root.add(themeTable).center().padBottom(35f)
        root.row()

        // ==== Ghi chú ====
        val note = Label("Mỗi loại trap tối đa 20%", Label.LabelStyle(fontNote, Color.LIGHT_GRAY))
        root.add(note).padBottom(25f)
        root.row()

        // ==== Nút hành động ====
        val saveButton = TextButton("SAVE & PLAY", buttonStyle)
        val resetButton = TextButton("RESET", buttonStyle)
        val backButton = TextButton("QUAY LẠI", buttonStyle)
        val btnRow = Table()
        btnRow.add(saveButton).width(130f).pad(10f)
        btnRow.add(resetButton).width(130f).pad(10f)
        btnRow.add(backButton).width(130f).pad(10f)
        root.add(btnRow).expandY().bottom().padBottom(20f)

        // ==== Sự kiện ====
        saveButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                val size = sizeField.text.toInt()
                SaveManager.gameSave.gridColumnCreativeMode = size
                SaveManager.gameSave.gridRowCreativeMode = size
                SaveManager.gameSave.spawnTileRateCreativeMode = when (spawnSelect.selected) {
                    "Normal" -> 1; "High" -> 2; "Very High" -> 3; else -> 1
                }
                SaveManager.gameSave.themeCreativeMode = themeSelect.selected
                saveTrapData(trapSliders)
                SaveManager.saveGame()
                game.screen = GameScreen(game, CreativeMode())
            }
        })

        resetButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                val size = sizeField.text.toInt()
                SaveManager.gameSave.gridColumnCreativeMode = size
                SaveManager.gameSave.gridRowCreativeMode = size
                applySavedTrapData(trapSliders, trapTable)
                spawnSelect.selected = when (SaveManager.gameSave.spawnTileRateCreativeMode) {
                    1 -> "Normal"; 2 -> "High"; 3 -> "Very High"; else -> "Normal"
                }
                themeSelect.selected = SaveManager.gameSave.themeCreativeMode.ifEmpty { "A" }
            }
        })

        backButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                game.screen = MenuScreen(game)
            }
        })
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        batch.projectionMatrix = camera.combined
        batch.begin()
        batch.draw(bg, 0f, 0f, viewport.worldWidth, viewport.worldHeight)
        batch.end()
        stage.act(delta)
        stage.draw()
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height, true)
    }

    override fun dispose() {
        stage.dispose()
        batch.dispose()
        bg.dispose()
    }
}
