package com.stephen.aiassistant.platform.hardware

import androidx.compose.runtime.Composable

interface VibrationManager {
    fun vibrate(duration: Long = 20L)
    /**
     * make vibrate for [timings]
     * - 3000 = 3Sec
     * @param[timings] off/on Timing
     *
     * - if \[300,500,700,500] > 0.3 delay > 0.5 vibrate > 0.7 delay . 0.5 vibrate
     */
    fun vibratePattern(timings: LongArray)
    fun stopVibrate()
}

// 提供获取 VibrationManager 实例的方法
@Composable
expect fun rememberVibrationManager(): VibrationManager