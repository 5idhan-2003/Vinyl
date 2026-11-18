package com.vinyl.player.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vinyl.player.audio.Track
import com.vinyl.player.theme.NothingBlack
import com.vinyl.player.theme.NothingBorderGray
import com.vinyl.player.theme.NothingDarkGray
import com.vinyl.player.theme.NothingRed
import com.vinyl.player.theme.NothingTextMuted
import com.vinyl.player.theme.NothingTextSecondary
import com.vinyl.player.theme.NothingWhite
import com.vinyl.player.ui.components.DotMatrixText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackListSheet(
    tracks: List<Track>,
    currentTrack: Track?,
    onTrackSelect: (Track) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = NothingBlack,
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    DotMatrixText(
                        text = "VINYL CRATE",
                        dotSize = 2.dp,
                        dotSpacing = 1.dp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${tracks.size} RECORDS CATALOGUED",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        color = NothingTextMuted
                    )
                }

                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = NothingWhite
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Track list
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(tracks) { index, track ->
                    val isSelected = track.id == currentTrack?.id
                    TrackRowItem(
                        index = index + 1,
                        track = track,
                        isSelected = isSelected,
                        onClick = { onTrackSelect(track) }
                    )
                }
            }
        }
    }
}

@Composable
private fun TrackRowItem(
    index: Int,
    track: Track,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val bg = if (isSelected) NothingDarkGray else NothingBlack
    val borderColor = if (isSelected) NothingRed else NothingBorderGray

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(bg)
            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Track number index
        Text(
            text = String.format("%02d", index),
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            color = if (isSelected) NothingRed else NothingTextMuted,
            modifier = Modifier.width(30.dp)
        )

        // Indicator dot
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(if (isSelected) NothingRed else NothingBorderGray)
        )

        Spacer(modifier = Modifier.width(12.dp))

        // Title and Artist
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = track.title.uppercase(),
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
                color = NothingWhite,
                maxLines = 1
            )
            Text(
                text = track.artist.uppercase(),
                fontFamily = FontFamily.Monospace,
                fontSize = 10.sp,
                color = NothingTextSecondary,
                maxLines = 1
            )
        }

        // Duration in mm:ss
        val minutes = (track.durationMs / 1000) / 60
        val seconds = (track.durationMs / 1000) % 60
        Text(
            text = String.format("%02d:%02d", minutes, seconds),
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp,
            color = NothingTextMuted
        )
    }
}
