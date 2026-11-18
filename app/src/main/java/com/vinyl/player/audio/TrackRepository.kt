package com.vinyl.player.audio

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TrackRepository(private val context: Context) {

    suspend fun loadTracks(): List<Track> = withContext(Dispatchers.IO) {
        val tracks = mutableListOf<Track>()

        // 1. Query device MediaStore
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.ALBUM_ID
        )

        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"
        val sortOrder = "${MediaStore.Audio.Media.TITLE} ASC"

        try {
            val cursor = context.contentResolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                null,
                sortOrder
            )

            cursor?.use {
                val idCol = it.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                val titleCol = it.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
                val artistCol = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
                val albumCol = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
                val durationCol = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
                val albumIdCol = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)

                while (it.moveToNext()) {
                    val id = it.getLong(idCol)
                    val title = it.getString(titleCol) ?: "Unknown Track"
                    val artist = it.getString(artistCol) ?: "Unknown Artist"
                    val album = it.getString(albumCol) ?: "Unknown Album"
                    val duration = it.getLong(durationCol)
                    val albumId = it.getLong(albumIdCol)

                    val contentUri = ContentUris.withAppendedId(
                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        id
                    )

                    val artworkUri = ContentUris.withAppendedId(
                        Uri.parse("content://media/external/audio/albumart"),
                        albumId
                    )

                    tracks.add(
                        Track(
                            id = id,
                            title = title,
                            artist = artist,
                            album = album,
                            durationMs = duration,
                            uri = contentUri,
                            artworkUri = artworkUri
                        )
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // If no local tracks exist on emulator/device, provide bundled vintage demo records
        if (tracks.isEmpty()) {
            tracks.addAll(getDemoVinylRecords())
        }

        tracks
    }

    private fun getDemoVinylRecords(): List<Track> {
        return listOf(
            Track(
                id = 1L,
                title = "ANALOG DRIFT (SIDE A)",
                artist = "NOTHING SOUND LAB",
                album = "VINYL ARCHIVE VOL. 1",
                durationMs = 214000L,
                uri = Uri.parse("https://commondatastorage.googleapis.com/codeskulptor-demos/riceracer_sound/engine_start.ogg"),
                rpm = 33
            ),
            Track(
                id = 2L,
                title = "MIDNIGHT GROOVE (45 RPM)",
                artist = "THE MONOCHROME TRIO",
                album = "WARSAW SESSIONS",
                durationMs = 186000L,
                uri = Uri.parse("https://commondatastorage.googleapis.com/codeskulptor-demos/riceracer_sound/engine_start.ogg"),
                rpm = 45
            ),
            Track(
                id = 3L,
                title = "DOT MATRIX NOCTURNE",
                artist = "CARL PEI & ENSEMBLE",
                album = "GLYPH SYMPHONY",
                durationMs = 245000L,
                uri = Uri.parse("https://commondatastorage.googleapis.com/codeskulptor-demos/riceracer_sound/engine_start.ogg"),
                rpm = 33
            )
        )
    }
}
