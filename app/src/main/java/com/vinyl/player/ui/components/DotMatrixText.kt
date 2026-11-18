package com.vinyl.player.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.vinyl.player.theme.NothingMidGray
import com.vinyl.player.theme.NothingWhite

/**
 * 5x7 Dot-Matrix Character definitions for Nothing OS NDot typography simulation.
 */
object DotMatrixFont {
    // 5 columns x 7 rows bitmask (each integer contains 7 rows of 5 bits)
    val GLYPHS: Map<Char, List<Int>> = mapOf(
        'A' to listOf(0b01110, 0b10001, 0b10001, 0b11111, 0b10001, 0b10001, 0b10001),
        'B' to listOf(0b11110, 0b10001, 0b10001, 0b11110, 0b10001, 0b10001, 0b11110),
        'C' to listOf(0b01111, 0b10000, 0b10000, 0b10000, 0b10000, 0b10000, 0b01111),
        'D' to listOf(0b11110, 0b10001, 0b10001, 0b10001, 0b10001, 0b10001, 0b11110),
        'E' to listOf(0b11111, 0b10000, 0b10000, 0b11110, 0b10000, 0b10000, 0b11111),
        'F' to listOf(0b11111, 0b10000, 0b10000, 0b11110, 0b10000, 0b10000, 0b10000),
        'G' to listOf(0b01111, 0b10000, 0b10000, 0b10111, 0b10001, 0b10001, 0b01111),
        'H' to listOf(0b10001, 0b10001, 0b10001, 0b11111, 0b10001, 0b10001, 0b10001),
        'I' to listOf(0b01110, 0b00100, 0b00100, 0b00100, 0b00100, 0b00100, 0b01110),
        'J' to listOf(0b00001, 0b00001, 0b00001, 0b00001, 0b10001, 0b10001, 0b01110),
        'K' to listOf(0b10001, 0b10010, 0b10100, 0b11000, 0b10100, 0b10010, 0b10001),
        'L' to listOf(0b10000, 0b10000, 0b10000, 0b10000, 0b10000, 0b10000, 0b11111),
        'M' to listOf(0b10001, 0b11011, 0b10101, 0b10101, 0b10001, 0b10001, 0b10001),
        'N' to listOf(0b10001, 0b11001, 0b10101, 0b10011, 0b10001, 0b10001, 0b10001),
        'O' to listOf(0b01110, 0b10001, 0b10001, 0b10001, 0b10001, 0b10001, 0b01110),
        'P' to listOf(0b11110, 0b10001, 0b10001, 0b11110, 0b10000, 0b10000, 0b10000),
        'Q' to listOf(0b01110, 0b10001, 0b10001, 0b10001, 0b10101, 0b10010, 0b01101),
        'R' to listOf(0b11110, 0b10001, 0b10001, 0b11110, 0b10100, 0b10010, 0b10001),
        'S' to listOf(0b01111, 0b10000, 0b10000, 0b01110, 0b00001, 0b00001, 0b11110),
        'T' to listOf(0b11111, 0b00100, 0b00100, 0b00100, 0b00100, 0b00100, 0b00100),
        'U' to listOf(0b10001, 0b10001, 0b10001, 0b10001, 0b10001, 0b10001, 0b01110),
        'V' to listOf(0b10001, 0b10001, 0b10001, 0b10001, 0b10001, 0b01010, 0b00100),
        'W' to listOf(0b10001, 0b10001, 0b10001, 0b10101, 0b10101, 0b11011, 0b10001),
        'X' to listOf(0b10001, 0b10001, 0b01010, 0b00100, 0b01010, 0b10001, 0b10001),
        'Y' to listOf(0b10001, 0b10001, 0b01010, 0b00100, 0b00100, 0b00100, 0b00100),
        'Z' to listOf(0b11111, 0b00001, 0b00010, 0b00100, 0b01000, 0b10000, 0b11111),
        '0' to listOf(0b01110, 0b10011, 0b10101, 0b10101, 0b11001, 0b10001, 0b01110),
        '1' to listOf(0b00100, 0b01100, 0b00100, 0b00100, 0b00100, 0b00100, 0b01110),
        '2' to listOf(0b01110, 0b10001, 0b00001, 0b00010, 0b00100, 0b01000, 0b11111),
        '3' to listOf(0b11110, 0b00001, 0b00001, 0b01110, 0b00001, 0b00001, 0b11110),
        '4' to listOf(0b00010, 0b00110, 0b01010, 0b10010, 0b11111, 0b00010, 0b00010),
        '5' to listOf(0b11111, 0b10000, 0b11110, 0b00001, 0b00001, 0b10001, 0b01110),
        '6' to listOf(0b01110, 0b10000, 0b11110, 0b10001, 0b10001, 0b10001, 0b01110),
        '7' to listOf(0b11111, 0b00001, 0b00010, 0b00100, 0b01000, 0b01000, 0b01000),
        '8' to listOf(0b01110, 0b10001, 0b10001, 0b01110, 0b10001, 0b10001, 0b01110),
        '9' to listOf(0b01110, 0b10001, 0b10001, 0b01111, 0b00001, 0b00001, 0b01110),
        ':' to listOf(0b00000, 0b00100, 0b00000, 0b00000, 0b00100, 0b00000, 0b00000),
        '-' to listOf(0b00000, 0b00000, 0b00000, 0b11111, 0b00000, 0b00000, 0b00000),
        '.' to listOf(0b00000, 0b00000, 0b00000, 0b00000, 0b00000, 0b00100, 0b00100),
        '/' to listOf(0b00001, 0b00010, 0b00010, 0b00100, 0b01000, 0b01000, 0b10000),
        '(' to listOf(0b00010, 0b00100, 0b01000, 0b01000, 0b01000, 0b00100, 0b00010),
        ')' to listOf(0b01000, 0b00100, 0b00010, 0b00010, 0b00010, 0b00100, 0b01000),
        ' ' to listOf(0, 0, 0, 0, 0, 0, 0)
    )
}

