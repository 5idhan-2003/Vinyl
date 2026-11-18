package com.vinyl.player.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxHeight
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.vinyl.player.MainActivity
import com.vinyl.player.R
import com.vinyl.player.theme.NothingBlack
import com.vinyl.player.theme.NothingCharcoal
import com.vinyl.player.theme.NothingRed
import com.vinyl.player.theme.NothingTextMuted
import com.vinyl.player.theme.NothingWhite

/**
 * Vinyl Player Widget - Authentic Turntable Deck Design
 * Designed to look like a physical vinyl player on your home screen.
 */
class VinylGlanceWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            GlanceTheme {
                TurntableWidgetDeck()
            }
        }
    }

    @Composable
    private fun TurntableWidgetDeck() {
        Box(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(ColorProvider(NothingCharcoal))
                .cornerRadius(24.dp)
                .padding(10.dp)
                .clickable(actionStartActivity<MainActivity>()),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = GlanceModifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 1. Physical Turntable Platter & Tonearm Unit
                Box(
                    modifier = GlanceModifier
                        .size(100.dp)
                        .cornerRadius(20.dp)
                        .background(ColorProvider(NothingBlack)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        provider = ImageProvider(R.drawable.ic_turntable_deck),
                        contentDescription = "Vinyl Turntable Deck",
                        modifier = GlanceModifier.fillMaxSize()
                    )
                }

                Spacer(modifier = GlanceModifier.width(12.dp))

                // 2. Turntable Deck Control Panel & Track Readout
                Column(
                    modifier = GlanceModifier.defaultWeight().fillMaxHeight(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Deck Header Tag with Red Indicator Dot
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = GlanceModifier
                                .size(6.dp)
                                .cornerRadius(3.dp)
                                .background(ColorProvider(NothingRed))
                        ) {}
                        Spacer(modifier = GlanceModifier.width(6.dp))
                        Text(
                            text = "VINYL PLAYER",
                            style = TextStyle(
                                color = ColorProvider(NothingTextMuted),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Spacer(modifier = GlanceModifier.width(8.dp))
                        Text(
                            text = "33⅓ RPM",
                            style = TextStyle(
                                color = ColorProvider(NothingRed),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }

                    Spacer(modifier = GlanceModifier.height(4.dp))

                    // Track Title & Artist
                    Text(
                        text = "Mexican Hero",
                        maxLines = 1,
                        style = TextStyle(
                            color = ColorProvider(NothingWhite),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = "Marzauros",
                        maxLines = 1,
                        style = TextStyle(
                            color = ColorProvider(NothingTextMuted),
                            fontSize = 11.sp
                        )
                    )

                    Spacer(modifier = GlanceModifier.height(8.dp))

                    // Mechanical Turntable Transport Controls
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        TurntableDeckButton(
                            label = "⏮",
                            onClick = actionRunCallback<PreviousActionCallback>()
                        )
                        Spacer(modifier = GlanceModifier.width(8.dp))
                        TurntableDeckButton(
                            label = "▶ / ⏸",
                            isMotorPower = true,
                            onClick = actionRunCallback<PlayPauseActionCallback>()
                        )
                        Spacer(modifier = GlanceModifier.width(8.dp))
                        TurntableDeckButton(
                            label = "⏭",
                            onClick = actionRunCallback<NextActionCallback>()
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun TurntableDeckButton(
        label: String,
        isMotorPower: Boolean = false,
        onClick: androidx.glance.action.Action
    ) {
        val bg = if (isMotorPower) ColorProvider(NothingRed) else ColorProvider(NothingBlack)
        val textClr = ColorProvider(NothingWhite)

        Box(
            modifier = GlanceModifier
                .cornerRadius(10.dp)
                .background(bg)
                .clickable(onClick)
                .padding(horizontal = 10.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                style = TextStyle(
                    color = textClr,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}

class PlayPauseActionCallback : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        // Toggle motor play/pause
    }
}

class NextActionCallback : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        // Next record
    }
}

class PreviousActionCallback : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        // Previous record
    }
}
