package com.stephen.aiassistant.ui.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.stephen.aiassistant.ui.theme.scanEffectColor

@Composable
fun BlueAreaScanEffect(
    modifier: Modifier = Modifier
) {
    // 扫描区域的位置动画
    val scanPosition = remember { Animatable(-1f) }

    LaunchedEffect(Unit) {
        scanPosition.animateTo(
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(2000)
            )
        )
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            val canvasWidth = size.width
            val scanAreaWidth = canvasWidth * 0.2f // 扫描区域宽度为画布宽度的 30%
            val scanAreaCenterX = scanPosition.value * (canvasWidth / 2) + canvasWidth / 2

            // 计算扫描区域的左右边界
            val startX = scanAreaCenterX - scanAreaWidth / 2
            val endX = scanAreaCenterX + scanAreaWidth / 2

            // 绘制横向渐变扫描区域
            drawRect(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        scanEffectColor.copy(alpha = 0f),
                        scanEffectColor.copy(alpha = 0.2f),
                        scanEffectColor.copy(alpha = 0.4f),
                        scanEffectColor.copy(alpha = 0.6f),
                        scanEffectColor.copy(alpha = 0.8f),
                        scanEffectColor.copy(alpha = 0.6f),
                        scanEffectColor.copy(alpha = 0.4f),
                        scanEffectColor.copy(alpha = 0.2f),
                        scanEffectColor.copy(alpha = 0f)
                    ),
                    startX = startX,
                    endX = endX
                )
            )
        }
    }
}

// 使用示例
@Composable
fun AreaScanEffectUsageExample() {
    Box(
        modifier = Modifier.fillMaxWidth(1f).height(400.dp),
        contentAlignment = Alignment.Center
    ) {
        BlueAreaScanEffect()
    }
}