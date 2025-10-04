package io.github.cogdanh2k3.Mode
import io.github.cogdanh2k3.DataGame.DataGame
import io.github.cogdanh2k3.game.Board

interface GameMode {
    val name: String
    val data: DataGame
    fun checkWin(board: Board, score: Int): Boolean
    fun checkLose(board: Board, score: Int): Boolean
    fun getTargetDescription(): String
    fun init()
    fun specialEffect()
}
// Chế độ Endless
class EndlessMode : GameMode {
    override val name: String = "Endless"
    override val data: DataGame = DataGame()
    override fun checkWin(board: Board, score: Int): Boolean {
        // Endless không có win
        return false
    }

    override fun specialEffect() {
    }
    override fun init() {
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
    override fun init() {
    }
    override fun specialEffect() {
    }
    override val name: String = "Target"
    override val data: DataGame = DataGame()

    override fun checkWin(board: Board, score: Int): Boolean {
        // Lấy tất cả giá trị trên board
        val boardValues = mutableListOf<Int>()
        for (r in 0 until board.size) {
            for (c in 0 until board.size) {
                val v = board.getTile(r, c).value
                if (v > 0) boardValues.add(v)
            }
        }

        // Sắp xếp giảm dần để ưu tiên match số lớn
        boardValues.sortDescending()

        // Copy target để check
        val neededTargets = targetValues.sortedDescending().toMutableList()

        // Duyệt từng target
        for (target in neededTargets) {
            // Tìm 1 ô trên board >= target
            val idx = boardValues.indexOfFirst { it >= target }
            if (idx == -1) {
                return false // không tìm được → thua
            } else {
                boardValues.removeAt(idx) // dùng ô này rồi thì bỏ đi
            }
        }

        return true // tất cả target đều match
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
    private fun valueToRoman(value: Int): String {
        if (value < 2) return "?"
        // log2(value) = bậc (2=2^1, 4=2^2, 8=2^3, ...)
        val level = (Math.log(value.toDouble()) / Math.log(2.0)).toInt()
        val romans = listOf(
            "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X",
            "XI", "XII", "XIII", "XIV", "XV"
        )
        return if (level - 1 in romans.indices) romans[level - 1] else "?"
    }
    override fun getTargetDescription(): String {
        return if (targetNames.isNotEmpty()) {
            targetValues.mapIndexed { i, v ->
                val roman = valueToRoman(v)
                if (i < targetNames.size && targetNames[i].isNotEmpty())
                    "${targetNames[i]} ($roman)"
                else
                    roman
            }.joinToString(", ")
        } else {
            targetValues.joinToString(", ") { valueToRoman(it) }
        }
    }
}
class TimedMode(
     val durationSeconds: Float = 33f // 3 phút
) : GameMode {
    override fun init() {
        remainingTime = durationSeconds
    }
    override fun specialEffect() {
        remainingTime += durationSeconds/60
    }
    override val name: String = "Timed"
    override val data: DataGame = DataGame()

    var remainingTime: Float = durationSeconds
        public set

    fun update(delta: Float) {
        if (remainingTime > 0f) {
            remainingTime -= delta
            if (remainingTime < 0f) remainingTime = 0f

        }

        // kiểm tra nếu hết giờ
        if (remainingTime <= 0f) {
            println("Time's up!")
            // gọi game over hoặc lose
            // gameOver()
        }
    }

    override fun checkWin(board: Board, score: Int): Boolean {
        // Có thể thắng theo score hoặc không, tạm để false
        return false
    }

    override fun checkLose(board: Board, score: Int): Boolean {
        println("Remaining Time: $remainingTime") // log
        val checklose = remainingTime <= 0f
        return checklose
    }

    override fun getTargetDescription(): String {
        val minutes = (remainingTime.toInt() / 60).toString().padStart(2, '0')
        val seconds = (remainingTime.toInt() % 60).toString().padStart(2, '0')
        return "$minutes:$seconds"
    }
}
