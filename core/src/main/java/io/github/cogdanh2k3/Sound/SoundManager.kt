package io.github.cogdanh2k3.audio

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.audio.Sound

object SoundManager {
    private val musics = mutableMapOf<SoundId, Music>()
    private val sounds = mutableMapOf<SoundId, Sound>()

    private var currentMusic: SoundId? = null

    var musicVolume = 0.1f
        private set
    var sfxVolume = 1f
        private set

    fun loadAll() {
        if (musics.isNotEmpty() || sounds.isNotEmpty()) return
        for (id in SoundId.values()) {
            if (id.isMusic) {
                musics[id] = Gdx.audio.newMusic(Gdx.files.internal(id.filePath))
            } else {
                sounds[id] = Gdx.audio.newSound(Gdx.files.internal(id.filePath))
            }
        }
    }

    fun playMusic(id: SoundId, looping: Boolean = true) {
        if (currentMusic == id && musics[id]?.isPlaying == true) return
        stopAllMusic()
        musics[id]?.apply {
            isLooping = looping
            volume = musicVolume
            play()
        }
        currentMusic = id
    }

    fun stopMusic(id: SoundId) {
        musics[id]?.stop()
        if (currentMusic == id) currentMusic = null
    }

    fun stopAllMusic() {
        musics.values.forEach { it.stop() }
        currentMusic = null
    }

    fun playSfx(id: SoundId) {
        sounds[id]?.play(sfxVolume)
    }

    fun updateMusicVolume(v: Float) {
        musicVolume = v.coerceIn(0f, 1f)
        currentMusic?.let { id -> musics[id]?.volume = musicVolume }
    }

    fun updateSfxVolume(v: Float) {
        sfxVolume = v.coerceIn(0f, 1f)
    }

    fun dispose() {
        musics.values.forEach { it.dispose() }
        sounds.values.forEach { it.dispose() }
        musics.clear()
        sounds.clear()
        currentMusic = null
    }
}
