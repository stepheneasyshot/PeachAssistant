package com.stephen.aiassistant.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

// 网络链接颜色
val networkTextColor = Color(0xff62bffc)

val scanEffectColor = Color(0xff7abdf7)

val peachIconColor = Color(0xffd7718d)

val lightCalorieCardBgColor = Color(0xffb3b4af)

val darkCalorieCardBgColor = Color(0xff2c4839)

val checkedColor = Color(0xff7ace6d)

val DarkColorScheme = darkColorScheme(
    // 使用最多的按钮色
    primary = Color(0xff484848),
    //大背景
    background = Color(0xFF010101),
    // 功能组背景
    surface = Color(0xff303030),
    // 组背景变体
    surfaceVariant = Color(0xff1d1d1d),
    // 文字颜色
    onPrimary = Color(0xffffffff),
    // 路径框，输入框
    secondary = Color(0xFF1a1a1a),
    // 特殊按钮颜色二
    tertiary = Color(0xff3d77c2),
    // 置灰文字，提示文字颜色
    onSecondary = Color(0x99ffffff),
    // 警告按钮或错误提示
    error = Color(0x99e53c3c),
    // 错误容器背景色
    errorContainer = Color(0xcce53c3c),
    // 背景之上的其余元素的颜色，目前只有分割线
    onBackground = Color(0xff323232),
    // 功能组选中的颜色
    onSurface = Color(0xff404040),
)

val LightColorScheme = lightColorScheme(
    primary = Color(0xfffefeff),
    background = Color(0xfff0f0f0),
    surface = Color(0xffffffff),
    surfaceVariant = Color(0xfff5f5f5),
    onPrimary = Color(0xff050505),
    secondary = Color(0xffe2e9f8),
    tertiary = Color(0xff62bffc),
    onSecondary = Color(0x99050505),
    error = Color(0xffe8563d),
    errorContainer = Color(0x99e8563d),
    onBackground = Color(0xffe2e5f0),
    onSurface = Color(0xffd6deec),
)