package io.github.cogdanh2k3.audio

enum class SoundId(val filePath: String, val isMusic: Boolean) {
    // Nhạc nền
    MUSIC("audio/music/music.wav", true),
    // Hiệu ứng
    MERGE("audio/sfx/merge.wav", false),
    SWOOSH("audio/sfx/swoosh.wav", false),
    WIN("audio/sfx/win.wav", false),
    LOSE("audio/sfx/fail.wav", false),
    ICECRACK("audio/sfx/icecrack.wav", false),
    BOMB("audio/sfx/bomb.wav", false),
    CLICK("audio/sfx/click.wav", false),
    THUNDER("audio/sfx/thunder.wav", false),
    CLEARDEBUFF("audio/sfx/clear_debuff.wav", false);
}
