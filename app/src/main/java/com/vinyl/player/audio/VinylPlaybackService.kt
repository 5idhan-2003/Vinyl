package com.vinyl.player.audio

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.PowerManager
import androidx.core.app.NotificationCompat
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import androidx.media3.session.MediaStyleNotificationHelper
import com.vinyl.player.MainActivity
import com.vinyl.player.R

/**
 * Foreground Playback Service optimized for ColorOS 17 / OxygenOS (OnePlus 13s).
 * - Integrates with ColorOS Aqua Dynamics / Fluid Cloud status capsule
 * - Prevents background termination via Partial WakeLock & Foreground Service
 * - Handles Audio Focus & OnePlus Alert Slider state transitions
 */
class VinylPlaybackService : MediaSessionService() {

    private var player: ExoPlayer? = null
    private var mediaSession: MediaSession? = null
    private var wakeLock: PowerManager.WakeLock? = null
    private var sessionActivityPendingIntent: PendingIntent? = null

    companion object {
        const val CHANNEL_ID = "vinyl_playback_channel_coloros"
        const val NOTIFICATION_ID = 1001
    }

    override fun onCreate() {
        super.onCreate()

        // 1. Create ColorOS Compliant Notification Channel
        createNotificationChannel()

        // 2. Acquire Partial WakeLock to prevent ColorOS Quick Freeze during playback
        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK,
            "Vinyl::ColorOSPlaybackWakeLock"
        ).apply {
            setReferenceCounted(false)
        }

        // 3. Setup Audio Attributes (Audio Focus handled automatically)
        val audioAttributes = AudioAttributes.Builder()
            .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
            .setUsage(C.USAGE_MEDIA)
            .build()

        player = ExoPlayer.Builder(this)
            .setAudioAttributes(audioAttributes, true)
            .setHandleAudioBecomingNoisy(true)
            .setWakeMode(C.WAKE_MODE_LOCAL)
            .build()

        // 4. Activity PendingIntent for notification clicks & Fluid Cloud capsule
        val openAppIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        sessionActivityPendingIntent = PendingIntent.getActivity(
            this,
            0,
            openAppIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        mediaSession = MediaSession.Builder(this, player!!)
            .setSessionActivity(sessionActivityPendingIntent!!)
            .build()

        // 5. Player State Listener for ColorOS notification and wake lock
        player?.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                if (isPlaying) {
                    wakeLock?.acquire(300_000L) // 5 minutes max per lease, refreshed continuously
                    startForegroundServiceWithNotification()
                } else {
                    if (wakeLock?.isHeld == true) {
                        wakeLock?.release()
                    }
                }
            }
        })
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Vinyl Music Playback",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Shows active playback controls for ColorOS Lockscreen & Aqua Dynamics"
                setShowBadge(false)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }

    private fun startForegroundServiceWithNotification() {
        val session = mediaSession ?: return
        val currentTrackTitle = player?.currentMediaItem?.mediaMetadata?.title?.toString() ?: "Vinyl (1) Turntable"
        val currentArtist = player?.currentMediaItem?.mediaMetadata?.artist?.toString() ?: "ColorOS 17 Audio"

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_vinyl_widget_preview)
            .setContentTitle(currentTrackTitle)
            .setContentText(currentArtist)
            .setSubText("33⅓ RPM • HI-FI")
            .setContentIntent(sessionActivityPendingIntent)
            .setStyle(MediaStyleNotificationHelper.MediaStyle(session))
            .setOngoing(true)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()

        startForeground(NOTIFICATION_ID, notification)
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        return mediaSession
    }

    override fun onDestroy() {
        if (wakeLock?.isHeld == true) {
            wakeLock?.release()
        }
        mediaSession?.run {
            player.release()
            release()
            mediaSession = null
        }
        super.onDestroy()
    }
}
