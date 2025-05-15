package com.stephen.aiassistant.ui.component

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun DashedRoundedRectangle(
    modifier: Modifier = Modifier,
    color: Color = Color.Black,
    strokeWidth: Dp = 2.dp,
    cornerRadius: Dp = 8.dp,
    dashLength: Dp = 10.dp,
    gapLength: Dp = 5.dp
) {
    Canvas(modifier = modifier) {
        // 计算虚线效果参数
        val scaledDashLength = dashLength.toPx()
        val scaledGapLength = gapLength.toPx()
        val pathEffect = PathEffect.dashPathEffect(
            intervals = floatArrayOf(scaledDashLength, scaledGapLength),
            phase = 0f
        )

        // 绘制圆角虚线框
        drawRoundRect(
            color = color,
            style = Stroke(
                width = strokeWidth.toPx(),
                pathEffect = pathEffect
            ),
            cornerRadius = CornerRadius(cornerRadius.toPx())
        )
    }
}