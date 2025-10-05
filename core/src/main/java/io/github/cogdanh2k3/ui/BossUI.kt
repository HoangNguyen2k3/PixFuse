package io.github.cogdanh2k3.ui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.utils.Align
import io.github.cogdanh2k3.Mode.BattleMode
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener

class BossUI(
    private val stage: Stage,
    private val mode: BattleMode
) {
    //-----------------------Boss------------------------
    private val bossImage: Image
    private val hpBarBg: Image
    private val hpBarFill: Image
    private val hpLabel: Label
    private val moveLabel: Label
    private val group: Group

    private val barWidth = 400f
    private val barHeight = 25f
    //--------------------Story--------------------------
    // --- Story Popup ---
    private val storyGroup: Group
    private val storyBg: Image
    private val storyName: Label
    private val storyText: Label

    private var storyLines: List<String> = listOf()
    private var currentLineIndex = 0
    private var storyFullText = ""
    private var storyDisplayIndex = 0
    private var typingSpeed = 0.03f
    private var isTyping = false
    private var storyVisible = false
    private var typingAction: com.badlogic.gdx.scenes.scene2d.Action? = null

    init {
        val bossTex = Texture(mode.boss.texturePath.ifEmpty { "UI/pikachu.png" })
        bossImage = Image(bossTex)
        bossImage.setSize(160f, 160f)
        bossImage.setPosition((stage.width - bossImage.width) / 2, stage.height - 220f)

        // Thanh HP nền
        hpBarBg = Image(Texture("UI/progress2.png"))
        hpBarBg.setSize(barWidth, barHeight)
        hpBarBg.setPosition((stage.width - barWidth) / 2, bossImage.y - 40f)

        // Thanh HP đầy
        hpBarFill = Image(Texture("UI/progress1.png"))
        hpBarFill.setSize(barWidth, barHeight)
        hpBarFill.setPosition(hpBarBg.x, hpBarBg.y)

        // Font rõ ràng hơn
        val font = BitmapFont().apply {
            data.setScale(1.4f) // phóng to chữ
            color = Color.WHITE
        }

        // Viền đen nhẹ cho dễ đọc
        val labelStyle = Label.LabelStyle(font, Color.WHITE)

        // Label HP
        hpLabel = Label("HP: ${mode.boss.currentHP}/${mode.boss.hp}", labelStyle)
        hpLabel.setAlignment(Align.center)
        hpLabel.setSize(barWidth, barHeight)
        hpLabel.setPosition(hpBarBg.x, hpBarBg.y)

        // Label lượt — to, rõ, có đổ bóng
        val moveFont = BitmapFont().apply {
            data.setScale(2.0f)
            color = Color.YELLOW
        }
        val moveStyle = Label.LabelStyle(moveFont, Color.YELLOW)
        moveLabel = Label("Turn Remain: ${mode.remainingMoves}", moveStyle)
        moveLabel.setAlignment(Align.right)
        moveLabel.setSize(300f, 80f)
        moveLabel.setPosition(stage.width - 320f, stage.height - 100f)

        // Gom tất cả vào 1 group
        group = Group()
        group.addActor(bossImage)
        group.addActor(hpBarBg)
        group.addActor(hpBarFill)
        group.addActor(hpLabel)
        group.addActor(moveLabel)
        stage.addActor(group)
        //group.toBack()



        // --- Story Popup ---
        storyGroup = Group()
        storyBg = Image(Texture("UI/dialog_box.png"))
        storyBg.setSize(stage.width * 0.8f, 180f)
        storyBg.setPosition(stage.width * 0.1f, 50f)

        storyName = Label("", Label.LabelStyle(BitmapFont(), Color.SKY))
        storyName.setFontScale(1.3f)
        storyName.setPosition(storyBg.x + 30f, storyBg.y + storyBg.height - 40f)

        storyText = Label("", Label.LabelStyle(BitmapFont(), Color.WHITE))
        storyText.setWrap(true)
        storyText.setWidth(storyBg.width - 60f)
        storyText.setAlignment(Align.topLeft)
        storyText.setPosition(storyBg.x + 30f, storyBg.y + storyBg.height - 80f)

        storyGroup.addActor(storyBg)
        storyGroup.addActor(storyName)
        storyGroup.addActor(storyText)
        storyGroup.isVisible = false
        stage.addActor(storyGroup)

        // Xử lý click để skip / next
        storyGroup.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                if (isTyping) {
                    // Hiện full dòng hiện tại
                    storyText.setText(storyFullText)
                    isTyping = false
                } else {
                    // Chuyển sang dòng tiếp
                    nextStoryLine()
                }
            }
        })
    }

    fun getBossCenterPosition(): Pair<Float, Float> {
        return Pair(bossImage.x + bossImage.width / 2f, bossImage.y + bossImage.height / 2f)
    }

    fun updateUI() {
        val hpPercent = mode.boss.currentHP.toFloat() / mode.boss.hp
        hpBarFill.setSize(barWidth * hpPercent, barHeight)
        hpLabel.setText("HP: ${mode.boss.currentHP}/${mode.boss.hp}")

        moveLabel.setText("Turn Remain: ${mode.remainingMoves}")
    }

    fun hide() { group.isVisible = false }
    fun show() { group.isVisible = true }











    fun showStoryPopup(bossName: String, textLines: List<String>) {
        storyLines = textLines
        currentLineIndex = 0
        storyVisible = true
        storyName.setText(bossName)
        storyGroup.isVisible = true
        storyGroup.color.a = 0f
        storyGroup.addAction(Actions.fadeIn(0.3f))

        showLine(storyLines[currentLineIndex])
    }

    private fun showLine(line: String) {
        // Hủy hành động gõ cũ (nếu có)
        typingAction?.let { stage.root.removeAction(it) }

        storyFullText = line
        storyDisplayIndex = 0
        storyText.setText("")
        isTyping = true

        // Hiệu ứng gõ chữ dần
        typingAction = Actions.forever(Actions.sequence(
            Actions.delay(typingSpeed),
            Actions.run {
                if (isTyping && storyDisplayIndex < storyFullText.length) {
                    storyText.setText(storyText.text.toString() + storyFullText[storyDisplayIndex])
                    storyDisplayIndex++
                } else {
                    isTyping = false
                }
            }
        ))

        stage.addAction(typingAction)
    }

    private fun nextStoryLine() {
        if (!storyVisible) return
        currentLineIndex++
        if (currentLineIndex >= storyLines.size) {
            hideStory()
        } else {
            showLine(storyLines[currentLineIndex])
        }
    }

    fun hideStory() {
        storyGroup.addAction(Actions.sequence(
            Actions.fadeOut(0.3f),
            Actions.run { storyGroup.isVisible = false }
        ))
        storyVisible = false
    }
}

