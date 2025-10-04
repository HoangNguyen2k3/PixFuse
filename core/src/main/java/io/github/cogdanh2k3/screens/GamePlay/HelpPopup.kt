package io.github.cogdanh2k3.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.video.VideoPlayer
import com.badlogic.gdx.video.VideoPlayerCreator

class HelpPopup(private val stage: Stage) {
    val skin = Skin().apply {
        val font = BitmapFont()
        add("default-font", font)

        // WindowStyle cần có cho Dialog
        add("default", Window.WindowStyle(font, Color.WHITE, TextureRegionDrawable(Texture("UI/button.png"))))

        // TextButton style
        add("default", TextButton.TextButtonStyle().apply {
            this.font = font
            this.fontColor = Color.WHITE
            val drawable = TextureRegionDrawable(Texture("UI/button.png"))
            up = drawable
            down = drawable.tint(Color.GRAY)
        })
    }
    private var dialog: Dialog? = null
    private var videoPlayer: VideoPlayer? = null

    fun showMainMenu() {
        dialog?.remove()
        dialog = Dialog("HƯỚNG DẪN", skin)
        dialog?.contentTable?.apply {
            pad(20f)
            defaults().width(250f).height(60f).pad(10f)

            add(makeButton("💣 Trap Bomb") { showVideo("video/videote.mp4") }).row()
            add(makeButton("🧱 Wall") { showVideo("video/videote.mp4") }).row()
            add(makeButton("⚡ Booster x2") { showVideo("video/videote.mp4") }).row()
            add(makeButton("✖ Đóng") { dialog?.hide() }).row()
        }

        dialog?.show(stage)
    }

    private fun makeButton(text: String, action: () -> Unit): TextButton {
        return TextButton(text, skin).apply {
            label.setAlignment(Align.center)
            addListener { action(); true }
        }
    }

    private fun showVideo(videoPath: String) {
        dialog?.remove()

        dialog = Dialog("XEM HƯỚNG DẪN", skin)
        dialog?.contentTable?.apply {
            pad(15f)

            // Hình đại diện (ảnh thumbnail) nếu videoPlayer chưa sẵn sàng
            val preview = Image(Texture("UI/button.png"))
            add(preview).width(400f).height(300f).row()

            val backBtn = TextButton("⬅ Quay lại", skin)
            add(backBtn).padTop(10f).row()
            backBtn.addListener { showMainMenu(); true }
        }

        // Dùng VideoPlayer để phát video
        videoPlayer = VideoPlayerCreator.createVideoPlayer()
        videoPlayer?.play(Gdx.files.internal(videoPath))

        dialog?.show(stage)
    }

    fun update() {
        videoPlayer?.update()
    }

    fun dispose() {
        videoPlayer?.dispose()
    }
}
