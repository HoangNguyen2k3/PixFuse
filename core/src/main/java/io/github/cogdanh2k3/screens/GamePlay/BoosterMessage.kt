package io.github.cogdanh2k3.ui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import io.github.cogdanh2k3.utils.FontUtils

class BoosterMessage(
    private val stage: Stage,
    private val boosterTableY: Float,
    private val screenWidth: Float
) {
    private val font = FontUtils.loadCustomFont(25, Color.WHITE)
    private val style = Label.LabelStyle(font, Color.GOLD)
    private val label = Label("", style).apply {
        setFontScale(1.2f)
        isVisible = false
    }

    init {
        stage.addActor(label)
    }

    fun show(message: String) {
        label.setText(message)
        label.pack() // 👈 bắt buộc để update width/height
        label.isVisible = true
        label.color = Color.GOLD

        // Căn giữa theo screenWidth
        val x = (screenWidth - label.width) / 2f
        val y = boosterTableY + 80f
        label.setPosition(x, y)

        label.clearActions()
        label.addAction(
            Actions.sequence(
                Actions.parallel(
                    Actions.moveBy(0f, 40f, 1f),
                    Actions.fadeOut(1f)
                ),
                Actions.run { label.isVisible = false }
            )
        )
    }
}
