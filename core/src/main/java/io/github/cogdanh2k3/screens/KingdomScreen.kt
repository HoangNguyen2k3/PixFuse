package io.github.cogdanh2k3.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
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

class KingdomScreen(val game: Main) : Screen {

    private val stage = Stage(FitViewport(1080f, 1920f))
    val currentUnlockedIndex = SaveManager.gameSave.int_levelBoss
    override fun show() {
        Gdx.input.inputProcessor = stage
        val bg = Image(Texture("UI/kingdom_bg.png"))
        bg.setFillParent(true)
        stage.addActor(bg)

        val kingdoms = KingdomDatabase.kingdoms

        var offsetX = -200f
        kingdoms.forEachIndexed { index, kingdom ->
            val island = Image(Texture(kingdom.islandImage))
            island.setSize(400f, 220f)
            island.setPosition(stage.width / 2f + offsetX, stage.height - 400f - index * 300f)

            val bossImg = Image(Texture(kingdom.bossImage))
            bossImg.setSize(120f, 120f)
            bossImg.setPosition(island.x + 140f, island.y + 90f)

            val nameLabel = Label(
                kingdom.name,
                Label.LabelStyle(FontUtils.loadCustomFont(22), Color.WHITE)
            )
            nameLabel.setPosition(island.x + 100f, island.y - 20f)
            nameLabel.setAlignment(Align.center)

            if (index > currentUnlockedIndex) {
                // đảo bị khóa
                island.color = Color(0.3f, 0.3f, 0.3f, 0.8f)
                bossImg.color = Color(0.3f, 0.3f, 0.3f, 0.8f)
                val lock = Image(Texture("UI/lock_icon.png"))
                lock.setSize(80f, 80f)
                lock.setPosition(island.x + 160f, island.y + 80f)
                stage.addActor(lock)
            } else {
                // đảo mở khóa → cho phép click
                island.addAction(Actions.forever(Actions.sequence(
                    Actions.scaleTo(1.05f, 1.05f, 1f),
                    Actions.scaleTo(1f, 1f, 1f)
                )))

                island.addListener(object : ClickListener() {
                    override fun clicked(event: InputEvent?, x: Float, y: Float) {
                        val bossData = BossDatabase.getBossForLevel(SaveManager.gameSave.int_levelBoss)
                        val boss = Boss(
                            name = bossData.name,
                            hp = bossData.hp,
                            texturePath = bossData.texturePath
                        )
                        val mode = BattleMode(boss,bossData.turnAttackBoss)

                        // TODO: Thay bằng BattleMode thực tế sau
                        game.screen = GameScreen(game, mode)
                    }
                })
            }

            stage.addActor(island)
            stage.addActor(bossImg)
            stage.addActor(nameLabel)

            offsetX *= -1f
        }

    }

    override fun render(delta: Float) {
        stage.act(delta)
        stage.draw()
    }

    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height, true)
    }

    override fun hide() {}
    override fun pause() {}
    override fun resume() {}
    override fun dispose() {
        stage.dispose()
    }
}
