package com.vinyl.player.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vinyl.player.theme.NothingRed
import com.vinyl.player.theme.NothingTextMuted
import com.vinyl.player.theme.NothingWhite
import kotlin.math.sin

/**
 * Dual-Channel Stereo Dot-Matrix LED VU Meter (Nothing OS Style)
 */
@Composable
fun DotMatrixVUMeter(
    isPlaying: Boolean,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "vu_meter_pulse")
    val wave1 by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 280, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "vu_l"
    )
    val wave2 by infiniteTransition.animateFloat(
        initialValue = 0.15f,
        targetValue = 0.88f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 350, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "vu_r"
    )

    val leftLevel = if (isPlaying) wave1 else 0f
    val rightLevel = if (isPlaying) wave2 else 0f

    Row(
        modifier = modifier.padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ChannelLadder(channel = "L", level = leftLevel)
        ChannelLadder(channel = "R", level = rightLevel)
    }
}

@Composable
private fun ChannelLadder(channel: String, level: Float) {
    val totalDots = 14
    val activeCount = (level * totalDots).toInt()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Canvas(
            modifier = Modifier
                .width(14.dp)
                .height(64.dp)
        ) {
            val dotRadius = 1.8.dp.toPx()
            val stepY = size.height / totalDots

            for (i in 0 until totalDots) {
                // Inverted so index 0 is at bottom, top is peak
                val indexFromBottom = totalDots - 1 - i
                val isLit = indexFromBottom < activeCount
                val isPeak = indexFromBottom >= totalDots - 3

                val litColor = if (isPeak) NothingRed else NothingWhite
                val unlitColor = Color(0x18FFFFFF)

                val color = if (isLit) litColor else unlitColor
                drawCircle(
                    color = color,
                    radius = dotRadius,
                    center = Offset(size.width / 2f, i * stepY + stepY / 2f)
                )
            }
        }

        Text(
            text = channel,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            fontSize = 9.sp,
            color = NothingTextMuted
        )
    }
}
