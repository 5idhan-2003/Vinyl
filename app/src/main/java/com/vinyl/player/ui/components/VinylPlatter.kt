package com.vinyl.player.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.vinyl.player.theme.NothingBlack
import com.vinyl.player.theme.NothingDarkGray
import com.vinyl.player.theme.NothingMidGray
import com.vinyl.player.theme.NothingRed
import com.vinyl.player.theme.NothingTextMuted
import com.vinyl.player.theme.NothingWhite
import com.vinyl.player.theme.TurntablePlatterRing
import com.vinyl.player.theme.VinylGrooveDark
import com.vinyl.player.theme.VinylGrooveLight
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

/**
 * Vinyl Platter Component:
 * - Vintage grooved vinyl disc with optical sheen reflections
 * - Strobe dots on platter rim
 * - Center label sticker with Nothing OS aesthetic
 * - Touch scrub / rotational scratch support
 */
@Composable
fun VinylPlatter(
    rotationAngle: Float,
    isPlaying: Boolean,
    trackTitle: String,
    artistName: String,
    onScrubDelta: (Float) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    var lastAngle = 0f
                    detectDragGestures(
                        onDragStart = { offset ->
                            val center = Offset(size.width / 2f, size.height / 2f)
                            lastAngle = (atan2(offset.y - center.y, offset.x - center.x) * 180f / PI).toFloat()
                        },
                        onDrag = { change, _ ->
                            change.consume()
                            val center = Offset(size.width / 2f, size.height / 2f)
                            val currentAngle = (atan2(change.position.y - center.y, change.position.x - center.x) * 180f / PI).toFloat()
                            var delta = currentAngle - lastAngle
                            if (delta > 180f) delta -= 360f
                            if (delta < -180f) delta += 360f
                            lastAngle = currentAngle
                            onScrubDelta(delta)
                        }
                    )
                }
        ) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val outerRadius = min(size.width, size.height) / 2f - 4.dp.toPx()

            // 1. Platter Outer Rim & Strobe Markings (Technics SL style)
            drawPlatterRim(center, outerRadius)

            // 2. Spinning Vinyl Disc
            rotate(rotationAngle, pivot = center) {
                drawVinylDisc(center, outerRadius * 0.95f, trackTitle, artistName)
            }

            // 3. Static Optical Specular Sheen (Vinyl reflection sheen does not rotate with grooves)
            drawVinylSpecularReflection(center, outerRadius * 0.95f)

            // 4. Center Spindle
            drawCircle(
                color = Color(0xFFC0C0C0),
                radius = 6.dp.toPx(),
                center = center
            )
            drawCircle(
                color = NothingBlack,
                radius = 3.dp.toPx(),
                center = center
            )
        }
    }
}

private fun DrawScope.drawPlatterRim(center: Offset, radius: Float) {
    // Outer chassis rim
    drawCircle(
        color = TurntablePlatterRing,
        radius = radius,
        center = center
    )
    drawCircle(
        color = NothingMidGray,
        radius = radius,
        center = center,
        style = Stroke(width = 2.dp.toPx())
    )

    // Strobe dots around perimeter
    val strobeCount = 48
    val dotRadius = 1.5.dp.toPx()
    val strobeOrbit = radius - 4.dp.toPx()
    for (i in 0 until strobeCount) {
        val angleRad = (i * (360f / strobeCount)) * (PI / 180f)
        val x = center.x + strobeOrbit * cos(angleRad).toFloat()
        val y = center.y + strobeOrbit * sin(angleRad).toFloat()
        drawCircle(
            color = if (i % 2 == 0) Color(0xFF888888) else Color(0xFF555555),
            radius = dotRadius,
            center = Offset(x, y)
        )
    }
}

private fun DrawScope.drawVinylDisc(
    center: Offset,
    vinylRadius: Float,
    trackTitle: String,
    artistName: String
) {
    // Main Vinyl Base Disc
    drawCircle(
        color = VinylGrooveDark,
        radius = vinylRadius,
        center = center
    )

    // Micro-Grooves (Concentric sound channels)
    val grooveStart = vinylRadius * 0.38f
    val grooveEnd = vinylRadius * 0.96f
    val grooveCount = 42
    val grooveStep = (grooveEnd - grooveStart) / grooveCount

    for (i in 0 until grooveCount) {
        val r = grooveStart + (i * grooveStep)
        val grooveColor = if (i % 4 == 0) VinylGrooveLight else Color(0xFF101010)
        drawCircle(
            color = grooveColor,
            radius = r,
            center = center,
            style = Stroke(width = 0.8f.dp.toPx())
        )
    }

    // Run-out groove lead-in ring
    drawCircle(
        color = Color(0xFF1E1E1E),
        radius = grooveStart,
        center = center,
        style = Stroke(width = 1.5.dp.toPx())
    )

    // Center Label (Nothing OS Vintage Record Label)
    val labelRadius = vinylRadius * 0.35f

    // Outer label border
    drawCircle(
        color = NothingDarkGray,
        radius = labelRadius,
        center = center
    )

    // Red Center Accent Ring (Nothing Red)
    drawCircle(
        color = NothingRed,
        radius = labelRadius * 0.72f,
        center = center
    )

    // Inner core label
    drawCircle(
        color = NothingBlack,
        radius = labelRadius * 0.65f,
        center = center
    )

    // Center label decorative text dots
    val dotCount = 8
    val dotOrbit = labelRadius * 0.85f
    for (i in 0 until dotCount) {
        val angleRad = (i * (360f / dotCount)) * (PI / 180f)
        val x = center.x + dotOrbit * cos(angleRad).toFloat()
        val y = center.y + dotOrbit * sin(angleRad).toFloat()
        drawCircle(
            color = NothingWhite,
            radius = 1.dp.toPx(),
            center = Offset(x, y)
        )
    }
}

/**
 * Optical butterfly/bowtie specular sheen across the vinyl grooves
 */
private fun DrawScope.drawVinylSpecularReflection(center: Offset, vinylRadius: Float) {
    val innerRadius = vinylRadius * 0.36f
    val outerRadius = vinylRadius * 0.97f

    // Two opposing conic sheen reflections
    val sheenBrush1 = Brush.radialGradient(
        colors = listOf(Color(0x35FFFFFF), Color(0x00FFFFFF)),
        center = center,
        radius = outerRadius
    )

    val path1 = Path().apply {
        // Upper-right sheen wedge
        moveTo(center.x + innerRadius * cos(0.3f), center.y + innerRadius * sin(0.3f))
        lineTo(center.x + outerRadius * cos(0.7f), center.y + outerRadius * sin(0.7f))
        lineTo(center.x + outerRadius * cos(0.9f), center.y + outerRadius * sin(0.9f))
        lineTo(center.x + innerRadius * cos(0.5f), center.y + innerRadius * sin(0.5f))
        close()
    }

    val path2 = Path().apply {
        // Lower-left sheen wedge
        val pi = PI.toFloat()
        moveTo(center.x + innerRadius * cos(pi + 0.3f), center.y + innerRadius * sin(pi + 0.3f))
        lineTo(center.x + outerRadius * cos(pi + 0.7f), center.y + outerRadius * sin(pi + 0.7f))
        lineTo(center.x + outerRadius * cos(pi + 0.9f), center.y + outerRadius * sin(pi + 0.9f))
        lineTo(center.x + innerRadius * cos(pi + 0.5f), center.y + innerRadius * sin(pi + 0.5f))
        close()
    }

    drawPath(path1, brush = sheenBrush1)
    drawPath(path2, brush = sheenBrush1)
}
