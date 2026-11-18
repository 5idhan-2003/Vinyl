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
import androidx.glance.action.actionParametersOf
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
 * Vintage Vinyl × Nothing OS Homescreen Widget
 */
class VinylGlanceWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            GlanceTheme {
                VinylWidgetContent()
            }
        }
    }

    @Composable
    private fun VinylWidgetContent() {
        Box(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(ColorProvider(NothingCharcoal))
                .cornerRadius(24.dp)
                .padding(14.dp)
                .clickable(actionStartActivity<MainActivity>())
        ) {
            Row(
                modifier = GlanceModifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Mini Vinyl Disc Platter Icon
                Box(
                    modifier = GlanceModifier
                        .size(76.dp)
                        .cornerRadius(38.dp)
                        .background(ColorProvider(NothingBlack)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        provider = ImageProvider(R.drawable.ic_vinyl_widget_preview),
                        contentDescription = "Vinyl Platter",
                        modifier = GlanceModifier.fillMaxSize()
                    )
                }

                Spacer(modifier = GlanceModifier.width(14.dp))

                // Track Info & Controls
                Column(
                    modifier = GlanceModifier.defaultWeight()
                ) {
                    // Header Tag
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Red indicator dot
                        Box(
                            modifier = GlanceModifier
                                .size(6.dp)
                                .cornerRadius(3.dp)
                                .background(ColorProvider(NothingRed))
                        ) {}
                        Spacer(modifier = GlanceModifier.width(6.dp))
                        Text(
                            text = "VINYL (1)",
                            style = TextStyle(
                                color = ColorProvider(NothingTextMuted),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }

                    Spacer(modifier = GlanceModifier.height(4.dp))

                    // Track Title
                    Text(
                        text = "ANALOG DRIFT",
                        maxLines = 1,
                        style = TextStyle(
                            color = ColorProvider(NothingWhite),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )

                    // Artist & RPM
                    Text(
                        text = "NOTHING SOUND LAB • 33⅓",
                        maxLines = 1,
                        style = TextStyle(
                            color = ColorProvider(NothingTextMuted),
                            fontSize = 10.sp
                        )
                    )

                    Spacer(modifier = GlanceModifier.height(8.dp))

                    // Transport Actions
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        WidgetControlButton(
                            label = "◀◀",
                            onClick = actionRunCallback<PreviousActionCallback>()
                        )
                        Spacer(modifier = GlanceModifier.width(8.dp))
                        WidgetControlButton(
                            label = "▶ / ⏸",
                            isPrimary = true,
                            onClick = actionRunCallback<PlayPauseActionCallback>()
                        )
                        Spacer(modifier = GlanceModifier.width(8.dp))
                        WidgetControlButton(
                            label = "▶▶",
                            onClick = actionRunCallback<NextActionCallback>()
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun WidgetControlButton(
        label: String,
        isPrimary: Boolean = false,
        onClick: androidx.glance.action.Action
    ) {
        val bg = if (isPrimary) ColorProvider(NothingRed) else ColorProvider(NothingBlack)
        val textClr = if (isPrimary) ColorProvider(NothingWhite) else ColorProvider(NothingWhite)

        Box(
            modifier = GlanceModifier
                .cornerRadius(12.dp)
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
        // Toggle playback via MediaSession / Broadcast
    }
}

class NextActionCallback : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        // Skip track
    }
}

class PreviousActionCallback : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        // Prev track
    }
}
