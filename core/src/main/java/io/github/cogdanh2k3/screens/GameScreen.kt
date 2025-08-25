package io.github.cogdanh2k3.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.input.GestureDetector
import io.github.cogdanh2k3.Main
import io.github.cogdanh2k3.game.Board
import io.github.cogdanh2k3.game.GameManager
import io.github.cogdanh2k3.utils.InputHandler

class GameScreen (val game: Main) : ScreenAdapter(){

    private val batch = SpriteBatch();
    private val font = BitmapFont();


//    1. Board
    private val board = Board(4);

//    2. Game Manager
    private val manager = GameManager(board)

    private var score = 0
    private var highScore = 0

    init {
        manager.spawnTile()
        manager.spawnTile()

        val gestureDetector = GestureDetector(InputHandler(manager, this))
        Gdx.input.inputProcessor = gestureDetector
    }
    override fun render(delta: Float){
        if (manager.isMoved) {
            manager.update()
            score = manager.score
            if (manager.isGameOver()) {
                // TODO: chuyển sang GameOverScreen
                // game.screen = GameOverScreen(game, score, highScore)
            }
        }

        Gdx.gl.glClearColor(1f, 1f, 1f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        batch.begin()
        board.draw(batch)   // vẽ grid + tiles (cần implement trong Board)
        font.draw(batch, "Score: $score", 50f, 700f)
        font.draw(batch, "Highscore: $highScore", 50f, 650f)
        batch.end()
    }

    override fun pause() {
        val prefs = Gdx.app.getPreferences("PicFusePrefs")
        if (score > highScore) {
            highScore = score
            prefs.putInteger("highscore", highScore)
            prefs.flush()
        }
    }

    override fun resume() {
        val prefs = Gdx.app.getPreferences("PicFusePrefs")
        highScore = prefs.getInteger("highscore", 0)
    }

    override fun dispose() {
        batch.dispose()
        font.dispose()
    }
}
