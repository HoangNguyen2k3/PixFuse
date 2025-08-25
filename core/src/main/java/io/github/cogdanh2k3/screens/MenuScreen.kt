package io.github.cogdanh2k3.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import io.github.cogdanh2k3.Main

class MenuScreen (val game: Main) : ScreenAdapter() {
    override fun render(delta: Float) {
        if(Gdx.input.justTouched()){
            game.screen = GameScreen(game)
        }
    }
}
