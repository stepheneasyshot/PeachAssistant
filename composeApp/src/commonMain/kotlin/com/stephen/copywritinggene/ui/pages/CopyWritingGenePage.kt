package com.stephen.copywritinggene.ui.pages

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import com.stephen.copywritinggene.data.luckySentence
import com.stephen.copywritinggene.ui.component.CenterText
import com.stephen.copywritinggene.ui.component.CommonButton
import com.stephen.copywritinggene.ui.component.WrappedEditText
import com.stephen.copywritinggene.ui.component.rememberToastState
import com.stephen.copywritinggene.ui.theme.defaultText
import com.stephen.copywritinggene.ui.theme.infoText
import com.stephen.copywritinggene.ui.theme.titleFirstText
import com.stephen.copywritinggene.vm.MainViewModel
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.compose.getKoin

@Composable
fun CopyWritingGenePage(
    viewModel: MainViewModel = getKoin().get(),
    onBackpressed: () -> Unit = {}
) {
    val toastState = rememberToastState()

    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    val copyWritingState = viewModel.copyWritingStateFlow.collectAsState()
    val copyWritingStreamState = viewModel.streamingText.collectAsState()

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
        }
    }

    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(1f).padding(bottom = 20.dp)
        ) {
            // 返回图标
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                tint = MaterialTheme.colorScheme.onPrimary,
                contentDescription = "back",
                modifier = Modifier
                    .size(36.dp)
                    .padding(end = 10.dp)
                    .clickable { onBackpressed() }
            )
            CenterText(
                text = "社交圈文案生成器",
                style = titleFirstText,
            )
        }
        Column(
            Modifier
                .animateContentSize()
                .padding(bottom = 10.dp)
                .fillMaxWidth(1f)
                .clip(RoundedCornerShape(10.dp))
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
                text = "生成文案",
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
                })
        }

        Column(
            Modifier
                .animateContentSize()
                .weight(1f)
                .fillMaxWidth(1f)
                .clip(RoundedCornerShape(10.dp))
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
                                    text = "复制",
                                    onClick = {
                                        toastState.show("文案复制成功，可粘贴至其他app")
                                        viewModel.copyText(copyWritingState.value.result)
                                    },
                                    modifier = Modifier.padding(bottom = 10.dp)
                                        .align(Alignment.CenterEnd)
                                )
                            }
                        }
                    }
                } else if (copyWritingStreamState.value.isNotEmpty()) {
                    CenterText(
                        text = copyWritingStreamState.value,
                        style = defaultText,
                        modifier = Modifier.padding(bottom = 10.dp)
                    )
                }
            }
        }
    }
}