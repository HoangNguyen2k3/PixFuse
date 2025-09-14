package io.github.cogdanh2k3.game

import kotlin.random.Random

class GameManager(val board: Board) {
    var score = 0
        private set
    var isMoved = false
        private set
    var hasWon = false
        private set
    var hasLost = false
        private set
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
        val merged = mutableListOf<Int>()
        var skip = false

        // Merge logic
        for (i in compact.indices) {
            if (skip) {
                skip = false
                continue
            }
            if (i < compact.lastIndex && compact[i] == compact[i + 1]) {
                val v = compact[i] * 2
                score += v
                merged.add(v)
                skip = true
            } else {
                merged.add(compact[i])
            }
        }
        while (merged.size < board.size) merged.add(0)

        val final = if (reversed) merged.reversed() else merged

        // --- FIX animation mapping ---
        // Đảm bảo mỗi occurrence (lần xuất hiện) của value map đúng vị trí trong merged
        val occurrenceMap = mutableMapOf<Int, Int>()

        for (i in work.indices) {
            val value = work[i]
            if (value == 0) continue

            // lần thứ mấy gặp value này trong work
            val occ = occurrenceMap.getOrDefault(value, 0)

            // tìm vị trí occ-th trong merged
            var count = 0
            var newPos = -1
            for (j in merged.indices) {
                if (merged[j] == value) {
                    if (count == occ) {
                        newPos = j
                        break
                    }
                    count++
                }
            }

            occurrenceMap[value] = occ + 1 // tăng counter

            if (newPos == -1) continue

            if (isRow) {
                val fromC = if (reversed) board.size - 1 - i else i
                val toC   = if (reversed) board.size - 1 - newPos else newPos
                if (fromC != toC) {
                    board.addMoveAnim(value, index, fromC, index, toC)
                }
            } else {
                val fromR = if (reversed) board.size - 1 - i else i
                val toR   = if (reversed) board.size - 1 - newPos else newPos
                if (fromR != toR) {
                    board.addMoveAnim(value, fromR, index, toR, index)
                }
            }
        }

        return final
    }


}
