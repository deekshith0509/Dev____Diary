package com.love.devadasudiary.core

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

/**
 * Thin wrapper for short tactile feedback. Centralizing here removes
 * duplicated SDK version branching from the ViewModel and lets callers
 * silently skip vibration when the feature is disabled by the user.
 */
class Haptics(context: Context) {

    private val appContext = context.applicationContext

    private val vibrator: Vibrator? by lazy { resolveVibrator() }

    fun tick(durationMs: Long = DiaryTimings.HAPTIC_DURATION_MS) {
        val v = vibrator ?: return
        if (!v.hasVibrator()) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                v.vibrate(VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                v.vibrate(durationMs)
            }
        } catch (_: SecurityException) {
            // VIBRATE permission could be revoked at runtime on some OEMs.
        }
    }

    private fun resolveVibrator(): Vibrator? = try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            (appContext.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager)
                ?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            appContext.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    } catch (_: Exception) {
        null
    }
}
