package io.github.cogdanh2k3.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.viewport.ScreenViewport
import io.github.cogdanh2k3.DataGame.GameSave
import io.github.cogdanh2k3.DataGame.SaveManager
import io.github.cogdanh2k3.Main
import io.github.cogdanh2k3.Mode.EndlessMode
import io.github.cogdanh2k3.Mode.TargetMode
import io.github.cogdanh2k3.game.DataBG
import io.github.cogdanh2k3.game.LevelManager
import io.github.cogdanh2k3.screens.GamePlay.GameScreen
import io.github.cogdanh2k3.utils.FontUtils

class LevelSelectScreen(val game: Main) : Screen {
    private val stage = Stage(ScreenViewport())
    private val skin = Skin()
    private val dataBG = DataBG()
    private lateinit var scrollPane: ScrollPane   // giữ tham chiếu tới scrollPane
    val gradientTexture = Texture("BG/background.png")
    val buttonTexture = Texture("UI/button.png")
    val buttonInactive = Texture("UI/button_inactive.png")
    val buttonDrawable = TextureRegionDrawable(TextureRegion(buttonTexture))
    val buttonDrawableInActive = TextureRegionDrawable(TextureRegion(buttonInactive))
    val textButtonActiveStyle = TextButton.TextButtonStyle().apply {
        up = buttonDrawable
        down = buttonDrawable.tint(Color.GRAY)  // nhấn xuống đổi màu
        font = FontUtils.loadCustomFont(96, Color.WHITE)                      // font bạn đang dùng
    }
    val textButtonInActiveStyle = TextButton.TextButtonStyle().apply {
        up = buttonDrawableInActive
        down = buttonDrawableInActive.tint(Color.GRAY)  // nhấn xuống đổi màu
        font = FontUtils.loadCustomFont(96, Color.WHITE)                      // font bạn đang dùng
    }
    // Tạo texture tròn trắng (dot)
    private fun createDotTexture(size: Int): Texture {
        val pixmap = Pixmap(size, size, Pixmap.Format.RGBA8888)
        pixmap.setColor(Color.WHITE)
        pixmap.fillCircle(size / 2, size / 2, size / 2)
        val tex = Texture(pixmap)
        pixmap.dispose()
        return tex
    }

    // --- Trong LevelSelectScreen ---
    private val dots = mutableListOf<Image>()
    private val dotTex = createDotTexture(20)
    // Tính world hiện tại dựa trên scrollX
/*    private var currentWorld: Int
        get() {
            val pageWidth = Gdx.graphics.width.toFloat()
            val center = scrollPane.scrollX + pageWidth / 2f
            val worldIndex = (center / pageWidth).toInt()
            return (worldIndex + 1).coerceIn(1, dataBG.worldBackgrounds.size)
        }*/
    private var _currentWorld: Int = 1
    var currentWorld: Int
        get() {
            val pageWidth = Gdx.graphics.width.toFloat()
            val center = scrollPane.scrollX + pageWidth / 2f
            val worldIndex = (center / pageWidth).toInt()
            return (worldIndex + 1).coerceIn(1, dataBG.worldBackgrounds.size)
        }
        set(value) {
            _currentWorld = value
            // force scrollPane tới world đó
            scrollPane.scrollX = (value - 1) * Gdx.graphics.width.toFloat()
        }
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
        for (world in 1..LevelManager.worlds.size) {
            worldsTable.add(createWorldTable(world)).pad(50f)
        }
        scrollPane = ScrollPane(worldsTable).apply {
            setScrollingDisabled(false, true) // chỉ cuộn ngang
            setSmoothScrolling(true)
        }

        root.add(scrollPane).expand().fill()
        // Indicator
        val indicatorTable = Table()
        for (i in 1..LevelManager.worlds.size) {
            val img = Image(dotTex)
            img.setColor(Color.LIGHT_GRAY) // mặc định
            indicatorTable.add(img).pad(10f)
            dots.add(img)
        }

