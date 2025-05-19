package com.stephen.aiassistant.ui.pages

import aiassistant.composeapp.generated.resources.Res
import aiassistant.composeapp.generated.resources.test_avatar
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.stephen.aiassistant.data.navBarHeight
import com.stephen.aiassistant.data.statusBarHeight
import com.stephen.aiassistant.ui.component.CenterText
import com.stephen.aiassistant.ui.theme.titleFirstText
import com.stephen.aiassistant.ui.theme.titleSecondText
import com.stephen.aiassistant.vm.MainViewModel
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.getKoin

@Composable
fun SideSettingsView(
    viewModel: MainViewModel = getKoin().get(),
    modifier: Modifier = Modifier,
    onClickPersonalSettings: () -> Unit,
    onDismissSettingsView: () -> Unit
) {
    Row(modifier = modifier) {
        Column(
            modifier = Modifier.fillMaxHeight(1f).weight(0.7f)
                .background(MaterialTheme.colorScheme.background)
                .padding(10.dp)
        ) {
            Row(
                modifier = Modifier.padding(
                    top = statusBarHeight,
                    bottom = 10.dp,
                ).fillMaxWidth(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.onSurface)
                    .clickable {
                        onClickPersonalSettings()
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(Res.drawable.test_avatar),
                    contentDescription = "avatar",
                    modifier = Modifier.padding(10.dp).size(48.dp)
                        .clip(RoundedCornerShape(50))
                )
                CenterText(text = "Stephen", style = titleFirstText)
            }

            LazyColumn(
                modifier = Modifier.padding(bottom = navBarHeight)
                    .weight(1f).fillMaxWidth(1f)
                    .pointerInput(Unit) {
                        detectDragGestures { _, dragAmount ->
                            if (dragAmount.x < 0) {
                                onDismissSettingsView()
                            }
                        }
                    }
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.onSurface)
                    .padding(10.dp)
            ) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(1f)
                            .clip(RoundedCornerShape(10.dp)),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CenterText(
                            text = "主题模式",
                            style = titleSecondText,
                            modifier = Modifier.padding(vertical = 10.dp)
                        )
                    }
                }
            }
        }

        Spacer(
            modifier = Modifier.fillMaxHeight(1f).weight(0.3f)
                .pointerInput(Unit) {
                    detectDragGestures { _, dragAmount ->
                        if (dragAmount.x < 0) {
                            onDismissSettingsView()
                        }
                    }
                }
                .clickable(
                    interactionSource = MutableInteractionSource(),
                    indication = null
                ) {
                    onDismissSettingsView()
                })
    }
}