package com.stephen.copywritinggene.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.animation.with
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
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
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.stephen.copywritinggene.data.luckySentence
import com.stephen.copywritinggene.ui.component.CenterText
import com.stephen.copywritinggene.ui.component.CommonButton
import com.stephen.copywritinggene.ui.component.WrappedEditText
import com.stephen.copywritinggene.ui.component.rememberToastState
import com.stephen.copywritinggene.ui.theme.defaultText
import com.stephen.copywritinggene.ui.theme.infoText
import com.stephen.copywritinggene.ui.theme.titleText
import com.stephen.copywritinggene.vm.MainViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.compose.getKoin

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ContentView(
    viewModel: MainViewModel = getKoin().get(),
    navController: NavHostController = rememberNavController(),
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

    Surface(Modifier.fillMaxSize(1f)) {
        Column(
            Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.background)
                .padding(top = 58.dp, start = 5.dp, end = 5.dp, bottom = 28.dp)
        ) {
            Column(
                Modifier
                    .animateContentSize()
                    .padding(bottom = 10.dp)
                    .fillMaxWidth(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CenterText(
                    text = "社交圈文案生成器",
                    style = titleText,
                    modifier = Modifier.padding(bottom = 20.dp)
                )
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
                        if (copyWritingState.value.isLoading) {
                            toastState.show("正在生成中，请稍后再试")
                        } else if (themeString.isEmpty()) {
                            toastState.show("请输入文案主题")
                        } else if (styleString.isEmpty()) {
                            toastState.show("请输入文案的风格")
                        } else {
                            focusManager.clearFocus()
                            viewModel.generateCopyWriting(themeString, styleString)
                        }
                    },
                    modifier = Modifier.padding(bottom = 10.dp)
                )
            }

            Column(
                Modifier
                    .animateContentSize()
                    .weight(1f)
                    .fillMaxWidth(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.surface)
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
}