        // Thêm indicator vào root (bên dưới scrollPane)
        root.row().padTop(20f)
        root.add(indicatorTable).center()
        currentWorld = SaveManager.loadGameSave().currentUnlockWorld;
    }
    private fun updateDots() {
        for ((index, dot) in dots.withIndex()) {
            if (index + 1 == currentWorld) {
                dot.setColor(Color.WHITE)
                dot.setScale(1.5f)   // chấm to lên
            } else {
                dot.setColor(Color.LIGHT_GRAY)
                dot.setScale(1f)
            }
        }
    }

    private fun createWorldTable(worldId: Int): Table {
        val table = Table()

        // Lấy world tương ứng
        val worlds = LevelManager.levels // nếu bạn flatten toàn bộ thì đổi lại
        val world = LevelManager.worlds.find { it.id == worldId }

        if (world == null) {
            println("World $worldId không tồn tại!")
            return table
        }
        for (i in world.levels.indices) {
            val target = world.target_level[i+1]  // Int
            if (target != null) {
                world.levels[i].target = target
                world.levels[i].currentWorld = worldId
            }
        }
        var temp: Int = 0
        for (sizeB in world.sizeBoard.values) {
            if (sizeB != null) {
                world.levels[temp++].sizeBoard = sizeB
            }
        }
        val levels = world.levels

        var idx = 0

        val rows = 3
        val cols = 5
        for (r in 0 until rows) {
            table.row().pad(20f)
            for (c in 0 until cols) {
                if (idx < levels.size) {
                    val level = levels[idx]
                    val styleName = if (level.unlocked) "unlocked" else "locked"
                    val btn = if (level.unlocked) {
                        TextButton(level.indexInWorld.toString(), textButtonActiveStyle)
                    } else {
                        TextButton(level.indexInWorld.toString(), textButtonInActiveStyle)
                    }
                    if (level.unlocked) {
                        btn.addListener(object : ClickListener() {
                            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                                println("Play level ${level.id} in world $worldId")
                                //val target = world.target_level[level.indexInWorld] ?: emptyList()
                                game.screen = GameScreen(
                                    game,
                                    TargetMode(level.target),
                                    level
                                )
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
        val bg = if(LevelManager.worlds[currentWorld-1].levels[0].unlocked == true){
            dataBG.worldBackgrounds[currentWorld]
        }else{
            dataBG.worldBackgrounds[0]
        }
        bg?.let {
            // set alpha = 0.5f (50% mờ), R=G=B=1f để giữ nguyên màu
            game.batch.setColor(1f, 1f, 1f, 0.5f)
            game.batch.draw(it, 0f, 0f, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
            game.batch.setColor(1f, 1f, 1f, 1f) // reset về full màu (rất quan trọng!)
        }

        game.batch.end()
/*        game.batch.begin()

// 1. Vẽ gradient background (dùng ShapeRenderer hoặc 1 texture nhỏ 1x2)
// Ví dụ đơn giản: fill màu đen đậm → đen nhạt
// => dễ nhất là bạn tạo sẵn 1 texture gradient.png và stretch full screen:
        game.batch.draw(gradientTexture, 0f, 0f, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())

// 2. Vẽ world background ở giữa màn
        val bg = dataBG.worldBackgrounds[currentWorld]
        bg?.let {
            val bgWidth = it.width.toFloat()
            val bgHeight = it.height.toFloat()

            // scale theo chiều ngang nếu muốn che hết width
            val scale = Gdx.graphics.width.toFloat() / bgWidth
            val drawWidth = bgWidth * scale
            val drawHeight = bgHeight * scale

            val x = (Gdx.graphics.width - drawWidth) / 2f   // căn giữa ngang
            val y = (Gdx.graphics.height - drawHeight) / 2f // căn giữa dọc

            game.batch.draw(it, x, y, drawWidth, drawHeight)
        }

        game.batch.end()*/

        updateDots()
        stage.act(delta)
        stage.draw()
    }

    override fun resize(width: Int, height: Int) { stage.viewport.update(width, height, true) }
    override fun pause() {}
    override fun resume() {}
    override fun hide() {}
    override fun dispose() { stage.dispose(); skin.dispose() }
}
