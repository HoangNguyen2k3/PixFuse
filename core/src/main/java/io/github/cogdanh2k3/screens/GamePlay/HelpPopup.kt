package io.github.cogdanh2k3.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.video.VideoPlayer
import com.badlogic.gdx.video.VideoPlayerCreator
import io.github.cogdanh2k3.utils.FontUtils

class HelpPopup(private val stage: Stage) {

    // --- Skin tự thiết kế với font lớn ---
    private val skin = Skin().apply {
        // Font mặc định
        val fontDefault = FontUtils.loadCustomFont(24, Color.WHITE)
        add("default-font", fontDefault)

        // Font lớn cho title
        val titleFont = BitmapFont().apply {
            data.setScale(2f) // scale lên 2 lần
        }
        add("title-font", titleFont)

        // Window style
        val windowPixmap = Pixmap(1,1, Pixmap.Format.RGBA8888).apply {
            setColor(Color(0.12f, 0.12f, 0.12f, 0.95f))
            fill()
        }
        add("default", Window.WindowStyle(titleFont, Color.WHITE, TextureRegionDrawable(Texture(windowPixmap))))

        // Button style với font lớn hơn
        val btnUpPixmap = Pixmap(1,1, Pixmap.Format.RGBA8888).apply {
            setColor(Color(0.2f,0.2f,0.25f,1f))
            fill()
        }
        val btnDownPixmap = Pixmap(1,1, Pixmap.Format.RGBA8888).apply {
            setColor(Color(0.35f,0.35f,0.4f,1f))
            fill()
        }
        add("default", TextButton.TextButtonStyle().apply {
            font = BitmapFont().apply { data.setScale(1.5f) } // scale nút
            fontColor = Color.WHITE
            up = TextureRegionDrawable(Texture(btnUpPixmap))
            down = TextureRegionDrawable(Texture(btnDownPixmap))
        })

        // Label mô tả nhỏ
        add("desc-label", Label.LabelStyle(fontDefault, Color.LIGHT_GRAY))

        // ScrollPane style
        val scrollBg = Pixmap(1,1, Pixmap.Format.RGBA8888).apply {
            setColor(Color(0.15f,0.15f,0.15f,0.95f))
            fill()
        }
        val scrollKnob = Pixmap(1,1, Pixmap.Format.RGBA8888).apply {
            setColor(Color(0.4f,0.4f,0.4f,0.8f))
            fill()
        }
        add("default", ScrollPane.ScrollPaneStyle().apply {
            background = TextureRegionDrawable(Texture(scrollBg))
            vScrollKnob = TextureRegionDrawable(Texture(scrollKnob))
        })
    }

    private var dialog: Dialog? = null
    private var videoPlayer: VideoPlayer? = null
    private var videoImage: Image? = null

    private val features = listOf(
        Triple("Bomb", "Gây nổ và phá hủy các ô xung quanh.", "video/Bomb.mp4"),
        Triple("Wall", "Tường cản đường, có thể xóa bằng booster.", "video/Wall.mp4"),
        Triple("Ice", "Làm đóng băng một ô được spawn.", "video/Ice.mp4"),
        Triple("X2", "Gấp đôi giá trị khi ghép thành công.", "video/X2.mp4"),
        Triple("Remove Row", "Xóa một hàng bất kỳ.", "video/RemoveRow.mp4"),
        Triple("Remove Wall", "Xóa một tường bất kỳ.", "video/RemoveWall.mp4"),
        Triple("Clear Debuff", "Xóa tất cả hiệu ứng bất lợi.", "video/ClearDebuff.mp4"),
        Triple("Thunder", "Gấp đôi giá trị hàng và cột tại đểm được merge.", "video/Thunder.mp4")
    )

    fun showMainMenu() {
        dialog?.remove()
        dialog = Dialog("", skin)
        dialog?.titleLabel?.setText("INTRO")
        dialog?.titleLabel?.setAlignment(Align.center)

        val mainTable = Table()
        mainTable.defaults().width(280f).pad(8f)

        for ((name, desc, video) in features) {
            val featureTable = Table()
            featureTable.defaults().width(280f)

            featureTable.add(makeButton(name) { showVideo(video) }).height(60f).row() // nút lớn hơn
            featureTable.add(Label(desc, skin, "desc-label")).height(30f).row() // mô tả lớn hơn 1 chút

            mainTable.add(featureTable).row()

            val sepPixmap = Pixmap(1,1, Pixmap.Format.RGBA8888).apply {
                setColor(Color(0.7f,0.7f,0.7f,0.4f))
                fill()
            }
            val sep = Image(Texture(sepPixmap))
            mainTable.add(sep).width(280f).height(2f).padTop(5f).padBottom(5f).row() // dày hơn 1 chút
        }

        val scrollPane = ScrollPane(mainTable, skin).apply {
            setScrollingDisabled(false, false)
            setFadeScrollBars(false)
            setForceScroll(false,true)
            setSmoothScrolling(true)
        }

        dialog?.contentTable?.clear()
        dialog?.contentTable?.add(scrollPane)!!.width(300f).height(450f).row() // cao hơn
        dialog?.contentTable?.add(makeButton("EXIT") { dialog?.hide() })!!.height(60f).padTop(10f).row() // nút lớn

        dialog?.show(stage)
    }

    private fun makeButton(text: String, action: () -> Unit): TextButton {
        return TextButton(text, skin).apply {
            label.setAlignment(Align.center)
            addListener(object : ClickListener() {
                override fun clicked(event: InputEvent?, x: Float, y: Float) {
                    action()
                }
            })
        }
    }

    fun showVideo(videoPath: String) {
        dialog?.remove()
        dialog = Dialog("DETAIL INTRO", skin)
        dialog?.titleLabel?.setAlignment(Align.center)

        dialog?.contentTable?.apply {
            pad(15f)

            videoImage = Image().apply { setSize(400f,300f) }
            add(videoImage).width(400f).height(300f).row()

            val backBtn = TextButton("BACK", skin)
            add(backBtn).padTop(10f).row()
            backBtn.addListener { showMainMenu(); true }
        }

        videoPlayer = VideoPlayerCreator.createVideoPlayer()
        videoPlayer?.play(Gdx.files.internal(videoPath))

        dialog?.show(stage)
    }

    fun update() {
        videoPlayer?.update()
        videoPlayer?.texture?.let { tex ->
            videoImage?.drawable = TextureRegionDrawable(tex)
        }
    }

    fun dispose() {
        videoPlayer?.dispose()
    }
}
