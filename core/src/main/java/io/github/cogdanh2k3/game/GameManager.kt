package io.github.cogdanh2k3.game

import com.badlogic.gdx.utils.Timer
import io.github.cogdanh2k3.DataGame.LevelData
import io.github.cogdanh2k3.Mode.GameMode
import io.github.cogdanh2k3.audio.SoundId
import io.github.cogdanh2k3.audio.SoundManager
import kotlin.random.Random
const val TILE_WALL = -1
const val TILE_FROZEN = -2
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
            board.LEVEL_WALLS=levelData.wallData
            board.InitGrid()
        }
    }
/*
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
*/
fun spawnTile() {
    if (hasWon || hasLost) return
    val empty = board.getEmptyCells()
    if (empty.isNotEmpty()) {
        val (r, c) = empty.random()
        val value = if (Random.nextFloat() < 0.9f) 2 else 4

        // Xác suất spawn tile đóng băng (ví dụ 20%)
        val frozen = if (Random.nextFloat() < 0.2f) 2 else 0

        board.setTile(r, c, value, frozen)
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
                val oldTile = board.getTile(r, c)
                val newTile = final[c]
                if (oldTile.value != newTile.value) { // ❌ bỏ so frozen
                    moved = true
                }
                board.setTile(r, c, newTile.copy(frozen = oldTile.frozen)) // giữ nguyên frozen
            }
        }

        if (moved) {
            SoundManager.playSfx(SoundId.SWOOSH)
            reduceFrozenTiles() // ✅ chỉ giảm khi có movement thật
        }
        isMoved = moved
    }

    private fun moveCols(reversed: Boolean) {
        var moved = false
        for (c in 0 until board.size) {
            val line = (0 until board.size).map { board.getTile(it, c) }
            val final = processLine(line, reversed, c, isRow = false)
            for (r in 0 until board.size) {
                val oldTile = board.getTile(r, c)
                val newTile = final[r]
                if (oldTile.value != newTile.value) {
                    moved = true
                }
                board.setTile(r, c, newTile.copy(frozen = oldTile.frozen))
            }
        }

        if (moved) {
            SoundManager.playSfx(SoundId.SWOOSH)
            reduceFrozenTiles()
        }
        isMoved = moved
    }
    // ✅ Hàm giảm frozen sau khi move
    private fun reduceFrozenTiles() {
        for (r in 0 until board.size) {
            for (c in 0 until board.size) {
                val t = board.getTile(r, c)
                if (t.frozen > 0) {
                    board.setTile(r, c, t.copy(frozen = t.frozen - 1))
                }
            }
        }
    }


    private fun processLine(
        line: List<Tile>,
        reversed: Boolean,
        index: Int,
        isRow: Boolean
    ): List<Tile> {

        val work = if (reversed) line.reversed() else line
        val final = work.map { it.copy() }.toMutableList()

        data class MoveAction(val from: Int, val to: Int, val value: Int, val merged: Boolean)
        val moveActions = mutableListOf<MoveAction>()

        var start = 0
        while (start < work.size) {
            // Nếu là wall hoặc tile đóng băng thì bỏ qua (đóng băng coi như wall)
            if (work[start].value == TILE_WALL || work[start].frozen > 0) {
                if (work[start].frozen > 0) {
                    // giảm thời gian đóng băng
                    final[start] = work[start].copy(frozen = work[start].frozen - 1)
                }
                start++
                continue
            }

            // tìm đoạn liên tục không có WALL và không có FROZEN
            var end = start
            while (end < work.size && work[end].value != TILE_WALL && work[end].frozen == 0) end++

            // compact đoạn
            val compact = mutableListOf<Pair<Tile, Int>>() // (Tile, vị trí gốc)
            for (i in start until end) {
                val t = work[i]
                if (t.value != 0) compact.add(t to i)
            }

            val mergedList = mutableListOf<Tile>()
            var writeIndex = start
            var i = 0
            while (i < compact.size) {
                val (tile, pos) = compact[i]

                // xử lý merge
                if (i < compact.lastIndex) {
                    val (nextTile, _) = compact[i + 1]

                    if (nextTile.frozen == 0 && tile.value == nextTile.value) {
                        // merge hợp lệ
                        val mergedValue = tile.value * 2
                        score += mergedValue

                        mergedList.add(Tile(mergedValue, 0))

                        moveActions.add(MoveAction(compact[i].second, writeIndex, tile.value, merged = false))
                        moveActions.add(MoveAction(compact[i + 1].second, writeIndex, nextTile.value, merged = true))

                        writeIndex++
                        i += 2
                        continue
                    }
                }

                // không merge
                mergedList.add(tile.copy())
                moveActions.add(MoveAction(pos, writeIndex, tile.value, merged = false))
                writeIndex++
                i++
            }

            // padding 0 cho đủ đoạn
            while (mergedList.size < end - start) mergedList.add(Tile(0, 0))

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
                    board.addMergeAnim(index, toC, action.value * 2)
                    SoundManager.playSfx(SoundId.MERGE)
                }
            } else {
                val fromR = if (reversed) board.size - 1 - action.from else action.from
                val toR = if (reversed) board.size - 1 - action.to else action.to
                board.addMoveAnim(action.value, fromR, index, toR, index)

                if (action.merged) {
                    board.addExplosion(toR, index)
                    board.addMergeAnim(toR, index, action.value * 2)
                    SoundManager.playSfx(SoundId.MERGE)
                }
            }
        }

        return output
    }








}
