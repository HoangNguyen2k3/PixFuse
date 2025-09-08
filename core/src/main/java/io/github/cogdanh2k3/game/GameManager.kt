package io.github.cogdanh2k3.game

import kotlin.random.Random

class GameManager(val board: Board) {
    var score = 0
        private set

    var isMoved = false
        private set

    // Spawn tile ngẫu nhiên
    fun spawnTile() {
        val emptyCells = board.getEmptyCells()
        if (emptyCells.isNotEmpty()) {
            val (row, col) = emptyCells.random()
            board.setTile(row, col, if (Random.nextFloat() < 0.9f) 2 else 4)
        }
    }

    fun update() {
        if (isMoved) {
            spawnTile()
            isMoved = false
        }
    }

    // ============================= Movement =============================
    fun moveLeft() {
        var moved = false
        for (y in 0 until board.size) {
            val row = board.getRow(y)
            val merged = mergeLineLeft(row)
            for (x in 0 until board.size) {
                if (board.getTile(y, x) != merged[x]) moved = true
                board.setTile(y, x, merged[x])
            }
        }
        isMoved = moved
    }

    fun moveRight() {
        var moved = false
        for (y in 0 until board.size) {
            val row = board.getRow(y)
            val merged = mergeLineRight(row)
            for (x in 0 until board.size) {
                if (board.getTile(y, x) != merged[x]) moved = true
                board.setTile(y, x, merged[x])
            }
        }
        isMoved = moved
    }

    fun moveDown() {
        var moved = false
        for (x in 0 until board.size) {
            val col = board.getCol(x)
            val merged = mergeLineUp(col)
            for (y in 0 until board.size) {
                if (board.getTile(y, x) != merged[y]) moved = true
                board.setTile(y, x, merged[y])
            }
        }
        isMoved = moved
    }

    fun moveUp() {
        var moved = false
        for (x in 0 until board.size) {
            val col = board.getCol(x)
            val merged = mergeLineDown(col)
            for (y in 0 until board.size) {
                if (board.getTile(y, x) != merged[y]) moved = true
                board.setTile(y, x, merged[y])
            }
        }
        isMoved = moved
    }

    // ============================= Merge logic =============================

    // Merge sang trái
    private fun mergeLineLeft(line: List<Int>): List<Int> {
        val compact = line.filter { it != 0 }
        val merged = mutableListOf<Int>()
        var skip = false
        for (i in compact.indices) {
            if (skip) {
                skip = false
                continue
            }
            if (i < compact.size - 1 && compact[i] == compact[i + 1]) {
                val value = compact[i] * 2
                score += value
                merged.add(value)
                skip = true
            } else {
                merged.add(compact[i])
            }
        }
        while (merged.size < board.size) merged.add(0)
        return merged
    }

    // Merge sang phải
    private fun mergeLineRight(line: List<Int>): List<Int> {
        val compact = line.filter { it != 0 }
        val temp = mutableListOf<Int>()
        var i = compact.size - 1
        while (i >= 0) {
            if (i > 0 && compact[i] == compact[i - 1]) {
                val value = compact[i] * 2
                score += value
                temp.add(value)
                i -= 2
            } else {
                temp.add(compact[i])
                i -= 1
            }
        }
        val merged = temp.asReversed().toMutableList()
        while (merged.size < board.size) merged.add(0, 0)
        return merged
    }

    // Merge lên trên
    private fun mergeLineUp(line: List<Int>): List<Int> {
        val compact = line.filter { it != 0 }
        val merged = mutableListOf<Int>()
        var skip = false
        for (i in compact.indices) {
            if (skip) {
                skip = false
                continue
            }
            if (i < compact.size - 1 && compact[i] == compact[i + 1]) {
                val value = compact[i] * 2
                score += value
                merged.add(value)
                skip = true
            } else {
                merged.add(compact[i])
            }
        }
        while (merged.size < board.size) merged.add(0)
        return merged
    }

    // Merge xuống dưới
    private fun mergeLineDown(line: List<Int>): List<Int> {
        val compact = line.filter { it != 0 }
        val temp = mutableListOf<Int>()
        var i = compact.size - 1
        while (i >= 0) {
            if (i > 0 && compact[i] == compact[i - 1]) {
                val value = compact[i] * 2
                score += value
                temp.add(value)
                i -= 2
            } else {
                temp.add(compact[i])
                i -= 1
            }
        }
        val merged = temp.asReversed().toMutableList()
        while (merged.size < board.size) merged.add(0, 0)
        return merged
    }
}
