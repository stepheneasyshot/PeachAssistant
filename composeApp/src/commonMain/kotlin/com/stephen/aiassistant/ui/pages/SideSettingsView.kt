package com.stephen.aiassistant.ui.pages

import aiassistant.composeapp.generated.resources.Res
import aiassistant.composeapp.generated.resources.ic_check
import aiassistant.composeapp.generated.resources.ic_feedback
import aiassistant.composeapp.generated.resources.ic_menu_more
import aiassistant.composeapp.generated.resources.ic_theme
import aiassistant.composeapp.generated.resources.test_avatar
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.stephen.aiassistant.data.ThemeState
import com.stephen.aiassistant.data.navBarHeight
import com.stephen.aiassistant.data.statusBarHeight
import com.stephen.aiassistant.ui.component.CenterText
import com.stephen.aiassistant.ui.component.DeviderLine
import com.stephen.aiassistant.ui.theme.checkedColor
import com.stephen.aiassistant.ui.theme.infoText
import com.stephen.aiassistant.ui.theme.titleFirstText
import com.stephen.aiassistant.ui.theme.titleSecondText
import com.stephen.aiassistant.ui.theme.titleThirdText
import com.stephen.aiassistant.vm.MainViewModel
import io.github.aakira.napier.Napier
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.getKoin

@Composable
fun SideSettingsView(
    viewModel: MainViewModel = getKoin().get(),
    modifier: Modifier = Modifier,
    onClickPersonalSettings: () -> Unit,
    onDismissSettingsView: () -> Unit
) {

    val themePanelExpandState = remember { mutableStateOf(false) }

    val themeArrowRotateAnim = animateFloatAsState(
        if (themePanelExpandState.value) 90f else 0f,
        label = "themeArrowRotateAnim"
    )

    val themeState = viewModel.themeState.collectAsState()

    Row(modifier = modifier) {
        Column(
            modifier = Modifier.fillMaxHeight(1f).weight(0.75f)
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(10.dp)
        ) {
            CenterText(
                text = "设置",
                style = titleFirstText,
                modifier = Modifier.padding(
                    top = statusBarHeight,
                    bottom = 10.dp
                ).fillMaxWidth(1f)
            )

            Column(
                modifier = Modifier.padding(bottom = 10.dp).fillMaxWidth(1f)
                    .clip(RoundedCornerShape(20.dp))
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier.clickable {
                        onClickPersonalSettings()
                    }.padding(15.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(Res.drawable.test_avatar),
                        contentDescription = "avatar",
                        modifier = Modifier.padding(end = 10.dp).size(48.dp)
                            .clip(RoundedCornerShape(50))
                    )
                    CenterText(text = "Stephen", style = titleFirstText)
                }

                DeviderLine(
                    modifier = Modifier.fillMaxWidth(1f).height(1.dp)
                        .padding(horizontal = 15.dp)
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(1f).clickable {
                        Napier.i("onClick user feedback")
                    }.padding(horizontal = 15.dp, vertical = 10.dp)
                ) {
                    Image(
                        contentDescription = "user feedback",
                        painter = painterResource(Res.drawable.ic_feedback),
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimary),
                        modifier = Modifier.padding(horizontal = 10.dp).size(24.dp)
                    )
                    CenterText(
                        text = "用户反馈",
                        style = titleSecondText,
                        modifier = Modifier.padding(vertical = 10.dp)
                    )
                    Spacer(Modifier.weight(1f))
                    MoreArrow()
                }
            }

            CenterText(
                text = "通用设置",
                style = infoText,
                color = MaterialTheme.colorScheme.onSecondary,
                modifier = Modifier.padding(bottom = 10.dp)
            )
            Column(
                modifier = Modifier.padding(bottom = 10.dp)
                    .weight(1f).fillMaxWidth(1f)
                    .pointerInput(Unit) {
                        detectDragGestures { _, dragAmount ->
                            if (dragAmount.x < 0) {
                                onDismissSettingsView()
                            }
                        }
                    }
                    .clip(RoundedCornerShape(20.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(horizontal = 15.dp, vertical = 10.dp)
            ) {
                // 主题模式选择column
                Column(
                    modifier = Modifier.padding(bottom = 10.dp).animateContentSize()
                        .fillMaxWidth(1f)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable(
                            interactionSource = MutableInteractionSource(),
                            indication = null
                        ) {
                            themePanelExpandState.value = !themePanelExpandState.value
                        }
                    ) {
                        Image(
                            painter = painterResource(Res.drawable.ic_theme),
                            contentDescription = null,
                            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimary),
                            modifier = Modifier.padding(horizontal = 10.dp).size(24.dp)
                        )
                        CenterText(
                            text = "主题模式",
                            style = titleSecondText,
                            modifier = Modifier.padding(vertical = 5.dp)
                        )
                        Spacer(Modifier.weight(1f))
                        MoreArrow(modifier = Modifier.rotate(themeArrowRotateAnim.value))
                    }
                    AnimatedVisibility(
                        visible = themePanelExpandState.value,
                        enter = fadeIn(
                            animationSpec = tween(500)
                        ),
                        exit = fadeOut(
                            animationSpec = tween(200)
                        )
                    ) {
                        ThemeChooseView(
                            theme = themeState.value,
                            modifier = Modifier.fillMaxWidth(1f)
                        ) {
                            Napier.i("choose theme: $it")
                            viewModel.setLocalThemeState(it)
                        }
                    }
                }
                DeviderLine(modifier = Modifier.fillMaxWidth(1f).height(1.dp))
            }
            CenterText(
                text = "关于",
                style = infoText,
                color = MaterialTheme.colorScheme.onSecondary,
                modifier = Modifier.padding(bottom = 10.dp)
            )
            Column(
                modifier = Modifier.padding(bottom = navBarHeight)
                    .fillMaxWidth(1f)
                    .clip(RoundedCornerShape(20.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(15.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 10.dp).fillMaxWidth(1f)
                ) {
                    CenterText(
                        text = "关于桃桃",
                        style = titleSecondText,
                        modifier = Modifier.padding(start = 15.dp)
                    )
                    Spacer(Modifier.weight(1f))
                    MoreArrow()
                }
                DeviderLine(
                    modifier = Modifier.padding(bottom = 10.dp).fillMaxWidth(1f).height(1.dp)
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(1f)
                ) {
                    CenterText(
                        text = "检查更新",
                        style = titleSecondText,
                        modifier = Modifier.padding(start = 15.dp)
                    )
                    Spacer(Modifier.weight(1f))
                    CenterText(
                        text = "v1.0.0",
                        color = MaterialTheme.colorScheme.onSecondary,
                        modifier = Modifier.padding(end = 10.dp)
                    )
                    MoreArrow()
                }
            }
        }

        // 右侧遮罩重合区域，做左滑手势监听
        Spacer(
            modifier = Modifier.fillMaxHeight(1f).weight(0.25f)
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
                }
        )
    }
}

