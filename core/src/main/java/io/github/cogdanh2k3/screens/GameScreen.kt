package io.github.cogdanh2k3.screens

import com.badlogic.gdx.Game
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import io.github.cogdanh2k3.game.Board
import io.github.cogdanh2k3.game.GameManager

class GameScreen (val game: Game) : ScreenAdapter(){

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
    }



//    3. InputHandler
//    4. SpriteBatch
//    5. Score/Highscore


}
