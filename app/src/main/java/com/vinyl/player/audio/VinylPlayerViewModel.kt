package com.vinyl.player.audio

import android.app.Application
import android.content.ComponentName
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.abs

data class PlayerUiState(
    val currentTrack: Track? = null,
    val tracks: List<Track> = emptyList(),
    val isPlaying: Boolean = false,
    val progress: Float = 0f, // 0.0f - 1.0f
    val currentPositionMs: Long = 0L,
    val durationMs: Long = 0L,
    val vinylRotationAngle: Float = 0f,
    val is33Rpm: Boolean = true,
    val pitchPercent: Float = 0f,
    val isCueRaised: Boolean = false,
    val isCrackleEnabled: Boolean = true,
    val isLibraryOpen: Boolean = false
)

/**
 * ViewModel with 120Hz LTPO Fluid Frame Timing & OnePlus O-Haptics Integration.
 */
class VinylPlayerViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = TrackRepository(application)
    private val haptics = HapticFeedbackManager(application)

    private val _uiState = MutableStateFlow(PlayerUiState())
    val uiState: StateFlow<PlayerUiState> = _uiState.asStateFlow()

    private var controllerFuture: ListenableFuture<MediaController>? = null
    private var mediaController: MediaController? = null
    private var rotationJob: Job? = null
    private var accumulatedScrubAngle = 0f

    init {
        loadLibrary()
        initMediaController()
        startHighRefreshRotationLoop()
    }

    private fun loadLibrary() {
        viewModelScope.launch {
            val tracks = repository.loadTracks()
            _uiState.value = _uiState.value.copy(
                tracks = tracks,
                currentTrack = tracks.firstOrNull(),
                durationMs = tracks.firstOrNull()?.durationMs ?: 0L
            )
        }
    }

    private fun initMediaController() {
        val sessionToken = SessionToken(
            getApplication(),
            ComponentName(getApplication(), VinylPlaybackService::class.java)
        )
        controllerFuture = MediaController.Builder(getApplication(), sessionToken).buildAsync()
        controllerFuture?.addListener({
            mediaController = controllerFuture?.get()
            setupPlayerListener()
        }, MoreExecutors.directExecutor())
    }

    private fun setupPlayerListener() {
        mediaController?.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                _uiState.value = _uiState.value.copy(isPlaying = isPlaying)
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == Player.STATE_ENDED) {
                    playNext()
                }
            }
        })
    }

    /**
     * 120Hz LTPO Frame Timing Loop:
     * Samples nanoTime every 8ms (~120 FPS) for buttery smooth vinyl rotation on OnePlus 13s.
     */
    private fun startHighRefreshRotationLoop() {
        rotationJob?.cancel()
        rotationJob = viewModelScope.launch {
            var lastNanoTime = System.nanoTime()
            while (isActive) {
                delay(8) // 120Hz frame timing target
                val nowNano = System.nanoTime()
                val deltaSec = (nowNano - lastNanoTime) / 1_000_000_000.0f
                lastNanoTime = nowNano

                val state = _uiState.value
                if (state.isPlaying && !state.isCueRaised) {
                    val baseSpeed = if (state.is33Rpm) 200f else 270f
                    val pitchFactor = 1f + (state.pitchPercent / 100f)
                    val speed = baseSpeed * pitchFactor

                    val newAngle = (state.vinylRotationAngle + speed * deltaSec) % 360f

                    val newPos = (state.currentPositionMs + (deltaSec * 1000f * pitchFactor).toLong())
                        .coerceAtMost(if (state.durationMs > 0) state.durationMs else 200000L)
                    val progress = if (state.durationMs > 0) newPos.toFloat() / state.durationMs else 0f

                    _uiState.value = state.copy(
                        vinylRotationAngle = newAngle,
                        currentPositionMs = newPos,
                        progress = progress
                    )
                }
            }
        }
    }

    fun togglePlayPause() {
        val current = _uiState.value
        val shouldPlay = !current.isPlaying

        haptics.performHeavyToggle()

        val cueState = if (shouldPlay) false else current.isCueRaised

        _uiState.value = current.copy(
            isPlaying = shouldPlay,
            isCueRaised = cueState
        )

        mediaController?.let { controller ->
            if (shouldPlay) {
                current.currentTrack?.let { track ->
                    if (controller.mediaItemCount == 0) {
                        val mediaItem = MediaItem.Builder()
                            .setUri(track.uri)
                            .setMediaMetadata(
                                MediaMetadata.Builder()
                                    .setTitle(track.title)
                                    .setArtist(track.artist)
                                    .setAlbumTitle(track.album)
                                    .build()
                            )
                            .build()
                        controller.setMediaItem(mediaItem)
                        controller.prepare()
                    }
                    controller.play()
                }
            } else {
                controller.pause()
            }
        }
    }

    fun playTrack(track: Track) {
        haptics.performMechanicalClick()
        _uiState.value = _uiState.value.copy(
            currentTrack = track,
            durationMs = track.durationMs,
            currentPositionMs = 0L,
            progress = 0f,
            isPlaying = true,
            isCueRaised = false,
            isLibraryOpen = false
        )

        mediaController?.let { controller ->
            val mediaItem = MediaItem.Builder()
                .setUri(track.uri)
                .setMediaMetadata(
                    MediaMetadata.Builder()
                        .setTitle(track.title)
                        .setArtist(track.artist)
                        .setAlbumTitle(track.album)
                        .build()
                )
                .build()
            controller.setMediaItem(mediaItem)
            controller.prepare()
            controller.play()
        }
    }

    fun playNext() {
        haptics.performMechanicalClick()
        val tracks = _uiState.value.tracks
        if (tracks.isEmpty()) return
        val currentIndex = tracks.indexOfFirst { it.id == _uiState.value.currentTrack?.id }
        val nextIndex = (currentIndex + 1) % tracks.size
        playTrack(tracks[nextIndex])
    }

    fun playPrevious() {
        haptics.performMechanicalClick()
        val tracks = _uiState.value.tracks
        if (tracks.isEmpty()) return
        val currentIndex = tracks.indexOfFirst { it.id == _uiState.value.currentTrack?.id }
        val prevIndex = if (currentIndex <= 0) tracks.size - 1 else currentIndex - 1
        playTrack(tracks[prevIndex])
    }

    fun toggleSpeed() {
        haptics.performMechanicalClick()
        _uiState.value = _uiState.value.copy(is33Rpm = !_uiState.value.is33Rpm)
    }

    fun toggleCue() {
        haptics.performNeedleDrop()
        val newCue = !_uiState.value.isCueRaised
        _uiState.value = _uiState.value.copy(isCueRaised = newCue)
        if (newCue) {
            mediaController?.volume = 0f
        } else {
            mediaController?.volume = 1f
        }
    }

    fun setPitch(pitch: Float) {
        if (abs(pitch - _uiState.value.pitchPercent) >= 1.0f) {
            haptics.performVinylGrooveTick()
        }
        _uiState.value = _uiState.value.copy(pitchPercent = pitch)
    }

    fun toggleCrackle() {
        haptics.performMechanicalClick()
        _uiState.value = _uiState.value.copy(isCrackleEnabled = !_uiState.value.isCrackleEnabled)
    }

    fun onScrubVinyl(deltaDegrees: Float) {
        accumulatedScrubAngle += abs(deltaDegrees)
        if (accumulatedScrubAngle > 18f) {
            haptics.performVinylGrooveTick()
            accumulatedScrubAngle = 0f
        }

        val state = _uiState.value
        val newAngle = (state.vinylRotationAngle + deltaDegrees) % 360f

        val deltaMs = (deltaDegrees / 360f * 1800f).toLong()
        val newPosition = (state.currentPositionMs + deltaMs).coerceIn(0L, state.durationMs)
        val progress = if (state.durationMs > 0) newPosition.toFloat() / state.durationMs else 0f

        _uiState.value = state.copy(
            vinylRotationAngle = newAngle,
            currentPositionMs = newPosition,
            progress = progress
        )

        mediaController?.seekTo(newPosition)
    }

    fun toggleLibrary(open: Boolean) {
        haptics.performMechanicalClick()
        _uiState.value = _uiState.value.copy(isLibraryOpen = open)
    }

    override fun onCleared() {
        rotationJob?.cancel()
        controllerFuture?.let { MediaController.releaseFuture(it) }
        super.onCleared()
    }
}
