package com.vinyl.player.ui.components

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vinyl.player.theme.NothingBlack
import com.vinyl.player.theme.NothingBorderGray
import com.vinyl.player.theme.NothingDarkGray
import com.vinyl.player.theme.NothingMidGray
import com.vinyl.player.theme.NothingRed
import com.vinyl.player.theme.NothingTextMuted
import com.vinyl.player.theme.NothingWhite

/**
 * Vintage Turntable Deck Controls with Nothing Industrial Aesthetic
 */
@Composable
fun TurntableDeckControls(
    is33Rpm: Boolean,
    onToggleSpeed: () -> Unit,
    isCueRaised: Boolean,
    onToggleCue: () -> Unit,
    pitchValue: Float, // -8f to +8f
    onPitchChange: (Float) -> Unit,
    isCrackleEnabled: Boolean,
    onToggleCrackle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(NothingDarkGray)
            .border(1.dp, NothingBorderGray, RoundedCornerShape(16.dp))
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Top row: Speed (33/45 RPM) & Cue Lever & Crackle filter
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Speed Switch
            TactileTogglePill(
                label = if (is33Rpm) "33⅓ RPM" else "45 RPM",
                subLabel = "SPEED",
                isActive = true,
                activeDotColor = if (is33Rpm) NothingWhite else NothingRed,
                onClick = onToggleSpeed
            )

            // Cue Lever
            TactileTogglePill(
                label = if (isCueRaised) "ARM UP" else "ARM DOWN",
                subLabel = "CUE",
                isActive = !isCueRaised,
                activeDotColor = NothingRed,
                onClick = onToggleCue
            )

            // Crackle / Vinyl Noise Filter
            TactileTogglePill(
                label = if (isCrackleEnabled) "WARMTH ON" else "CLEAN",
                subLabel = "FILTER",
                isActive = isCrackleEnabled,
                activeDotColor = NothingRed,
                onClick = onToggleCrackle
            )
        }

        // Bottom row: Quartz Pitch Slider (±8%)
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "PITCH (QUARTZ LOCK)",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = NothingTextMuted
                )
                Text(
                    text = String.format("%+.1f%%", pitchValue),
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (pitchValue == 0f) NothingWhite else NothingRed
                )
            }

            Slider(
                value = pitchValue,
                onValueChange = onPitchChange,
                valueRange = -8f..8f,
                steps = 15,
                colors = SliderDefaults.colors(
                    thumbColor = NothingWhite,
                    activeTrackColor = NothingRed,
                    inactiveTrackColor = NothingMidGray
                )
            )
        }
    }
}

@Composable
fun TactileTogglePill(
    label: String,
    subLabel: String,
    isActive: Boolean,
    activeDotColor: Color = NothingRed,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(NothingBlack)
            .border(1.dp, NothingBorderGray, RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // LED status dot
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(if (isActive) activeDotColor else Color(0x30FFFFFF))
            )
            Column {
                Text(
                    text = subLabel,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    color = NothingTextMuted
                )
                Text(
                    text = label,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = NothingWhite
                )
            }
        }
    }
}