@Composable
fun DotMatrixChar(
    char: Char,
    dotSize: Dp = 2.dp,
    dotSpacing: Dp = 1.dp,
    activeColor: Color = NothingWhite,
    inactiveColor: Color = Color(0x15FFFFFF),
    showInactiveGrid: Boolean = true,
    modifier: Modifier = Modifier
) {
    val upperChar = char.uppercaseChar()
    val pattern = DotMatrixFont.GLYPHS[upperChar] ?: DotMatrixFont.GLYPHS[' ']!!

    val charWidth = (dotSize * 5) + (dotSpacing * 4)
    val charHeight = (dotSize * 7) + (dotSpacing * 6)

    Canvas(
        modifier = modifier
            .width(charWidth)
            .height(charHeight)
    ) {
        val dotRadius = dotSize.toPx() / 2f
        val stepX = dotSize.toPx() + dotSpacing.toPx()
        val stepY = dotSize.toPx() + dotSpacing.toPx()

        for (row in 0 until 7) {
            val rowBits = pattern[row]
            for (col in 0 until 5) {
                val isLit = (rowBits and (1 shl (4 - col))) != 0
                val color = if (isLit) activeColor else if (showInactiveGrid) inactiveColor else Color.Transparent

                if (color != Color.Transparent) {
                    drawCircle(
                        color = color,
                        radius = dotRadius,
                        center = Offset(
                            x = col * stepX + dotRadius,
                            y = row * stepY + dotRadius
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun DotMatrixText(
    text: String,
    dotSize: Dp = 2.dp,
    dotSpacing: Dp = 1.dp,
    charSpacing: Dp = 4.dp,
    activeColor: Color = NothingWhite,
    inactiveColor: Color = Color(0x10FFFFFF),
    showInactiveGrid: Boolean = true,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        text.forEachIndexed { index, char ->
            DotMatrixChar(
                char = char,
                dotSize = dotSize,
                dotSpacing = dotSpacing,
                activeColor = activeColor,
                inactiveColor = inactiveColor,
                showInactiveGrid = showInactiveGrid
            )
            if (index < text.length - 1) {
                Spacer(modifier = Modifier.width(charSpacing))
            }
        }
    }
}
