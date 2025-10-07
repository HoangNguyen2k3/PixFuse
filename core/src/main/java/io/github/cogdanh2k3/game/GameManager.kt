package io.github.cogdanh2k3.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.Timer
import io.github.cogdanh2k3.DataGame.LevelData
import io.github.cogdanh2k3.Mode.BattleMode
import io.github.cogdanh2k3.Mode.GameMode
import io.github.cogdanh2k3.Mode.TimedMode
import io.github.cogdanh2k3.audio.SoundId
import io.github.cogdanh2k3.audio.SoundManager
import io.github.cogdanh2k3.screens.GamePlay.GameScreen
import io.github.cogdanh2k3.ui.BossUI
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
    //-------------------Booster-----------------------
    var doubleNextMerge: Boolean = false
    var activeWallBooster: Boolean = false
    var activeBombRowBooster: Boolean = false
    public lateinit var bossUI: BossUI
    lateinit var stage: Stage
    fun InitData(){
        if(levelData != null && levelData.id != -1){
            board.tileImages = mode.data.themes[levelData.currentWorld-1].images
            board.LEVEL_WALLS=levelData.wallData
            board.InitGrid()
        }
    }
/*fun spawnTile() {
    if (hasWon || hasLost) return
    val empty = board.getEmptyCells()
    if (empty.isNotEmpty()) {
        val (r, c) = empty.random()
        val value = if (Random.nextFloat() < 0.9f) 2 else 4

        // Xác suất spawn tile đóng băng (20%)
        val frozen = if (Random.nextFloat() < 0.2f) 2 else 0

        // Xác suất spawn trap boom (10%)
        val isBoom = Random.nextFloat() < 0.1f

        board.setTile(r, c, Tile(
            value = value,
            frozen = frozen,
            isBoom = isBoom,
            boomCounter = if (isBoom) 2 else 0
        ))
        board.addSpawnAnim(r, c, value)
    }
}*/

    fun spawnTile() {
    if (hasWon || hasLost) return
    val empty = board.getEmptyCells()
    if (empty.isEmpty()) return

    val (r, c) = empty.random()

    // an toàn: double-check ô vẫn trống (defensive)
    val existing = board.getTile(r, c)
    if (existing.value != 0 || existing.frozen != 0 || existing.isBoom) {
        // shouldn't happen, log và abort
        com.badlogic.gdx.Gdx.app.log("spawnTile", "skipping spawn: cell not empty at $r,$c")
        return
    }

    val value = if (Random.nextFloat() < 0.9f) 2 else 4
    val p = Random.nextFloat()

    when {
        p < 0.10f -> {
            // BOOM (10%)
            val tile = Tile(value = value, frozen = 0, isBoom = true, boomCounter = 3,isThunder = false, thunderCounter = 0)
            board.setTile(r, c, tile)
            board.addSpawnAnim(r, c, value)
        }
        p < 0.20f -> {
            // FROZEN (20%)
            val tile = Tile(value = value, frozen = 3, isBoom = false, boomCounter = 0, isThunder = false, thunderCounter = 0)
            board.setTile(r, c, tile)
            board.addSpawnAnim(r, c, value)
        }
        p < 0.30f -> {
            // FROZEN (20%)
            val tile = Tile(value = value, frozen = 0, isBoom = false, boomCounter = 0,isThunder = true, thunderCounter = 3)
            board.setTile(r, c, tile)
            board.addSpawnAnim(r, c, value)
        }
        else -> {
            // NORMAL
            val tile = Tile(value = value, frozen = 0, isBoom = false, boomCounter = 0,isThunder = false, thunderCounter = 0)
            board.setTile(r, c, tile)
            board.addSpawnAnim(r, c, value)
        }
    }
}
    fun spawnTileBegin() {
        if (hasWon || hasLost) return
        val empty = board.getEmptyCells()
        if (empty.isEmpty()) return

        val (r, c) = empty.random()

        // an toàn: double-check ô vẫn trống (defensive)
        val existing = board.getTile(r, c)
        if (existing.value != 0 || existing.frozen != 0 || existing.isBoom) {
            // shouldn't happen, log và abort
            com.badlogic.gdx.Gdx.app.log("spawnTile", "skipping spawn: cell not empty at $r,$c")
            return
        }
        val value = if (Random.nextFloat() < 0.9f) 2 else 4
                // NORMAL
                val tile = Tile(value = value, frozen = 0, isBoom = false, boomCounter = 0,isThunder = false, thunderCounter = 0)
                board.setTile(r, c, tile)
                board.addSpawnAnim(r, c, value)
    }
    fun update() {
        if(hasWon||hasLost) return
        if(mode.name=="Timed"){
            println("Remaining Time:") // log
            checkLose()
        }
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
/*        if (board.getEmptyCells().isNotEmpty()) return
        println("Remaining Time: 2") // log*/
        // không còn ô trống → check merge được nữa khôngif
        if (mode.checkLose(board,score)){
            SoundManager.playSfx(SoundId.LOSE)
            hasLost = true
            return
        }
/*        for (r in 0 until board.size) {
            for (c in 0 until board.size) {
                val v = board.getTile(r, c)
                if (r + 1 < board.size && v == board.getTile(r + 1, c)) return
                if (c + 1 < board.size && v == board.getTile(r, c + 1)) return
            }
        }
        SoundManager.playSfx(SoundId.LOSE)
        hasLost = true*/
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
                // Phát hiện movement: so sánh value + flags có thể gây false positive nếu chỉ so value
                if (oldTile.value != newTile.value || oldTile.isBoom != newTile.isBoom
                    || oldTile.isThunder != newTile.isThunder || oldTile.boomCounter != newTile.boomCounter
                    || oldTile.thunderCounter != newTile.thunderCounter) {
                    moved = true
                }
                // Giữ frozen của ô cũ (như logic cũ) nhưng dùng các flag từ newTile
                board.setTile(r, c, newTile.copy(frozen = oldTile.frozen))
            }
        }

        if (moved) {
            if (doubleNextMerge == true) {
                doubleNextMerge = false
            }
            SoundManager.playSfx(SoundId.SWOOSH)
            reduceSpecialTiles() // ✅ chỉ giảm khi có movement thật
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
                if (oldTile.value != newTile.value || oldTile.isBoom != newTile.isBoom
                    || oldTile.isThunder != newTile.isThunder || oldTile.boomCounter != newTile.boomCounter
                    || oldTile.thunderCounter != newTile.thunderCounter) {
                    moved = true
                }
                board.setTile(r, c, newTile.copy(frozen = oldTile.frozen))
            }
        }
        if (moved) {
            if (doubleNextMerge == true) {
                doubleNextMerge = false
            }
            SoundManager.playSfx(SoundId.SWOOSH)
            reduceSpecialTiles()
        }
        isMoved = moved
    }

    // ✅ Hàm giảm frozen / countdown sau khi move
    private fun reduceSpecialTiles() {
        for (r in 0 until board.size) {
            for (c in 0 until board.size) {
                val t = board.getTile(r, c)

                // giảm frozen (chỉ ở đây)
                if (t.frozen > 0) {
                    val newFrozen = t.frozen - 1
                    board.setTile(r, c, t.copy(frozen = newFrozen))
                    if (newFrozen == 0) {
                        SoundManager.playVibration(200)
                        SoundManager.playSfx(SoundId.ICECRACK)
                        board.addExplosionIceThaw(r, c)
                    }
                }

                // Xử lý boom (đếm ngược, nổ khi = 0)
                if (t.isBoom) {
                    val newBoom = t.boomCounter - 1
                    if (newBoom <= 0) {
                        // nổ khi hết counter
                        explode(r, c)
                        SoundManager.playVibration(200)
                        SoundManager.playSfx(SoundId.BOMB)
                    } else {
                        board.setTile(r, c, t.copy(boomCounter = newBoom))
                    }
                }

                // Xử lý thunder: nếu countdown về 0 mà không được merge thì CHỈ mất trạng thái thunder (không kích hoạt buff)
                if (t.isThunder) {
                    val newThunder = t.thunderCounter - 1
                    if (newThunder <= 0) {
                        board.setTile(r, c, t.copy(isThunder = false, thunderCounter = 0))
                    } else {
                        board.setTile(r, c, t.copy(thunderCounter = newThunder))
                    }
                }
            }
        }
    }

    private fun triggerThunderBuff(r: Int, c: Int) {
        if (r !in 0 until board.size || c !in 0 until board.size) return

        val thunderTile = board.getTile(r, c)
        board.addExplosionThunder(r, c)
        SoundManager.playVibration(250)
        SoundManager.playSfx(SoundId.THUNDER)
        val toDouble = mutableSetOf<Pair<Int, Int>>() // lưu tất cả ô sẽ nhân đôi

        // --- Hàng ---
        for (i in 0 until board.size) {
            if (i == c) continue // bỏ trung tâm (xử lý riêng)
            val tile = board.getTile(r, i)
            if (tile.value > 0 && tile.value != TILE_WALL) {
                toDouble.add(r to i)
            }
            if(tile.value!=TILE_WALL){
                board.addExplosionThunder(r, i)
            }
        }

        // --- Cột ---
        for (i in 0 until board.size) {
            if (i == r) continue
            val tile = board.getTile(i, c)
            if (tile.value > 0 && tile.value != TILE_WALL) {
                toDouble.add(i to c)
            }
            if(tile.value!=TILE_WALL){
                board.addExplosionThunder(i, c)
            }
        }

        // --- Nhân giá trị thật ---
        for ((rr, cc) in toDouble) {
            val t = board.getTile(rr, cc)
            val newVal = t.value * 2
            board.setTile(rr, cc, t.copy(value = newVal))

        }

        // Trung tâm thunder tile -> giữ lại, bỏ trạng thái thunder
        val center = board.getTile(r, c)
        board.setTile(r, c, center.copy(isThunder = false, thunderCounter = 0))
    }



    private fun explode(r: Int, c: Int) {
        for (dr in -1..1) {
            for (dc in -1..1) {
                val nr = r + dr
                val nc = c + dc

                if (nr in 0 until board.size && nc in 0 until board.size) {
                    val t = board.getTile(nr, nc)
                    // CHỈ xóa/hiệu ứng nếu ô không phải wall
                    if (t.value != TILE_WALL) {
                        board.setTile(nr, nc, Tile(0))
                        board.addExplosionBoom(nr, nc)
                    }
                }
            }
        }

        board.addExplosionBoom(r, c)
        SoundManager.playVibration(300)

    }

    private fun processLine(
        line: List<Tile>,
        reversed: Boolean,
        index: Int,
        isRow: Boolean
    ): List<Tile> {

        val work = if (reversed) line.reversed() else line
        val final = work.map { it.copy() }.toMutableList()

        data class MoveAction(val from: Int, val to: Int, val value: Int, val merged: Boolean, val isThunderMerged: Boolean = false)
        val moveActions = mutableListOf<MoveAction>()

        var start = 0
        while (start < work.size) {
            // Nếu là wall hoặc tile đóng băng thì bỏ qua (đóng băng coi như wall)
            if (work[start].value == TILE_WALL || work[start].frozen > 0) {
                // **KHÔNG giảm frozen ở đây** (việc giảm làm ở reduceSpecialTiles)
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
                    val (nextTile, nextPos) = compact[i + 1]

                    if (nextTile.frozen == 0 && tile.value == nextTile.value) {
                        // merge hợp lệ
                        var mergedValue = tile.value * 2
                        if (doubleNextMerge == true) {
                            mergedValue *= 2
                        }
                        score += mergedValue

                        // kiểm tra xem có thunder trong 2 tile tham gia merge không
                        val isThunderMerge = tile.isThunder || nextTile.isThunder

                        // kết quả merge -> tile bình thường (không carry thunder/boom)
                        //mergedList.add(Tile(mergedValue, 0))
                        mergedList.add(Tile(mergedValue, 0, isThunder = isThunderMerge))
                        // ghi moveActions: 2 action (đầu là from tile1 -> to, thứ hai là tile2 -> to (merged))
                        moveActions.add(MoveAction(compact[i].second, writeIndex, tile.value, merged = false))
                        moveActions.add(MoveAction(compact[i + 1].second, writeIndex, nextTile.value, merged = true, isThunderMerged = isThunderMerge))

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

        // --- Tạo animation + trigger thunder nếu merge có thunder ---
        for (action in moveActions) {
            if (action.from == action.to) continue

            if (isRow) {
                val fromC = if (reversed) board.size - 1 - action.from else action.from
                val toC = if (reversed) board.size - 1 - action.to else action.to
                board.addMoveAnim(action.value, index, fromC, index, toC)

                if (action.merged) {
                    SoundManager.playVibration(100)
                    board.addExplosion(index, toC)
                    board.addMergeAnim(index, toC, action.value * 2)
                    SoundManager.playSfx(SoundId.MERGE)
                    mode.specialEffect()
if(mode is BattleMode){
    processInBattleMode(stage, index, toC)
}
                    // Nếu merge này bao gồm thunder => trigger thunder buff tại ô merge (index,toC)
                    if (action.isThunderMerged) {
                        triggerThunderBuff(index, toC)
                    }
                }
            } else {
                val fromR = if (reversed) board.size - 1 - action.from else action.from
                val toR = if (reversed) board.size - 1 - action.to else action.to
                board.addMoveAnim(action.value, fromR, index, toR, index)

                if (action.merged) {
                    SoundManager.playVibration(100)
                    board.addExplosion(toR, index)
                    board.addMergeAnim(toR, index, action.value * 2)
                    SoundManager.playSfx(SoundId.MERGE)
                    mode.specialEffect()
                    if(mode is BattleMode){
                        processInBattleMode(stage, toR, index)
                    }
                    if (action.isThunderMerged) {
                        triggerThunderBuff(toR, index)
                    }
                }
            }
        }
        if (mode is BattleMode) {
            mode.applyQueuedDamages(board, stage, bossUI)
        }
        return output
    }
    fun processInBattleMode(stage: Stage, r: Int, c: Int) {
        if (mode is BattleMode) {
            val tile = board.getTile(r, c)
            val (tileX, tileY) = board.getTilePosition(r, c)
            val value = board.getTile(r, c).value.takeIf { it > 0 } ?: 2 // fallback tránh 0
            val damage = value * mode.boss.damageMultiplier

            mode.queueAttack(tileX, tileY, tileX, tileY, damage)
            mode.onMoveUsed()

            when {
                mode.checkWin(board, score) -> mode.onBossDefeated()
                mode.checkLose(board, score) -> mode.onLose()
            }
        }
    }









}
