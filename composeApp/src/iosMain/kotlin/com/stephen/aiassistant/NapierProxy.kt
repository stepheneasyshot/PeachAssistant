package com.stephen.aiassistant

import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier

/**
 * IOS初始化日志工具类
 */
fun iosInitLog() {
    Napier.base(DebugAntilog())
}