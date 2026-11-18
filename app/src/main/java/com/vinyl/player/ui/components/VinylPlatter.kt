package com.vinyl.player.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.vinyl.player.theme.NothingBlack
import com.vinyl.player.theme.NothingRed
import com.vinyl.player.theme.NothingWhite
import com.vinyl.player.theme.VinylGrooveDark
import com.vinyl.player.theme.VinylGrooveLight
import com.vinyl.player.theme.WhiteVinylBody
import com.vinyl.player.theme.WhiteVinylGroove
import com.vinyl.player.theme.WhiteVinylGrooveDeep
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

/**
 * Vinyl Platter Component:
 * - Minimalist White Vinyl Record with bold red center label (as in reference image)
 * - Concentric audio micro-grooves
 * - Spindle hole and vintage label typography stamp
 * - Interactive finger scratching / scrubbing
 */
@Composable
fun VinylPlatter(
    rotationAngle: Float,
    isPlaying: Boolean,
    trackTitle: String,
    artistName: String,
    isWhiteVinyl: Boolean = true,
    onScrubDelta: (Float) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .padding(16.dp),
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

            // 1. Subtle Platter Drop Shadow
            drawCircle(
                color = Color(0x40000000),
                radius = outerRadius + 2.dp.toPx(),
                center = center + Offset(0f, 6.dp.toPx())
            )

            // 2. Spinning Vinyl Disc (White Vinyl / Black Vinyl)
            rotate(rotationAngle, pivot = center) {
                drawVinylDisc(
                    center = center,
                    vinylRadius = outerRadius,
                    isWhiteVinyl = isWhiteVinyl,
                    trackTitle = trackTitle,
                    artistName = artistName
                )
            }

            // 3. Optical Specular Sheen (Subtle light reflection across grooves)
            drawVinylSpecular(center, outerRadius, isWhiteVinyl)

            // 4. Center Spindle Pin
            drawCircle(
                color = Color(0xFF1A1A1A),
                radius = 5.dp.toPx(),
                center = center
            )
            drawCircle(
                color = NothingBlack,
                radius = 2.5.dp.toPx(),
                center = center
            )
        }
    }
}

private fun DrawScope.drawVinylDisc(
    center: Offset,
    vinylRadius: Float,
    isWhiteVinyl: Boolean,
    trackTitle: String,
    artistName: String
) {
    val discBaseColor = if (isWhiteVinyl) WhiteVinylBody else VinylGrooveDark

    // Main Vinyl Disc Surface
    drawCircle(
        color = discBaseColor,
        radius = vinylRadius,
        center = center
    )

    // Outer rim edge border
    drawCircle(
        color = if (isWhiteVinyl) Color(0xFFE5E5EA) else Color(0xFF222224),
        radius = vinylRadius,
        center = center,
        style = Stroke(width = 1.dp.toPx())
    )

    // Concentric Micro-Grooves
    val grooveStart = vinylRadius * 0.40f
    val grooveEnd = vinylRadius * 0.94f
    val grooveCount = 36
    val grooveStep = (grooveEnd - grooveStart) / grooveCount

    for (i in 0 until grooveCount) {
        val r = grooveStart + (i * grooveStep)
        val grooveColor = if (isWhiteVinyl) {
            if (i % 3 == 0) WhiteVinylGrooveDeep else WhiteVinylGroove
        } else {
            if (i % 3 == 0) VinylGrooveLight else Color(0xFF141416)
        }

        drawCircle(
            color = grooveColor,
            radius = r,
            center = center,
            style = Stroke(width = if (i % 6 == 0) 1.2.dp.toPx() else 0.7.dp.toPx())
        )
    }

    // Lead-out run groove
    drawCircle(
        color = if (isWhiteVinyl) Color(0xFFDCDCE0) else Color(0xFF1A1A1D),
        radius = grooveStart,
        center = center,
        style = Stroke(width = 1.5.dp.toPx())
    )

    // CENTER RED LABEL (Signature element from reference design)
    val labelRadius = vinylRadius * 0.35f

    drawCircle(
        color = NothingRed,
        radius = labelRadius,
        center = center
    )

    // Inner center spindle circle
    drawCircle(
        color = NothingBlack,
        radius = 8.dp.toPx(),
        center = center
    )
    drawCircle(
        color = NothingWhite,
        radius = 4.dp.toPx(),
        center = center
    )
    drawCircle(
        color = NothingBlack,
        radius = 2.dp.toPx(),
        center = center
    )

    // Label detail dots / starburst simulation
    val starPoints = 8
    val starRadius = labelRadius * 0.65f
    for (i in 0 until starPoints) {
        val angle = (i * (360f / starPoints)) * (PI / 180f)
        val x = center.x + starRadius * cos(angle).toFloat()
        val y = center.y + starRadius * sin(angle).toFloat()
        drawCircle(
            color = Color(0x60FFFFFF),
            radius = 1.dp.toPx(),
            center = Offset(x, y)
        )
    }
}

private fun DrawScope.drawVinylSpecular(center: Offset, vinylRadius: Float, isWhiteVinyl: Boolean) {
    val innerRadius = vinylRadius * 0.38f
    val outerRadius = vinylRadius * 0.95f

    val sheenColor = if (isWhiteVinyl) Color(0x20000000) else Color(0x25FFFFFF)

    val sheenBrush = Brush.radialGradient(
        colors = listOf(sheenColor, Color.Transparent),
        center = center,
        radius = outerRadius
    )

    for (angle in listOf(0.7f, (PI + 0.7f).toFloat())) {
        val path = Path().apply {
            moveTo(center.x + innerRadius * cos(angle - 0.2f), center.y + innerRadius * sin(angle - 0.2f))
            lineTo(center.x + outerRadius * cos(angle - 0.45f), center.y + outerRadius * sin(angle - 0.45f))
            lineTo(center.x + outerRadius * cos(angle + 0.45f), center.y + outerRadius * sin(angle + 0.45f))
            lineTo(center.x + innerRadius * cos(angle + 0.2f), center.y + innerRadius * sin(angle + 0.2f))
            close()
        }
        drawPath(path, brush = sheenBrush)
    }
}
