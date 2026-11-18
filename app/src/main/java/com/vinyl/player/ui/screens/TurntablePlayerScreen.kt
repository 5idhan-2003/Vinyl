package com.vinyl.player.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vinyl.player.audio.VinylPlayerViewModel
import com.vinyl.player.theme.NothingBlack
import com.vinyl.player.theme.NothingBorderGray
import com.vinyl.player.theme.NothingDarkGray
import com.vinyl.player.theme.NothingRed
import com.vinyl.player.theme.NothingTextMuted
import com.vinyl.player.theme.NothingTextSecondary
import com.vinyl.player.theme.NothingWhite
import com.vinyl.player.ui.components.DotMatrixText
import com.vinyl.player.ui.components.DotMatrixVUMeter
import com.vinyl.player.ui.components.Tonearm
import com.vinyl.player.ui.components.TurntableDeckControls
import com.vinyl.player.ui.components.VinylPlatter

@Composable
fun TurntablePlayerScreen(
    viewModel: VinylPlayerViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(NothingBlack)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 1. Technical Top Bar (Nothing Phone Header)
            TopTechnicalHeader(
                onOpenLibrary = { viewModel.toggleLibrary(true) }
            )

            // 2. Turntable Deck (Vinyl Platter + Mechanical Tonearm)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(NothingDarkGray)
                    .border(1.dp, NothingBorderGray, RoundedCornerShape(24.dp))
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                // Spinning Vinyl Platter
                VinylPlatter(
                    rotationAngle = state.vinylRotationAngle,
                    isPlaying = state.isPlaying,
                    trackTitle = state.currentTrack?.title ?: "NO DISC",
                    artistName = state.currentTrack?.artist ?: "READY",
                    onScrubDelta = { delta -> viewModel.onScrubVinyl(delta) },
                    modifier = Modifier.fillMaxWidth(0.92f)
                )

                // Overlaid Mechanical Tonearm
                Tonearm(
                    isPlaying = state.isPlaying,
                    progress = state.progress,
                    isCueRaised = state.isCueRaised,
                    modifier = Modifier.matchParentSize()
                )
            }

            // 3. Track Metadata Deck & VU Meter
            TrackMetadataSection(
                title = state.currentTrack?.title ?: "NO RECORD LOADED",
                artist = state.currentTrack?.artist ?: "SELECT TRACK FROM CRATE",
                currentPosMs = state.currentPositionMs,
                durationMs = state.durationMs,
                isPlaying = state.isPlaying
            )

            // 4. Vintage Turntable Deck Controls (33/45 RPM, Cue, Crackle, Pitch)
            TurntableDeckControls(
                is33Rpm = state.is33Rpm,
                onToggleSpeed = { viewModel.toggleSpeed() },
                isCueRaised = state.isCueRaised,
                onToggleCue = { viewModel.toggleCue() },
                pitchValue = state.pitchPercent,
                onPitchChange = { viewModel.setPitch(it) },
                isCrackleEnabled = state.isCrackleEnabled,
                onToggleCrackle = { viewModel.toggleCrackle() }
            )

            // 5. Mechanical Transport Controls (Previous, Motor Start/Stop, Next)
            TransportControlBar(
                isPlaying = state.isPlaying,
                onPlayPause = { viewModel.togglePlayPause() },
                onPrevious = { viewModel.playPrevious() },
                onNext = { viewModel.playNext() }
            )

            Spacer(modifier = Modifier.height(10.dp))
        }

        // Library Drawer / Crate Modal
        if (state.isLibraryOpen) {
            TrackListSheet(
                tracks = state.tracks,
                currentTrack = state.currentTrack,
                onTrackSelect = { viewModel.playTrack(it) },
                onDismiss = { viewModel.toggleLibrary(false) }
            )
        }
    }
}

@Composable
private fun TopTechnicalHeader(onOpenLibrary: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            DotMatrixText(
                text = "VINYL (1)",
                dotSize = 2.dp,
                dotSpacing = 1.dp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "HI-FI / 33.3 RPM DIRECT DRIVE",
                fontFamily = FontFamily.Monospace,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = NothingTextMuted
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Live Red LED indicator
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(NothingRed)
            )

            // Library / Crate button
            IconButton(
                onClick = onOpenLibrary,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(NothingDarkGray)
                    .border(1.dp, NothingBorderGray, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.LibraryMusic,
                    contentDescription = "Vinyl Crate",
                    tint = NothingWhite,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun TrackMetadataSection(
    title: String,
    artist: String,
    currentPosMs: Long,
    durationMs: Long,
    isPlaying: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(NothingDarkGray)
            .border(1.dp, NothingBorderGray, RoundedCornerShape(16.dp))
            .padding(14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title.uppercase(),
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = NothingWhite,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = artist.uppercase(),
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                color = NothingTextSecondary,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(6.dp))

            // Time readout in monospace
            val curMin = (currentPosMs / 1000) / 60
            val curSec = (currentPosMs / 1000) % 60
            val durMin = (durationMs / 1000) / 60
            val durSec = (durationMs / 1000) % 60
            Text(
                text = String.format("%02d:%02d / %02d:%02d", curMin, curSec, durMin, durSec),
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp,
                color = NothingRed
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Stereo Dot-Matrix VU Meter
        DotMatrixVUMeter(isPlaying = isPlaying)
    }
}

@Composable
private fun TransportControlBar(
    isPlaying: Boolean,
    onPlayPause: () -> Unit,
    onPrevious: () -> Unit,
    onNext: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Previous Track
        IconButton(
            onClick = onPrevious,
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(NothingDarkGray)
                .border(1.dp, NothingBorderGray, CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.SkipPrevious,
                contentDescription = "Previous Track",
                tint = NothingWhite
            )
        }

        // Center Motor Start / Stop (Platter Engage)
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(if (isPlaying) NothingRed else NothingWhite)
                .clickable(onClick = onPlayPause),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                contentDescription = if (isPlaying) "Stop Motor" else "Start Motor",
                tint = if (isPlaying) NothingWhite else NothingBlack,
                modifier = Modifier.size(36.dp)
            )
        }

        // Next Track
        IconButton(
            onClick = onNext,
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(NothingDarkGray)
                .border(1.dp, NothingBorderGray, CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.SkipNext,
                contentDescription = "Next Track",
                tint = NothingWhite
            )
        }
    }
}
