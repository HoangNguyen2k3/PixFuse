package io.github.cogdanh2k3.utils

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.input.GestureDetector
import io.github.cogdanh2k3.game.GameManager
import io.github.cogdanh2k3.screens.GamePlay.GameScreen
import kotlin.math.abs

class InputHandler(
    private val manager: GameManager,
    private val screen: GameScreen
) : GestureDetector.GestureAdapter() {

    private val swipeThreshold = 50f   // cham nhe -> ko tinh la vuot
    private val velocityThreshold = 200f // toc do toi thieu de vuot

    override fun fling(velocityX: Float, velocityY: Float, button: Int): Boolean {
        if (manager.hasWon || manager.hasLost) {
            Gdx.app.log("InputHandler", "Game ended → fling ignored")
            return false
        }
        val absX = abs(velocityX)
        val absY = abs(velocityY)

        if (absX > absY && absX > velocityThreshold) {
            if (velocityX > 0) {
                manager.moveRight()
            } else {
                manager.moveLeft()
            }
            return true
        } else if (absY > absX && absY > velocityThreshold) {
            if (velocityY > 0) {
                manager.moveDown()
            } else {
                manager.moveUp()
            }
            return true
        }
        return false
    }
}
