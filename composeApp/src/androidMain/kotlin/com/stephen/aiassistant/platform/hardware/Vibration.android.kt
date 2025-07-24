package com.stephen.aiassistant.platform.hardware

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.VibratorManager
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.stephen.aiassistant.appContext

@RequiresApi(Build.VERSION_CODES.S)
class AndroidVibrationManager : VibrationManager {

    private val vibrator =
        appContext.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager

    override fun vibrate(duration: Long) {
        vibrator.defaultVibrator.vibrate(VibrationEffect.createOneShot(duration, 60))
    }

    override fun vibratePattern(timings: LongArray) {
        val repeat = -1
        vibrator.defaultVibrator.vibrate(VibrationEffect.createWaveform(timings, repeat))
    }


    override fun stopVibrate() {
        vibrator.defaultVibrator.cancel()
    }
}

// 提供获取 VibrationManager 实例的方法
@RequiresApi(Build.VERSION_CODES.S)
@Composable
actual fun rememberVibrationManager(): VibrationManager = remember { AndroidVibrationManager() }