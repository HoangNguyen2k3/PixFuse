package io.github.cogdanh2k3.Mode
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import io.github.cogdanh2k3.Boss
import io.github.cogdanh2k3.DataGame.DataGame
import io.github.cogdanh2k3.audio.SoundId
import io.github.cogdanh2k3.audio.SoundManager
import io.github.cogdanh2k3.game.Board
import io.github.cogdanh2k3.ui.BossUI
import io.github.cogdanh2k3.utils.FontUtils

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
        val hasUsableEmpty = board.getEmptyCells().any { (r, c) ->
            val tile = board.getTile(r, c)
            tile.frozen <= 0   // ô trống usable nếu frozen <= 0
        }
        if (hasUsableEmpty) return false  // còn ít nhất 1 ô trống usable → chưa thua
        for (r in 0 until board.size) {
            for (c in 0 until board.size) {
                val v = board.getTile(r, c)
                if (r + 1 < board.size && v == board.getTile(r + 1, c)&&(v.frozen<=0&&board.getTile(r + 1, c).frozen<=0)) return false
                if (c + 1 < board.size && v == board.getTile(r, c + 1)&&(v.frozen<=0&&board.getTile(r + 1, c).frozen<=0)) return false
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
) : GameMode
{
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
/*    override fun checkLose(board: Board, score: Int): Boolean {
        if (board.getEmptyCells().isNotEmpty()) return false
        for (r in 0 until board.size) {
            for (c in 0 until board.size) {
                val v = board.getTile(r, c)
                if (r + 1 < board.size && v == board.getTile(r + 1, c)) return false
                if (c + 1 < board.size && v == board.getTile(r, c + 1)) return false
            }
        }
        return true
    }*/
override fun checkLose(board: Board, score: Int): Boolean {
    // Lose khi không còn ô trống và không merge được
    val hasUsableEmpty = board.getEmptyCells().any { (r, c) ->
        val tile = board.getTile(r, c)
        tile.frozen <= 0   // ô trống usable nếu frozen <= 0
    }
    if (hasUsableEmpty) return false  // còn ít nhất 1 ô trống usable → chưa thua
    for (r in 0 until board.size) {
        for (c in 0 until board.size) {
            val v = board.getTile(r, c)
            if (r + 1 < board.size && v == board.getTile(r + 1, c)&&(v.frozen<=0&&board.getTile(r + 1, c).frozen<=0)) return false
            if (c + 1 < board.size && v == board.getTile(r, c + 1)&&(v.frozen<=0&&board.getTile(r + 1, c).frozen<=0)) return false
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
) : GameMode
{
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

class BattleMode(
    val boss: Boss,
    val maxMoves: Int = 25
) : GameMode
{

    override val name: String = "Battle"
    override val data: DataGame = DataGame()

    var remainingMoves = maxMoves
        private set

    // ✅ Hàng đợi damage
    private val pendingAttacks = mutableListOf<PendingAttack>()

    data class PendingAttack(
        val fromX: Float,
        val fromY: Float,
        val toX: Float,
        val toY: Float,
        val damage: Int
    )

    override fun init() {
        remainingMoves = maxMoves
        boss.currentHP = boss.hp
        pendingAttacks.clear()
    }

    fun onMoveUsed() {
        remainingMoves--
    }

    override fun specialEffect() {}
    override fun checkWin(board: Board, score: Int): Boolean = boss.isDefeated()
    override fun checkLose(board: Board, score: Int): Boolean = remainingMoves <= 0 && !boss.isDefeated()

    override fun getTargetDescription(): String {
        return "${boss.name}: ${boss.currentHP}/${boss.hp} HP  |  Lượt: $remainingMoves"
    }

    // ✅ Thêm cả tọa độ boss (toX, toY)
    fun queueAttack(fromX: Float, fromY: Float, toX: Float, toY: Float, damage: Int) {
        if (damage > 0) {
            pendingAttacks.add(PendingAttack(fromX, fromY, toX, toY, damage))
        }
    }

    // ✅ Thực thi các đòn tấn công
    fun applyQueuedDamages(board: Board, stage: Stage, bossUI: BossUI? = null) {
        val bossPos = bossUI?.getBossCenterPosition() ?: Pair(stage.width / 2f, stage.height - 200f)
        val (bossX, bossY) = bossPos

        for (attack in pendingAttacks) {
            addDamageEffect(board, stage, attack.fromX, attack.fromY, bossX, bossY, attack.damage)
            boss.takeDamage(attack.damage)
        }

        if (pendingAttacks.isNotEmpty()) {
            SoundManager.playSfx(SoundId.MERGE)
        }

        pendingAttacks.clear()
        bossUI?.updateUI()

        if (boss.isDefeated()) onBossDefeated()
        else if (remainingMoves <= 0) onLose()
    }

    private fun addDamageEffect(
        board: Board,
        stage: Stage,
        fromX: Float,
        fromY: Float,
        toX: Float,
        toY: Float,
        damage: Int
    ) {
        val bullet = Image(Texture("Boss/bulletBoss.png"))
        bullet.setSize(80f, 80f)
        bullet.setOrigin(Align.center)
        bullet.setPosition(fromX - bullet.width / 2f, fromY - bullet.height / 2f)
        stage.addActor(bullet)
        bullet.toFront()

        // ✅ Tính độ cao bay động:
        // bay lên đến khoảng 85% chiều cao stage (gần đỉnh)
        val targetY = stage.height * 0.85f
        val distanceY = (targetY - fromY).coerceAtLeast(100f) // tối thiểu bay 100px

        bullet.addAction(
            Actions.sequence(
                Actions.parallel(
                    Actions.moveBy(0f, distanceY, 0.7f, Interpolation.sineOut),
                    Actions.rotateBy(360f, 0.7f)
                ),
                Actions.fadeOut(0.2f),
                Actions.run {
                    // Vụ nổ tại vị trí viên đạn biến mất (đỉnh đường bay)
                    board.addExplosionBoomBoss(fromX, fromY + distanceY)
                    bullet.remove()
                }
            )
        )

        // ✅ Floating damage text bay cùng khu vực nổ
        val label = Label("-$damage", Label.LabelStyle(BitmapFont(), Color.RED))
        label.setFontScale(1.2f)
        label.setPosition(fromX - label.width / 2f, fromY + distanceY)
        stage.addActor(label)
        label.toFront()
        label.addAction(
            Actions.sequence(
                Actions.parallel(
                    Actions.moveBy(0f, 100f, 1f, Interpolation.sineOut),
                    Actions.fadeOut(0.5f)
                ),
                Actions.run { label.remove() }
            )
        )
    }



    fun onBossDefeated() {
        println("🎉 Boss ${boss.name} đã bị đánh bại!")
    }

    fun onLose() {
        println("💀 Thua! Hết lượt, boss ${boss.name} còn ${boss.currentHP} HP.")
    }
}

