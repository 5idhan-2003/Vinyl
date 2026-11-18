package com.vinyl.player.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
import com.vinyl.player.ui.components.VinylPlatter

/**
 * Vinyl Player Screen - Recreated to match the exact aesthetic of the user's reference design.
 * Features:
 * - Minimalist, pure black background
 * - White vinyl disc with bold red label
 * - Pill mood dropdown [ Groovy mood v ]
 * - Heart & Chip icons
 * - Sleek line progress bar with vertical notch
 * - Classic transport bar and bottom icon navigation
 */
@Composable
fun TurntablePlayerScreen(
    viewModel: VinylPlayerViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    var isFavorite by remember { mutableStateOf(false) }
    var isWhiteVinyl by remember { mutableStateOf(true) }
    var currentMood by remember { mutableStateOf("Groovy mood") }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(NothingBlack)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 24.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // 1. Top Bar: Asterisk Logo & Brand Name
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "✲",
                        fontSize = 18.sp,
                        color = NothingWhite,
                        modifier = Modifier.padding(end = 6.dp)
                    )
                    Text(
                        text = "astromood.fm",
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp,
                        letterSpacing = 1.sp,
                        color = NothingWhite
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Mood Pill & Top Actions (Search & More)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // [ Groovy mood  ⌵ ] Pill
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .border(1.dp, NothingWhite, RoundedCornerShape(8.dp))
                            .clickable { viewModel.toggleLibrary(true) }
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = currentMood,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Normal,
                                color = NothingWhite
                            )
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = "Dropdown",
                                tint = NothingWhite,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    // Search & 3-dots Menu
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        IconButton(onClick = { viewModel.toggleLibrary(true) }) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                tint = NothingWhite,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        IconButton(onClick = { /* Menu */ }) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "Menu",
                                tint = NothingWhite,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }
            }

            // 2. Center Vinyl Record with Share Icon
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f),
                contentAlignment = Alignment.Center
            ) {
                // Share Icon on top-right of record
                IconButton(
                    onClick = { /* Share */ },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 8.dp, end = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share",
                        tint = NothingWhite,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // The Vinyl Record (White Vinyl or Classic Black)
                VinylPlatter(
                    rotationAngle = state.vinylRotationAngle,
                    isPlaying = state.isPlaying,
                    trackTitle = state.currentTrack?.title ?: "Mexican Hero",
                    artistName = state.currentTrack?.artist ?: "Marzauros",
                    isWhiteVinyl = isWhiteVinyl,
                    onScrubDelta = { delta -> viewModel.onScrubVinyl(delta) },
                    modifier = Modifier.fillMaxSize(0.92f)
                )
            }

            // 3. Track Metadata Section (Title, Artist, Heart & Chip Icons)
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = state.currentTrack?.title ?: "Mexican Hero",
                            fontFamily = FontFamily.SansSerif,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = NothingWhite
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = state.currentTrack?.artist ?: "Marzauros",
                            fontFamily = FontFamily.SansSerif,
                            fontWeight = FontWeight.Normal,
                            fontSize = 13.sp,
                            color = NothingTextMuted
                        )
                    }

                    // Heart & Chip Icons
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        IconButton(onClick = { isFavorite = !isFavorite }) {
                            Icon(
                                imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Favorite",
                                tint = if (isFavorite) NothingRed else NothingWhite,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        // Nothing Industrial Microchip Icon
                        IconButton(onClick = { viewModel.toggleSpeed() }) {
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .border(1.5.dp, NothingWhite, RoundedCornerShape(3.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .background(NothingWhite)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Progress Bar with Vertical Notch & Timestamps
                Column(modifier = Modifier.fillMaxWidth()) {
                    Slider(
                        value = state.progress,
                        onValueChange = { newProgress ->
                            val newMs = (newProgress * state.durationMs).toLong()
                            viewModel.onScrubVinyl(newProgress * 360f)
                        },
                        colors = SliderDefaults.colors(
                            thumbColor = NothingWhite,
                            activeTrackColor = NothingWhite,
                            inactiveTrackColor = NothingMidGray
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(18.dp)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    val curMin = (state.currentPositionMs / 1000) / 60
                    val curSec = (state.currentPositionMs / 1000) % 60
                    val durMin = (state.durationMs / 1000) / 60
                    val durSec = (state.durationMs / 1000) % 60

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = String.format("%d:%02d", curMin, curSec),
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            color = NothingWhite
                        )
                        Text(
                            text = String.format("%d:%02d", durMin, durSec),
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            color = NothingWhite
                        )
                    }
                }
            }

            // 4. Transport Controls (|◀, ▶, ▶|)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { viewModel.playPrevious() },
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.SkipPrevious,
                        contentDescription = "Previous Track",
                        tint = NothingWhite,
                        modifier = Modifier.size(34.dp)
                    )
                }

                IconButton(
                    onClick = { viewModel.togglePlayPause() },
                    modifier = Modifier.size(64.dp)
                ) {
                    Icon(
                        imageVector = if (state.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = "Play/Pause",
                        tint = NothingWhite,
                        modifier = Modifier.size(48.dp)
                    )
                }

                IconButton(
                    onClick = { viewModel.playNext() },
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.SkipNext,
                        contentDescription = "Next Track",
                        tint = NothingWhite,
                        modifier = Modifier.size(34.dp)
                    )
                }
            }

            // 5. Bottom Navigation Bar: (⇄, ◎, 🎨, ♫)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Shuffle / Repeat
                IconButton(onClick = { /* Toggle repeat */ }) {
                    Icon(
                        imageVector = Icons.Default.Repeat,
                        contentDescription = "Repeat",
                        tint = NothingWhite,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Vinyl Crate / Camera Disc
                IconButton(onClick = { viewModel.toggleLibrary(true) }) {
                    Text(
                        text = "◎",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = NothingWhite
                    )
                }

                // Palette: Toggle White Vinyl / Black Vinyl
                IconButton(onClick = { isWhiteVinyl = !isWhiteVinyl }) {
                    Text(
                        text = "🎨",
                        fontSize = 18.sp,
                        color = NothingWhite
                    )
                }

                // Music Library
                IconButton(onClick = { viewModel.toggleLibrary(true) }) {
                    Text(
                        text = "♫",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = NothingWhite
                    )
                }
            }
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
