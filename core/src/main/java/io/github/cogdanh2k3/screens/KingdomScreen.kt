package io.github.cogdanh2k3.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.viewport.FitViewport
import io.github.cogdanh2k3.Boss
import io.github.cogdanh2k3.DataGame.BossDatabase
import io.github.cogdanh2k3.DataGame.KingdomDatabase
import io.github.cogdanh2k3.DataGame.SaveManager
import io.github.cogdanh2k3.Main
import io.github.cogdanh2k3.Mode.BattleMode
import io.github.cogdanh2k3.screens.GamePlay.GameScreen
import io.github.cogdanh2k3.utils.FontUtils

class KingdomScreen(private val game: Main) : Screen {

    private val stage = Stage(FitViewport(1080f, 1920f))
    private val shapeRenderer = ShapeRenderer()
    private val currentUnlockedIndex = SaveManager.gameSave.int_levelBoss

    // lưu danh sách vị trí tâm các đảo để nối line
    private val islandCenters = mutableListOf<Pair<Float, Float>>()

    override fun show() {
        Gdx.input.inputProcessor = stage

        // --- Background ---
        val bg = Image(Texture("BG/background.png"))
        bg.setFillParent(true)
        stage.addActor(bg)

        // --- Load danh sách Kingdom ---
        val kingdoms = KingdomDatabase.kingdoms
        val startY = stage.height - 400f
        val spacingY = 400f
        val islandSize = Pair(500f, 500f)
        val offsetXAmount = 180f

        kingdoms.forEachIndexed { index, kingdom ->
            val island = Image(Texture(kingdom.islandImage))
            island.setSize(islandSize.first, islandSize.second)

            // Lệch trái/phải xen kẽ
            val offsetX = if (index % 2 == 0) -offsetXAmount else offsetXAmount
            val posX = stage.width / 2f - island.width / 2f + offsetX
            val posY = startY - index * spacingY
            island.setPosition(posX, posY)

            // Lưu lại tâm để vẽ line sau
            val centerX = posX + island.width / 2f
            val centerY = posY + island.height / 2f
            islandCenters.add(centerX to centerY)

            // --- Tên đảo ---
            val nameLabel = Label(
                kingdom.name,
                Label.LabelStyle(FontUtils.loadCustomFont(28), Color.WHITE)
            )
            nameLabel.setAlignment(Align.center)
            nameLabel.setSize(island.width, 40f)
            nameLabel.setPosition(posX, island.y - 50f)

            if (index > currentUnlockedIndex) {
                // Đảo bị khóa
                island.color = Color(0.3f, 0.3f, 0.3f, 0.8f)
                val lock = Image(Texture("UI/lock_icon.png"))
                lock.setSize(100f, 100f)
                lock.setPosition(
                    island.x + island.width / 2f - lock.width / 2f,
                    island.y + island.height / 2f - lock.height / 2f
                )
                stage.addActor(island)
                stage.addActor(lock)
            } else {
                // Đảo mở khóa → hiệu ứng và cho phép click
                island.setOrigin(Align.center)
                island.addAction(
                    Actions.forever(
                        Actions.sequence(
                            Actions.scaleTo(1.08f, 1.08f, 0.8f),
                            Actions.scaleTo(1f, 1f, 0.8f)
                        )
                    )
                )

                island.addListener(object : ClickListener() {
                    override fun clicked(event: InputEvent?, x: Float, y: Float) {
                        val bossData = BossDatabase.getBossForLevel(index)
                        val boss = Boss(
                            name = bossData.name,
                            hp = bossData.hp,
                            texturePath = bossData.texturePath
                        )
                        val mode = BattleMode(boss, bossData.turnAttackBoss,index)
                        game.screen = GameScreen(game, mode,null,index+1)
                    }
                })
                stage.addActor(island)
            }

            stage.addActor(nameLabel)
        }
    }

    override fun render(delta: Float) {
        // --- Update stage ---
        stage.act(delta)

        // --- Vẽ đường nối bằng ShapeRenderer ---
        stage.viewport.apply()
        shapeRenderer.projectionMatrix = stage.camera.combined
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)

        shapeRenderer.color = Color(1f, 1f, 1f, 0.4f)
        for (i in 0 until islandCenters.size - 1) {
            val (x1, y1) = islandCenters[i]
            val (x2, y2) = islandCenters[i + 1]

            // Vẽ line hơi dày (2px)
            drawThickLine(x1, y1 - 80f, x2, y2 + 80f, 20f, Color(1f, 1f, 1f, 1f))
        }

        shapeRenderer.end()

        // --- Vẽ Stage ---
        stage.draw()
    }

    private fun drawThickLine(x1: Float, y1: Float, x2: Float, y2: Float, thickness: Float, color: Color) {
        shapeRenderer.color = color
        val dx = x2 - x1
        val dy = y2 - y1
        val length = kotlin.math.sqrt(dx * dx + dy * dy)
        val angle = kotlin.math.atan2(dy, dx)

        shapeRenderer.identity()
        shapeRenderer.translate(x1, y1, 0f)
        shapeRenderer.rotate(0f, 0f, 1f, Math.toDegrees(angle.toDouble()).toFloat())
        shapeRenderer.rect(0f, -thickness / 2, length, thickness)
    }

    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height, true)
    }

    override fun hide() {}
    override fun pause() {}
    override fun resume() {}

    override fun dispose() {
        stage.dispose()
        shapeRenderer.dispose()
    }
}
