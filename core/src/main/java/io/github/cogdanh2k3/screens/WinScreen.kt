package io.github.cogdanh2k3.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.viewport.FitViewport
import io.github.cogdanh2k3.Main

class WinScreen(private val game: Main, private val score: Int) : ScreenAdapter() {

    private val camera = OrthographicCamera()
    private val viewport = FitViewport(480f, 800f, camera)
    private val batch = SpriteBatch()
    private val shapeRenderer = ShapeRenderer()

    private val titleFont = BitmapFont().apply {
        data.setScale(3f)
        color = Color.GOLD
    }
    private val infoFont = BitmapFont().apply {
        data.setScale(1.5f)
        color = Color.WHITE
    }
    private val buttonFont = BitmapFont().apply {
        data.setScale(1.6f)
        color = Color.BLACK
    }

    // 2 nút
    private val playAgainButton = Rectangle(80f, 250f, 140f, 80f)
    private val homeButton = Rectangle(260f, 250f, 140f, 80f)

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 0.8f) // nền mờ
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        camera.update()
        batch.projectionMatrix = camera.combined
        shapeRenderer.projectionMatrix = camera.combined

        // Vẽ khung nền popup
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        shapeRenderer.color = Color(0.15f, 0.15f, 0.15f, 0.95f)
        shapeRenderer.rect(60f, 200f, 360f, 400f)

        // Vẽ nút Play Again
        shapeRenderer.color = Color.GOLD
        shapeRenderer.rect(playAgainButton.x, playAgainButton.y, playAgainButton.width, playAgainButton.height)

        // Vẽ nút Home
        shapeRenderer.color = Color.SKY
        shapeRenderer.rect(homeButton.x, homeButton.y, homeButton.width, homeButton.height)

        shapeRenderer.end()

        // Vẽ chữ
        batch.begin()
        titleFont.draw(batch, "YOU WIN!", 120f, 550f)
        infoFont.draw(batch, "Score: $score", 170f, 480f)

        // Vẽ text trong nút
        drawButtonText("Play Again", playAgainButton, batch)
        drawButtonText("Home", homeButton, batch)

        batch.end()

        // Xử lý click
        if (Gdx.input.justTouched()) {
            val touchPos = Vector3(Gdx.input.x.toFloat(), Gdx.input.y.toFloat(), 0f)
            viewport.unproject(touchPos)

            when {
                playAgainButton.contains(touchPos.x, touchPos.y) -> {
                    game.screen = GameScreen(game) // restart
                }
                homeButton.contains(touchPos.x, touchPos.y) -> {
                    game.screen = MenuScreen(game) // quay lại menu chính
                }
            }
        }
    }

    private fun drawButtonText(text: String, button: Rectangle, batch: SpriteBatch) {
        val layout = com.badlogic.gdx.graphics.g2d.GlyphLayout(buttonFont, text)
        val textX = button.x + (button.width - layout.width) / 2f
        val textY = button.y + (button.height + layout.height) / 2f
        buttonFont.draw(batch, text, textX, textY)
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height, true)
    }

    override fun dispose() {
        batch.dispose()
        shapeRenderer.dispose()
        titleFont.dispose()
        infoFont.dispose()
        buttonFont.dispose()
    }
}
