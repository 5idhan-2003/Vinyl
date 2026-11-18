package com.vinyl.player.audio

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

/**
 * Advanced Haptic Feedback Manager tuned for OnePlus O-Haptics (Bionic Vibration Motor)
 * on ColorOS / OxygenOS.
 */
class HapticFeedbackManager(context: Context) {

    private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        vibratorManager?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    /**
     * Tactile mechanical click (for speed switch, motor power, cue lever)
     */
    fun performMechanicalClick() {
        if (vibrator?.hasVibrator() != true) return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val effect = VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK)
            vibrator.vibrate(effect)
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(20L)
        }
    }

    /**
     * Heavy toggle switch clunk (for Platter Motor Start / Stop)
     */
    fun performHeavyToggle() {
        if (vibrator?.hasVibrator() != true) return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val effect = VibrationEffect.createPredefined(VibrationEffect.EFFECT_HEAVY_CLICK)
            vibrator.vibrate(effect)
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(35L)
        }
    }

    /**
     * Micro-tick for vinyl scrubbing and pitch slider notches (mimics physical needle friction)
     */
    fun performVinylGrooveTick() {
        if (vibrator?.hasVibrator() != true) return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (vibrator.areAllPrimitivesSupported(VibrationEffect.Composition.PRIMITIVE_TICK)) {
                val composition = VibrationEffect.startComposition()
                    .addPrimitive(VibrationEffect.Composition.PRIMITIVE_TICK, 0.4f)
                    .compose()
                vibrator.vibrate(composition)
                return
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_TICK))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(5L)
        }
    }

    /**
     * Soft cue needle drop haptic
     */
    fun performNeedleDrop() {
        if (vibrator?.hasVibrator() != true) return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (vibrator.areAllPrimitivesSupported(VibrationEffect.Composition.PRIMITIVE_SLOW_RISE)) {
                val composition = VibrationEffect.startComposition()
                    .addPrimitive(VibrationEffect.Composition.PRIMITIVE_CLICK, 0.6f, 50)
                    .compose()
                vibrator.vibrate(composition)
                return
            }
        }
        performMechanicalClick()
    }
}
