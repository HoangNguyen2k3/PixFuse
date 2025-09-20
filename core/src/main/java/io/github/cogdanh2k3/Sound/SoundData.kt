package io.github.cogdanh2k3.audio

enum class SoundId(val filePath: String, val isMusic: Boolean) {
    // Nhạc nền
    MUSIC("audio/music/music.wav", true),
    // Hiệu ứng
    MERGE("audio/sfx/merge.wav", false),
    SWOOSH("audio/sfx/swoosh.wav", false),
    WIN("audio/sfx/win.wav", false),
    LOSE("audio/sfx/fail.wav", false);
}
