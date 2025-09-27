package io.github.cogdanh2k3.Mode
import io.github.cogdanh2k3.DataGame.DataGame
import io.github.cogdanh2k3.game.Board

interface GameMode {
    val name: String
    val data: DataGame
    fun checkWin(board: Board, score: Int): Boolean
    fun checkLose(board: Board, score: Int): Boolean
    fun getTargetDescription(): String
}
// Chế độ Endless
class EndlessMode : GameMode {
    override val name: String = "Endless"
    override val data: DataGame = DataGame()
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
class TargetMode(
    private val targetValues: List<Int>,         // nhiều giá trị mục tiêu
    private val targetNames: List<String> = emptyList() // tên tương ứng (nếu có)
) : GameMode {
    override val name: String = "Target"
    override val data: DataGame = DataGame()

    override fun checkWin(board: Board, score: Int): Boolean {
        // Kiểm tra tất cả targetValues đều xuất hiện
        return targetValues.all { target ->
            var found = false
            for (r in 0 until board.size) {
                for (c in 0 until board.size) {
                    if (board.getTile(r, c).value == target) {
                        found = true
                        break
                    }
                }
                if (found) break
            }
            found
        }
    }

    override fun checkLose(board: Board, score: Int): Boolean {
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
        // Nếu có tên thì ghép (tên + số), nếu không thì chỉ số
        return if (targetNames.isNotEmpty()) {
            targetValues.mapIndexed { i, v ->
                if (i < targetNames.size && targetNames[i].isNotEmpty())
                    "${targetNames[i]} ($v)"
                else
                    v.toString()
            }.joinToString(", ")
        } else {
            targetValues.joinToString(", ")
        }
    }
}
class TimedMode(
    private val durationSeconds: Float = 180f // 3 phút
) : GameMode {
    override val name: String = "Timed"
    override val data: DataGame = DataGame()

    var remainingTime: Float = durationSeconds
        private set

    fun update(delta: Float) {
        if (remainingTime > 0f) {
            remainingTime -= delta
            if (remainingTime < 0f) remainingTime = 0f
        }
    }

    override fun checkWin(board: Board, score: Int): Boolean {
        // Có thể thắng theo score hoặc không, tạm để false
        return false
    }

    override fun checkLose(board: Board, score: Int): Boolean {
        return remainingTime <= 0f
    }

    override fun getTargetDescription(): String {
        val minutes = (remainingTime.toInt() / 60).toString().padStart(2, '0')
        val seconds = (remainingTime.toInt() % 60).toString().padStart(2, '0')
        return "$minutes:$seconds"
    }
}
