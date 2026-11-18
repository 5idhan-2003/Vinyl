package com.vinyl.player.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = NothingWhite,
    onPrimary = NothingBlack,
    primaryContainer = NothingDarkGray,
    onPrimaryContainer = NothingWhite,
    secondary = NothingRed,
    onSecondary = NothingWhite,
    secondaryContainer = NothingRedGlow,
    onSecondaryContainer = NothingRed,
    background = NothingBlack,
    onBackground = NothingWhite,
    surface = NothingCharcoal,
    onSurface = NothingWhite,
    surfaceVariant = NothingDarkGray,
    onSurfaceVariant = NothingTextSecondary,
    outline = NothingBorderGray
)

@Composable
fun VinylTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = DarkColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = NothingBlack.toArgb()
            window.navigationBarColor = NothingBlack.toArgb()
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = false
                isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = VinylTypography,
        content = content
    )
}
