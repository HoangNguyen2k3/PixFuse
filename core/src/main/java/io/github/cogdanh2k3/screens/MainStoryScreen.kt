package io.github.cogdanh2k3.screens

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
import io.github.cogdanh2k3.Main
import io.github.cogdanh2k3.utils.FontUtils

class MainStoryScreenclass(private val game: Main) : ScreenAdapter() {

    private val camera = OrthographicCamera()
    private val viewport = ExtendViewport(480f, 800f, camera)
    private val batch = SpriteBatch()
    private val stage = Stage(viewport, batch)

    private val bg = Texture("UI/bg_new.png")
    private val hero = Texture("UI/pikachu.png")

    private val storyText = """
        Trong thế giới của PixFuse, người chơi hóa thân thành một nhà thám hiểm trẻ tuổi với khả năng đặc biệt - sức mạnh "Fusion" (hợp nhất).
        Đây là năng lực huyền bí cho phép kết hợp sức mạnh của các sinh vật để tạo ra những dạng tiến hóa mạnh mẽ hơn.

        Thế giới game được chia thành nhiều vùng đất khác nhau, mỗi vùng có hệ sinh thái độc đáo với những sinh vật đặc trưng riêng:
        vùng Pokemon, vùng Dragon Ball, vùng Doraemon và nhiều vùng bí ẩn khác đang chờ khám phá.

        Một làn sóng bóng tối lan rộng, biến những sinh vật hòa bình thành quái vật hung dữ.
        Là người mang sức mạnh Fusion, bạn phải du hành qua các vùng đất, hợp nhất sức mạnh và khôi phục hòa bình cho thế giới.
    """.trimIndent()

    private lateinit var storyLabel: Label
    private lateinit var nextButton: TextButton
    private lateinit var skipButton: TextButton

    private var charIndex = 0
    private var timeSinceLastChar = 0f
    private var charDelay = 0.02f
    private var isFullyShown = false

    override fun show() {
        Gdx.input.inputProcessor = stage

        // Font setup
        val storyFont = FontUtils.loadCustomFont(18, Color.WHITE)
        val titleFont = FontUtils.loadCustomFont(40, Color.GOLD)

        val labelStyle = Label.LabelStyle(storyFont, Color.WHITE)
        storyLabel = Label("", labelStyle)
        storyLabel.setWrap(true)
        storyLabel.setAlignment(Align.topLeft)
        storyLabel.color = Color(1f, 1f, 1f, 0f)

        // === Root layout ===
        val root = Table()
        root.setFillParent(true)
        root.pad(30f)
        stage.addActor(root)

        // --- Title ---
        val title = Label("PixFuse: Opening", Label.LabelStyle(titleFont, Color(1f, 0.9f, 0.3f, 1f)))
        title.setAlignment(Align.center)
        title.addAction(Actions.fadeIn(2f))
        root.add(title).expandX().center().padBottom(15f)
        root.row()

        // --- Story text ---
        root.add(storyLabel).width(400f).height(400f).padBottom(10f)
        root.row()

        // --- Hero image (Pikachu) ---
        val heroImg = Image(hero)
        heroImg.setSize(220f, 220f)
        heroImg.addAction(Actions.fadeIn(2f))
        root.add(heroImg).center().padBottom(25f)
        root.row()

        // === Create pixel button style ===
        val buttonFont = FontUtils.loadCustomFont(20, Color.WHITE)

        fun createPixelBackground(width: Int, height: Int, baseColor: Color, borderColor: Color): Drawable {
            val pixmap = Pixmap(width, height, Pixmap.Format.RGBA8888)
            pixmap.setColor(baseColor)
            pixmap.fill()

            pixmap.setColor(borderColor)
            val thickness = 3
            for (x in 0 until width) {
                for (t in 0 until thickness) {
                    pixmap.drawPixel(x, t)
                    pixmap.drawPixel(x, height - 1 - t)
                }
            }
            for (y in 0 until height) {
                for (t in 0 until thickness) {
                    pixmap.drawPixel(t, y)
                    pixmap.drawPixel(width - 1 - t, y)
                }
            }

            val texture = Texture(pixmap)
            pixmap.dispose()
            return Image(texture).drawable
        }

        // pixel colors
        val upDrawable = createPixelBackground(180, 60, Color(0.2f, 0.2f, 0.2f, 1f), Color(1f, 0.9f, 0.3f, 1f))
        val overDrawable = createPixelBackground(180, 60, Color(0.35f, 0.35f, 0.35f, 1f), Color(1f, 1f, 0.4f, 1f))
        val downDrawable = createPixelBackground(180, 60, Color(0.1f, 0.1f, 0.1f, 1f), Color(0.7f, 0.6f, 0.2f, 1f))

        val buttonStyle = TextButton.TextButtonStyle().apply {
            font = buttonFont
            fontColor = Color.WHITE
            up = upDrawable
            over = overDrawable
            down = downDrawable
        }

        nextButton = TextButton("NEXT >", buttonStyle)
        skipButton = TextButton("SKIP", buttonStyle)

        nextButton.pad(12f, 25f, 12f, 25f)
        skipButton.pad(12f, 25f, 12f, 25f)

        // --- Button layout ---
        val buttonTable = Table()
        buttonTable.add(skipButton).width(140f).padRight(40f)
        buttonTable.add(nextButton).width(160f)
        root.add(buttonTable).expandY().bottom().padBottom(15f)
        root.row()

        // Fade in
        storyLabel.addAction(Actions.fadeIn(1f))

        // === Button listeners ===
        skipButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                if (charIndex < storyText.length) {
                    charIndex = storyText.length
                    storyLabel.setText(storyText)
                    isFullyShown = true
                } else if (isFullyShown) {
                    game.screen = MenuScreen(game)
                }
            }
        })

        nextButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                game.screen = ModeSelectScreen(game)
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

        // Typewriter effect
        if (charIndex < storyText.length) {
            timeSinceLastChar += delta
            if (timeSinceLastChar >= charDelay) {
                timeSinceLastChar = 0f
                charIndex++
                storyLabel.setText(storyText.substring(0, charIndex))

                if (charIndex == storyText.length) {
                    isFullyShown = true
                }
            }
        }

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
        hero.dispose()
    }
}
