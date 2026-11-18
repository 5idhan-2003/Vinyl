package com.vinyl.player.audio

import android.net.Uri

data class Track(
    val id: Long,
    val title: String,
    val artist: String,
    val album: String,
    val durationMs: Long,
    val uri: Uri,
    val artworkUri: Uri? = null,
    val rpm: Int = 33 // 33 or 45
)
