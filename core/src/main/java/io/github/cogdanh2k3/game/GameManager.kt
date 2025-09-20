package io.github.cogdanh2k3.game

import com.badlogic.gdx.utils.Timer
import io.github.cogdanh2k3.Mode.GameMode
import io.github.cogdanh2k3.audio.SoundId
import io.github.cogdanh2k3.audio.SoundManager
import kotlin.random.Random

class GameManager(val board: Board, val mode: GameMode,val level:Int = -1) {
    var score = 0
        private set
    var isMoved = false
        private set
    var hasWon = false
        private set
    var hasLost = false
        private set
    fun InitData(){
        if(level!=-1){
            board.tileImages = mode.data.themes[level-1].images
        }
    }
    fun spawnTile() {
        if (hasWon || hasLost) return
        val empty = board.getEmptyCells()
        if (empty.isNotEmpty()) {
            val (r, c) = empty.random()
            val value = if (Random.nextFloat() < 0.9f) 2 else 4
            board.setTile(r, c, value)
            board.addSpawnAnim(r, c, value)
        }
    }

    fun update() {
        if(hasWon||hasLost) return
        if (isMoved) {
            spawnTile()
            checkWin()
            checkLose()
            isMoved = false
        }
    }
    private fun checkWin() {
        if (hasWon) return
        for (r in 0 until board.size) {
            for (c in 0 until board.size) {
                if (board.getTile(r, c) == 32) {
                    SoundManager.playSfx(SoundId.WIN)
                    hasWon = true
                    return
                }
            }
        }
    }
    private fun checkLose() {
        if (hasLost || hasWon) return
        if (board.getEmptyCells().isNotEmpty()) return

        // không còn ô trống → check merge được nữa không
        for (r in 0 until board.size) {
            for (c in 0 until board.size) {
                val v = board.getTile(r, c)
                if (r + 1 < board.size && v == board.getTile(r + 1, c)) return
                if (c + 1 < board.size && v == board.getTile(r, c + 1)) return
            }
        }
        SoundManager.playSfx(SoundId.LOSE)
        hasLost = true
    }

    fun moveLeft() = moveRows(reversed = false)
    fun moveRight() = moveRows(reversed = true)
    fun moveUp() = moveCols(reversed = false)
    fun moveDown() = moveCols(reversed = true)

    private fun moveRows(reversed: Boolean) {
        var moved = false
        for (r in 0 until board.size) {
            val line = (0 until board.size).map { board.getTile(r, it) }
            val final = processLine(line, reversed, r, isRow = true)
            for (c in 0 until board.size) {
                if (board.getTile(r, c) != final[c]) {
                    moved = true
                    board.setTile(r, c, final[c])
                }
            }
        }
        isMoved = moved
    }

    private fun moveCols(reversed: Boolean) {
        var moved = false
        for (c in 0 until board.size) {
            val line = (0 until board.size).map { board.getTile(it, c) }
            val final = processLine(line, reversed, c, isRow = false)
            for (r in 0 until board.size) {
                if (board.getTile(r, c) != final[r]) {
                    moved = true
                    board.setTile(r, c, final[r])
                }
            }
        }
        isMoved = moved
    }

    private fun processLine(
        line: List<Int>,
        reversed: Boolean,
        index: Int,
        isRow: Boolean
    ): List<Int> {

        val work = if (reversed) line.reversed() else line
        val compact = work.filter { it != 0 }.toMutableList()
        val mergedList = mutableListOf<Int>()
        var skip = false

        // --- Merge logic ---
        for (i in compact.indices) {
            if (skip) {
                skip = false
                continue
            }
            if (i < compact.lastIndex && compact[i] == compact[i + 1]) {
                val v = compact[i] * 2
                score += v
                mergedList.add(v)
                skip = true
            } else {
                mergedList.add(compact[i])
            }
        }

        while (mergedList.size < board.size) mergedList.add(0)
        val final = if (reversed) mergedList.reversed() else mergedList

        // --- Move actions với mergedResult flag ---
        data class MoveAction(val from: Int, val to: Int, val value: Int, val mergedResult: Boolean)
        val moveActions = mutableListOf<MoveAction>()

        var readIndex = 0
        var writeIndex = 0
/*        while (readIndex < work.size) {
            val v = work[readIndex]
            if (v == 0) {
                readIndex++
                continue
            }

            if (readIndex < work.lastIndex && work[readIndex] == work[readIndex + 1]) {
                val mergedValue = v * 2
                score += mergedValue
                // Chỉ tile kết quả merge spawn explosion
                moveActions.add(MoveAction(readIndex, writeIndex, v, mergedResult = false))
                moveActions.add(MoveAction(readIndex + 1, writeIndex, work[readIndex + 1], mergedResult = true))
                writeIndex++
                readIndex += 2
            } else {
                moveActions.add(MoveAction(readIndex, writeIndex, v, mergedResult = false))
                writeIndex++
                readIndex++
            }
        }*/
        //var readIndex = 0
        //var writeIndex = 0
        while (readIndex < compact.size) {
            val v = compact[readIndex]
            if (readIndex < compact.lastIndex && compact[readIndex] == compact[readIndex + 1]) {
                // Tile merge
                val mergedValue = v * 2
                score += mergedValue
                // Tile đầu di chuyển về writeIndex
                moveActions.add(MoveAction(readIndex, writeIndex, v, mergedResult = false))
                // Tile thứ hai merge → spawn explosion
                moveActions.add(MoveAction(readIndex + 1, writeIndex, compact[readIndex + 1], mergedResult = true))
                writeIndex++
                readIndex += 2
            } else {
                // Tile đơn
                moveActions.add(MoveAction(readIndex, writeIndex, v, mergedResult = false))
                writeIndex++
                readIndex++
            }
        }
        // --- Tạo animation + explosion ---
        for (action in moveActions) {
            if (action.from == action.to) continue

            if (isRow) {
                val fromC = if (reversed) board.size - 1 - action.from else action.from
                val toC = if (reversed) board.size - 1 - action.to else action.to
                board.addMoveAnim(action.value, index, fromC, index, toC)
                if (action.mergedResult) {
                    board.addExplosion(index, toC)
                    SoundManager.playSfx(SoundId.MERGE)
                }
            } else {
                val fromR = if (reversed) board.size - 1 - action.from else action.from
                val toR = if (reversed) board.size - 1 - action.to else action.to
                board.addMoveAnim(action.value, fromR, index, toR, index)
                if (action.mergedResult){
                    board.addExplosion(toR, index)
                    SoundManager.playSfx(SoundId.MERGE)
                }
            }
        }

        return final
    }




}
