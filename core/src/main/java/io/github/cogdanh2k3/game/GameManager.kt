package io.github.cogdanh2k3.game

import com.badlogic.gdx.utils.Timer
import io.github.cogdanh2k3.DataGame.LevelData
import io.github.cogdanh2k3.Mode.GameMode
import io.github.cogdanh2k3.audio.SoundId
import io.github.cogdanh2k3.audio.SoundManager
import kotlin.random.Random
const val TILE_WALL = -1
class GameManager(val board: Board, val mode: GameMode, val levelData: LevelData? = null) {
    var score = 0
        private set
    var isMoved = false
        private set
    var hasWon = false
        private set
    var hasLost = false
        private set

    fun InitData(){
        if(levelData != null && levelData.id != -1){
            board.tileImages = mode.data.themes[levelData.currentWorld-1].images
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
        if (mode.checkWin(board, score)) {
            SoundManager.playSfx(SoundId.WIN)
            hasWon = true

            if (levelData!=null&&levelData.id != -1) {
                LevelManager.unlockNext(levelData.id)
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
        if(moved){
            SoundManager.playSfx(SoundId.SWOOSH)
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
        val final = work.toMutableList()

        data class MoveAction(val from: Int, val to: Int, val value: Int, val merged: Boolean)
        val moveActions = mutableListOf<MoveAction>()

        var start = 0
        while (start < work.size) {
            if (work[start] == TILE_WALL) {
                start++
                continue
            }

            // tìm đoạn liên tục không chứa WALL
            var end = start
            while (end < work.size && work[end] != TILE_WALL) end++

            // compact đoạn
            val compact = mutableListOf<Pair<Int, Int>>() // (giá trị, vị trí gốc)
            for (i in start until end) {
                val v = work[i]
                if (v != 0) compact.add(v to i)
            }

            val mergedList = mutableListOf<Int>()
            var writeIndex = start
            var i = 0
            while (i < compact.size) {
                val (v, pos) = compact[i]
                if (i < compact.lastIndex && v == compact[i + 1].first) {
                    // merge
                    val mergedValue = v * 2
                    score += mergedValue
                    mergedList.add(mergedValue)

                    moveActions.add(MoveAction(compact[i].second, writeIndex, v, merged = false))
                    moveActions.add(MoveAction(compact[i + 1].second, writeIndex, v, merged = true))

                    writeIndex++
                    i += 2
                } else {
                    // không merge
                    mergedList.add(v)
                    moveActions.add(MoveAction(pos, writeIndex, v, merged = false))

                    writeIndex++
                    i++
                }
            }

            // padding 0 cho đủ đoạn
            while (mergedList.size < end - start) mergedList.add(0)

            // gán kết quả vào final
            for (j in mergedList.indices) {
                final[start + j] = mergedList[j]
            }

            start = end
        }

        val output = if (reversed) final.reversed() else final

        // --- Tạo animation ---
        for (action in moveActions) {
            if (action.from == action.to) continue

            if (isRow) {
                val fromC = if (reversed) board.size - 1 - action.from else action.from
                val toC = if (reversed) board.size - 1 - action.to else action.to
                board.addMoveAnim(action.value, index, fromC, index, toC)

                if (action.merged) {
                    board.addExplosion(index, toC)
                    SoundManager.playSfx(SoundId.MERGE)
                }
            } else {
                val fromR = if (reversed) board.size - 1 - action.from else action.from
                val toR = if (reversed) board.size - 1 - action.to else action.to
                board.addMoveAnim(action.value, fromR, index, toR, index)

                if (action.merged) {
                    board.addExplosion(toR, index)
                    SoundManager.playSfx(SoundId.MERGE)
                }
            }
        }

        return output
    }






}
