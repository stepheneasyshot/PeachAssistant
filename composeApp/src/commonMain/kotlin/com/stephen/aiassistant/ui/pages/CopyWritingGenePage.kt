package com.stephen.aiassistant.ui.pages

import aiassistant.composeapp.generated.resources.Res
import aiassistant.composeapp.generated.resources.arrow_left
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import com.stephen.aiassistant.data.luckySentence
import com.stephen.aiassistant.data.navBarHeight
import com.stephen.aiassistant.platform.stopSpeaking
import com.stephen.aiassistant.ui.component.CenterText
import com.stephen.aiassistant.ui.component.CommonButton
import com.stephen.aiassistant.ui.component.WrappedEditText
import com.stephen.aiassistant.ui.component.rememberToastState
import com.stephen.aiassistant.ui.theme.defaultText
import com.stephen.aiassistant.ui.theme.infoText
import com.stephen.aiassistant.ui.theme.titleSecondText
import com.stephen.aiassistant.vm.MainViewModel
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.getKoin

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.CopyWritingGenePage(
    viewModel: MainViewModel = getKoin().get(),
    funcInfo: Pair<String, DrawableResource>,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onBackPressed: () -> Unit = {}
) {
    val toastState = rememberToastState()

    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    val copyWritingState = viewModel.copyWritingStateFlow.collectAsState()

    var themeString by remember { mutableStateOf("") }
    var styleString by remember { mutableStateOf("") }

    val maxIndex = luckySentence.size - 1
    var randomSentence by remember { mutableStateOf(luckySentence[(0..maxIndex).random()]) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        scope.launch {
            while (true) {
                delay(3000L)
                randomSentence = luckySentence[(0..maxIndex).random()]
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            scope.cancel()
            focusManager.clearFocus()
            stopSpeaking()
        }
    }

    Column(modifier = Modifier.padding(bottom = navBarHeight)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(1f).padding(bottom = 20.dp)
        ) {
            // 返回图标
            Image(
                painter = painterResource(Res.drawable.arrow_left),
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimary),
                contentDescription = "back",
                modifier = Modifier
                    .padding(horizontal = 10.dp)
                    .size(28.dp)
                    .clickable { onBackPressed() }
            )
            CenterText(
                text = funcInfo.first,
                style = titleSecondText,
                modifier = Modifier.sharedElement(
                    sharedContentState = rememberSharedContentState(
                        key = funcInfo.first
                    ),
                    animatedVisibilityScope = animatedVisibilityScope
                )
            )
        }

        Image(
            painter = painterResource(funcInfo.second),
            contentScale = ContentScale.FillWidth,
            contentDescription = null,
            modifier = Modifier.sharedElement(
                sharedContentState = rememberSharedContentState(
                    key = funcInfo.second.toString()
                ),
                animatedVisibilityScope = animatedVisibilityScope
            ).fillMaxWidth(1f).heightIn(max = 140.dp).padding(10.dp).clip(RoundedCornerShape(20.dp))
        )

        Column(
            Modifier
                .animateContentSize()
                .padding(bottom = 10.dp, start = 10.dp, end = 10.dp)
                .fillMaxWidth(1f)
                .clip(RoundedCornerShape(20.dp))
                .background(MaterialTheme.colorScheme.surface)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                ) {
                    focusManager.clearFocus()
                }
                .padding(horizontal = 10.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 10.dp)
            ) {
                WrappedEditText(
                    value = themeString,
                    tipText = "请输入文案主题",
                    onValueChange = {
                        themeString = it
                    },
                    onEnterPressed = {
                        focusManager.clearFocus()
                    },
                    modifier = Modifier.fillMaxWidth(1f).focusRequester(focusRequester)
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 10.dp)
            ) {
                WrappedEditText(
                    value = styleString,
                    tipText = "请输入文案的风格",
                    onValueChange = {
                        styleString = it
                    },
                    onEnterPressed = {
                        focusManager.clearFocus()
                    },
                    modifier = Modifier.fillMaxWidth(1f).focusRequester(focusRequester)
                )
            }

            CommonButton(
                onClick = {
                    focusManager.clearFocus()
                    if (copyWritingState.value.isLoading) {
                        toastState.show("正在生成中，请稍后再试")
                    } else if (themeString.isEmpty()) {
                        toastState.show("请输入文案主题")
                    } else if (styleString.isEmpty()) {
                        toastState.show("请输入文案的风格")
                    } else {
                        viewModel.generateCopyWriting(themeString, styleString)
                    }
                }) {
                CenterText(text = "生成文案")
            }
        }

        Column(
            Modifier
                .animateContentSize()
                .padding(horizontal = 10.dp)
                .weight(1f)
                .fillMaxWidth(1f)
                .clip(RoundedCornerShape(20.dp))
                .background(MaterialTheme.colorScheme.surface)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                ) {
                    focusManager.clearFocus()
                }
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 使用 AnimatedContent 实现渐入渐出动画
            AnimatedContent(
                targetState = randomSentence,
                label = "CopyWritingResultAnimation"
            ) { resultText ->
                CenterText(
                    text = resultText,
                    style = infoText,
                    modifier = Modifier.padding(bottom = 20.dp).alpha(0.6f)
                )
            }
            if (copyWritingState.value.isLoading) {
                CircularProgressIndicator()
            } else {
                if (copyWritingState.value.isError) {
                    CenterText(
                        text = "生成失败，请检查网络，稍后重试",
                        style = defaultText,
                        modifier = Modifier.padding(bottom = 10.dp)
                    )
                } else if (copyWritingState.value.result.isNotEmpty()) {
                    LazyColumn {
                        item {
                            CenterText(
                                text = copyWritingState.value.result,
                                style = defaultText,
                                modifier = Modifier.padding(bottom = 10.dp)
                            )
                            Box(modifier = Modifier.fillMaxWidth(1f)) {
                                CommonButton(
                                    onClick = {
                                        toastState.show("文案复制成功，可粘贴至其他app")
                                        viewModel.copyText(copyWritingState.value.result)
                                    },
                                    modifier = Modifier.padding(bottom = 10.dp)
                                        .align(Alignment.CenterEnd)
                                ) {
                                    CenterText(text = "复制")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}