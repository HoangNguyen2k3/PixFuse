package io.github.cogdanh2k3.Mode
import io.github.cogdanh2k3.game.Board

interface GameMode {
    val name: String
    fun checkWin(board: Board, score: Int): Boolean
    fun checkLose(board: Board, score: Int): Boolean
    fun getTargetDescription(): String
}
// Chế độ Endless
class EndlessMode : GameMode {
    override val name: String = "Endless"

    override fun checkWin(board: Board, score: Int): Boolean {
        // Endless không có win
        return false
    }

    override fun checkLose(board: Board, score: Int): Boolean {
        // Lose khi không còn ô trống và không merge được
        if (board.getEmptyCells().isNotEmpty()) return false
        for (r in 0 until board.size) {
            for (c in 0 until board.size) {
                val v = board.getTile(r, c)
                if (r + 1 < board.size && v == board.getTile(r + 1, c)) return false
                if (c + 1 < board.size && v == board.getTile(r, c + 1)) return false
            }
        }
        return true
    }

    override fun getTargetDescription(): String {
        return "Endless Mode" // Hiển thị vô cực
    }
}
// Chế độ Target (ví dụ: đạt 64 để thắng)
class TargetMode(private val targetValue: Int, private val targetName: String = "") : GameMode {
    override val name: String = "Target"

    override fun checkWin(board: Board, score: Int): Boolean {
        for (r in 0 until board.size) {
            for (c in 0 until board.size) {
                if (board.getTile(r, c) == targetValue) {
                    return true
                }
            }
        }
        return false
    }

    override fun checkLose(board: Board, score: Int): Boolean {
        // Giống như Endless → lose khi không còn nước đi
        if (board.getEmptyCells().isNotEmpty()) return false
        for (r in 0 until board.size) {
            for (c in 0 until board.size) {
                val v = board.getTile(r, c)
                if (r + 1 < board.size && v == board.getTile(r + 1, c)) return false
                if (c + 1 < board.size && v == board.getTile(r, c + 1)) return false
            }
        }
        return true
    }

    override fun getTargetDescription(): String {
        // Có thể hiển thị số hoặc tên con vật
        return if (targetName.isNotEmpty()) "$targetName ($targetValue)" else targetValue.toString()
    }
}
