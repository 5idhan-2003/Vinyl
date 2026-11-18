package com.vinyl.player.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.dp
import com.vinyl.player.theme.NothingBlack
import com.vinyl.player.theme.NothingDarkGray
import com.vinyl.player.theme.NothingMidGray
import com.vinyl.player.theme.NothingRed
import com.vinyl.player.theme.NothingWhite
import com.vinyl.player.theme.TonearmDark
import com.vinyl.player.theme.TonearmSilver

/**
 * Mechanical Tonearm Component:
 * - Pivot gimbal, counterweight
 * - Tonearm tracking based on playback progress
 * - Cue drop / lift animation
 * - Headshell with Nothing Red cartridge
 */
@Composable
fun Tonearm(
    isPlaying: Boolean,
    progress: Float, // 0.0f (outer groove) to 1.0f (inner groove)
    isCueRaised: Boolean,
    modifier: Modifier = Modifier
) {
    // When cue is raised or not playing, arm is parked at rest position (-8 degrees)
    // When playing, arm tracks from +7 degrees (lead-in) to +28 degrees (inner groove)
    val targetAngle = when {
        isCueRaised -> -10f
        !isPlaying -> -6f
        else -> 7f + (progress.coerceIn(0f, 1f) * 21f)
    }

    val animatedAngle by animateFloatAsState(
        targetValue = targetAngle,
        animationSpec = tween(durationMillis = 600),
        label = "tonearm_rotation"
    )

    // Needle drop elevation animation (shadow offset)
    val elevationOffset by animateFloatAsState(
        targetValue = if (isCueRaised || !isPlaying) 12f else 3f,
        animationSpec = tween(durationMillis = 400),
        label = "tonearm_elevation"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        // Pivot origin placed at top-right quadrant
        val pivot = Offset(size.width * 0.82f, size.height * 0.16f)

        // Drop shadow for 3D depth
        rotate(animatedAngle, pivot = pivot) {
            drawTonearmAssembly(
                pivot = pivot,
                elevation = elevationOffset,
                isShadow = true
            )
            drawTonearmAssembly(
                pivot = pivot,
                elevation = 0f,
                isShadow = false
            )
        }

        // Static Arm Rest Post & Pivot Base Gimbal
        drawArmBase(pivot)
    }
}

private fun DrawScope.drawArmBase(pivot: Offset) {
    // Gimbal outer ring
    drawCircle(
        color = NothingDarkGray,
        radius = 18.dp.toPx(),
        center = pivot
    )
    drawCircle(
        color = NothingMidGray,
        radius = 18.dp.toPx(),
        center = pivot,
        style = Stroke(width = 1.5.dp.toPx())
    )

    // Inner bearing
    drawCircle(
        color = Color(0xFF222222),
        radius = 11.dp.toPx(),
        center = pivot
    )
    drawCircle(
        color = TonearmSilver,
        radius = 4.dp.toPx(),
        center = pivot
    )
}

private fun DrawScope.drawTonearmAssembly(
    pivot: Offset,
    elevation: Float,
    isShadow: Boolean
) {
    val offset = Offset(elevation, elevation)
    val effectivePivot = pivot + offset

    val armColor = if (isShadow) Color(0x35000000) else TonearmSilver
    val detailColor = if (isShadow) Color.Transparent else TonearmDark
    val cartridgeColor = if (isShadow) Color.Transparent else NothingRed

    val armLength = size.height * 0.58f

    // 1. Counterweight behind pivot
    val counterweightStart = Offset(effectivePivot.x, effectivePivot.y - 32.dp.toPx())
    val counterweightEnd = Offset(effectivePivot.x, effectivePivot.y - 12.dp.toPx())
    drawLine(
        color = if (isShadow) Color(0x35000000) else Color(0xFF555555),
        start = counterweightStart,
        end = counterweightEnd,
        strokeWidth = 14.dp.toPx(),
        cap = StrokeCap.Round
    )

    // 2. Tonearm Tube (Classic subtle S-curve / offset)
    val armPath = Path().apply {
        moveTo(effectivePivot.x, effectivePivot.y)
        // Gentle curve towards cartridge
        cubicTo(
            effectivePivot.x - 12.dp.toPx(), effectivePivot.y + armLength * 0.35f,
            effectivePivot.x + 8.dp.toPx(), effectivePivot.y + armLength * 0.70f,
            effectivePivot.x - 16.dp.toPx(), effectivePivot.y + armLength
        )
    }

    drawPath(
        path = armPath,
        color = armColor,
        style = Stroke(
            width = if (isShadow) 7.dp.toPx() else 4.dp.toPx(),
            cap = StrokeCap.Round
        )
    )

    // 3. Headshell & Cartridge
    val headshellOrigin = Offset(effectivePivot.x - 16.dp.toPx(), effectivePivot.y + armLength)
    val headshellPath = Path().apply {
        moveTo(headshellOrigin.x, headshellOrigin.y)
        lineTo(headshellOrigin.x - 10.dp.toPx(), headshellOrigin.y + 24.dp.toPx())
        lineTo(headshellOrigin.x + 2.dp.toPx(), headshellOrigin.y + 26.dp.toPx())
        lineTo(headshellOrigin.x + 4.dp.toPx(), headshellOrigin.y + 6.dp.toPx())
        close()
    }

    drawPath(
        path = headshellPath,
        color = if (isShadow) Color(0x35000000) else NothingDarkGray
    )

    // Cartridge body (Nothing Red)
    if (!isShadow) {
        val cartridgeCenter = Offset(headshellOrigin.x - 4.dp.toPx(), headshellOrigin.y + 20.dp.toPx())
        drawCircle(
            color = cartridgeColor,
            radius = 3.5.dp.toPx(),
            center = cartridgeCenter
        )

        // Stylus needle tip
        drawCircle(
            color = NothingWhite,
            radius = 1.2.dp.toPx(),
            center = Offset(headshellOrigin.x - 5.dp.toPx(), headshellOrigin.y + 25.dp.toPx())
        )
    }
}
