package com.stephen.aiassistant.ui.pages

import aiassistant.composeapp.generated.resources.Res
import aiassistant.composeapp.generated.resources.arrow_left
import aiassistant.composeapp.generated.resources.ic_robot
import aiassistant.composeapp.generated.resources.ic_voice_message
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.stephen.aiassistant.data.ChatRole
import com.stephen.aiassistant.data.navBarHeight
import com.stephen.aiassistant.platform.BindActivitylauncher
import com.stephen.aiassistant.platform.rememberSpeechToTextLauncher
import com.stephen.aiassistant.platform.stopSpeaking
import com.stephen.aiassistant.platform.textToSpeech
import com.stephen.aiassistant.ui.component.CenterText
import com.stephen.aiassistant.ui.component.bounceClick
import com.stephen.aiassistant.ui.component.rememberToastState
import com.stephen.aiassistant.ui.theme.defaultText
import com.stephen.aiassistant.ui.theme.infoText
import com.stephen.aiassistant.ui.theme.titleSecondText
import com.stephen.aiassistant.vm.MainViewModel
import com.stephen.permissions.PermissionState
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.getKoin

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.EnglishSpeakingPage(
    viewModel: MainViewModel = getKoin().get(),
    funcInfo: Pair<String, DrawableResource>,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onBackPressed: () -> Unit = {}
) {
    val microPermissionState = viewModel.microphonePermissionState

    val toastState = rememberToastState()

    val listState = rememberLazyListState()

    val chatListState = viewModel.aiModelChatListStateFlow.collectAsState()

    val speechToTextLauncher = rememberSpeechToTextLauncher().apply {
        waitForResult { result ->
            Napier.i("SpeechToText result: $result")
            // 发送识别的文字到 AI
            viewModel.englishSpeakingExercise(result)
        }
    }

    BindActivitylauncher(speechToTextLauncher)

    LaunchedEffect(chatListState.value.chatList.size) {
        // 每次列表更新，都滚动到最底部
        if (chatListState.value.chatList.isNotEmpty())
            listState.animateScrollToItem(chatListState.value.chatList.size - 1)
        // 语音播报ai的反馈
        withContext(Dispatchers.IO) {
            chatListState.value.chatList.lastOrNull()?.let {
                if (it.role == ChatRole.ASSISTANT) {
                    textToSpeech(it.content)
                }
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
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
            ).fillMaxWidth(1f).heightIn(max = 220.dp).padding(10.dp).clip(RoundedCornerShape(20.dp))
        )

        Column(
            Modifier
                .animateContentSize()
                .fillMaxWidth(1f)
                .clip(RoundedCornerShape(20.dp))
                .padding(horizontal = 10.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(1f).weight(1f).padding(vertical = 10.dp),
                state = listState
            ) {
                items(chatListState.value.chatList) { chatItem ->
                    ChatItemView(
                        content = chatItem.content,
                        role = chatItem.role,
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(1f)
                    .bounceClick(
                        onDown = {
                            when (microPermissionState.value) {
                                PermissionState.Granted -> {
                                    Napier.i("Microphone permission granted!")
                                    // 开始语音转文字
                                    runCatching {
                                        speechToTextLauncher.launch()
                                    }.onFailure {
                                        toastState.show("当前设备不支持原生语音识别")
                                    }
                                }

                                PermissionState.DeniedAlways -> {
                                    Napier.i("Microphone permission DeniedAlways! Open permission settings")
                                    viewModel.openPermissionSettings()
                                }

                                else -> {
                                    Napier.i("Microphone permission Denied! Request permission again.")
                                    viewModel.checkMicrophonePermission()
                                }
                            }
                        },
                        onUp = {
                            if (microPermissionState.value == PermissionState.Granted) {
                                speechToTextLauncher.stopRecognizing()
                            }
                        })
                    .clip(RoundedCornerShape(20.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(Res.drawable.ic_voice_message),
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimary),
                    contentDescription = "voice message",
                    modifier = Modifier.size(36.dp).padding(end = 10.dp)
                )
                CenterText(
                    text = "按住开始对话",
                    style = defaultText,
                )
            }
        }
    }
}

@Composable
fun ChatItemView(
    content: String,
    role: ChatRole,
) {
    Box(modifier = Modifier.fillMaxWidth(1f)) {
        Row(
            modifier = Modifier.align(if (role == ChatRole.USER) Alignment.CenterEnd else Alignment.CenterStart)
        ) {
            when (role) {
                ChatRole.USER -> {
                    SelectionContainer(
                        modifier = Modifier.clip(RoundedCornerShape(20.dp))
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(10.dp)
                    ) {
                        CenterText(
                            text = content,
                        )
                    }
                }

                else -> {
                    Image(
                        painter = painterResource(Res.drawable.ic_robot),
                        contentDescription = "logo",
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimary),
                        modifier = Modifier.padding(end = 8.dp).size(26.dp)
                            .clip(RoundedCornerShape(50))
                    )
                    Column {
                        Row(
                            modifier = Modifier.padding(bottom = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CenterText(
                                text = "Merry AI Assistant",
                                style = infoText,
                                color = MaterialTheme.colorScheme.onSecondary,
                            )
                        }
                        CenterText(
                            text = content,
                        )
                    }
                }
            }
        }
    }
}