package io.github.cogdanh2k3.utils

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.utils.Array

class SpriteSheetAnimation(
    imagePath: String,
    private val rows: Int,
    private val cols: Int,
    frameDuration: Float,
    playMode: Animation.PlayMode = Animation.PlayMode.LOOP
) {
    private val sheet: Texture = Texture(Gdx.files.internal(imagePath))
    private val animation: Animation<TextureRegion>
    private var stateTime = 0f

    init {
        val frameWidth = sheet.width / cols
        val frameHeight = sheet.height / rows
        val tmp = TextureRegion.split(sheet, frameWidth, frameHeight)

        val frames = Array<TextureRegion>(rows * cols)
        for (r in 0 until rows) {
            for (c in 0 until cols) {
                frames.add(tmp[r][c])
            }
        }
        animation = Animation(frameDuration, frames, playMode)
    }

    /** internal update (for single-instance usage) */
    fun update(delta: Float) {
        stateTime += delta
    }

    /** get frame using internal stateTime */
    fun getFrame(looping: Boolean = true): TextureRegion {
        return animation.getKeyFrame(stateTime, looping)
    }

    /** get frame at arbitrary time — useful to draw multiple instances with offsets */
    fun getFrameAt(time: Float, looping: Boolean = true): TextureRegion {
        return animation.getKeyFrame(time, looping)
    }

    fun reset() { stateTime = 0f }

    fun dispose() { sheet.dispose() }

    /** helper to expose frame original size */
    val frameWidth: Int get() = sheet.width / cols
    val frameHeight: Int get() = sheet.height / rows
    fun isAnimationFinished(stateTime: Float): Boolean {
        return animation.isAnimationFinished(stateTime)
    }
}
