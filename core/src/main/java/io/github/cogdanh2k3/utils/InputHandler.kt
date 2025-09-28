package io.github.cogdanh2k3.utils

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.input.GestureDetector
import com.badlogic.gdx.math.Vector3
import io.github.cogdanh2k3.game.GameManager
import io.github.cogdanh2k3.screens.GamePlay.GameScreen
import kotlin.math.abs

class InputHandler(
    private val manager: GameManager,
    private val screen: GameScreen
) : GestureDetector.GestureAdapter() {

    private val swipeThreshold = 50f   // cham nhe -> ko tinh la vuot
    private val velocityThreshold = 200f // toc do toi thieu de vuot

    // Xử lý vuốt
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

    // Xử lý chạm (cho booster)
    override fun touchDown(x: Float, y: Float, pointer: Int, button: Int): Boolean {
        if (manager.activeWallBooster == true) {
            val touchPoint = Vector3(x, y, 0f)
            screen.viewport.unproject(touchPoint)

            val pos = screen.board.getTileAt(touchPoint.x, touchPoint.y)
            if (pos != null) {
                val (r, c) = pos
                val tile = screen.board.getTile(r, c)

                if (tile.value == -1) {
                    tile.value = 0 // Xóa wall
                    screen.boosterMessage.show("Đã loại bỏ WALL!")
                    manager.activeWallBooster = false
                } else {
                    screen.boosterMessage.show("Ô này không phải WALL!")
                }
            }
            return true
        }else if(manager.activeBombRowBooster == true){
            val touchPoint = Vector3(x, y, 0f)
            screen.viewport.unproject(touchPoint)

            val pos = screen.board.getTileAt(touchPoint.x, touchPoint.y)
            if (pos != null) {
                val (row, col) = pos
                if (screen.board.getTile(row, col).value != -1) { // không cho chọn wall
                    screen.board.clearRow(row)
                    screen.boosterMessage.show("Đã xóa hàng $row!")
                    manager.activeBombRowBooster = false
                } else {
                    screen.boosterMessage.show("Không thể chọn Wall!")
                }
            }
                return true
        }
        return false
    }
}