@Composable
fun ThemeChooseView(
    modifier: Modifier,
    theme: Int,
    onClickTheme: (theme: Int) -> Unit = {}
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.padding(bottom = 10.dp).fillMaxWidth(1f)
                .clickable(
                    interactionSource = MutableInteractionSource(),
                    indication = null
                ) {
                    onClickTheme(ThemeState.DARK)
                },
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                CenterText(
                    text = "深色模式",
                    style = titleThirdText,
                    modifier = Modifier.padding(end = 20.dp)
                )
            }
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                if (theme == ThemeState.DARK)
                    Image(
                        painter = painterResource(Res.drawable.ic_check),
                        colorFilter = ColorFilter.tint(checkedColor),
                        contentDescription = "checked dark theme",
                        modifier = Modifier
                            .size(26.dp)
                    )
            }
        }
        Row(
            modifier = Modifier.padding(bottom = 10.dp).fillMaxWidth(1f)
                .clickable(
                    interactionSource = MutableInteractionSource(),
                    indication = null
                ) {
                    onClickTheme(ThemeState.LIGHT)
                },
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                CenterText(
                    text = "浅色模式",
                    style = titleThirdText,
                    modifier = Modifier.padding(end = 20.dp)
                )
            }
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                if (theme == ThemeState.LIGHT)
                    Image(
                        painter = painterResource(Res.drawable.ic_check),
                        colorFilter = ColorFilter.tint(checkedColor),
                        contentDescription = "checked light theme",
                        modifier = Modifier
                            .size(26.dp)
                    )
            }
        }
        Row(
            modifier = Modifier.padding(bottom = 10.dp).fillMaxWidth(1f)
                .clickable(
                    interactionSource = MutableInteractionSource(),
                    indication = null
                ) {
                    onClickTheme(ThemeState.SYSTEM)
                },
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                CenterText(
                    text = "跟随系统",
                    style = titleThirdText,
                    modifier = Modifier.padding(end = 20.dp)
                )
            }
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                if (theme == ThemeState.SYSTEM)
                    Image(
                        painter = painterResource(Res.drawable.ic_check),
                        colorFilter = ColorFilter.tint(checkedColor),
                        contentDescription = "checked system theme",
                        modifier = Modifier
                            .size(26.dp)
                    )
            }
        }
    }
}

@Composable
fun MoreArrow(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(Res.drawable.ic_menu_more),
        contentDescription = null,
        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSecondary),
        modifier = modifier.padding(10.dp).size(32.dp)
    )
}