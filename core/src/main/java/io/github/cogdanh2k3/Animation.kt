package io.github.cogdanh2k3
import com.badlogic.gdx.math.Interpolation
import kotlin.math.min
data class Animation(
    val value: Int,
    val startX: Float,
    val startY: Float,
    val endX: Float,
    val endY: Float,
    val duration: Float = 0.15f,
    var elapsed: Float = 0f,
    val toR: Int,
    val toC: Int,
){
    fun update(delta: Float): Boolean {
        elapsed += delta
        return elapsed >= duration
    }

    fun getCurrentPos(): Pair<Float, Float> {
        val progress = min(1f, elapsed / duration)
        val x = Interpolation.smooth.apply(startX, endX, progress)
        val y = Interpolation.smooth.apply(startY, endY, progress)
        return Pair(x, y)
    }
}
