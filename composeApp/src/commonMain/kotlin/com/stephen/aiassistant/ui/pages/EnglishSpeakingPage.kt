package com.stephen.aiassistant.ui.pages

import aiassistant.composeapp.generated.resources.Res
import aiassistant.composeapp.generated.resources.arrow_left
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.stephen.aiassistant.platform.stopSpeaking
import com.stephen.aiassistant.platform.textToSpeech
import com.stephen.aiassistant.ui.component.CenterText
import com.stephen.aiassistant.ui.theme.titleSecondText
import com.stephen.aiassistant.vm.MainViewModel
import com.stephen.permissions.PermissionState
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.getKoin

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.EnglishSpeakingPage(
    viewModel: MainViewModel = getKoin().get(),
    funcInfo: Pair<String, DrawableResource>,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onBackpressed: () -> Unit = {}
) {
    val microPermissionState = viewModel.microphonePermissionState

    LaunchedEffect(Unit) {
        delay(1000L)
        textToSpeech("Hi, I'm your personal English teacher. How can I help you today?")
    }

    DisposableEffect(Unit) {
        onDispose {
            stopSpeaking()
        }
    }

    Column {
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
                    .clickable { onBackpressed() }
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
            contentScale = ContentScale.FillBounds,
            contentDescription = null,
            modifier = Modifier.sharedElement(
                sharedContentState = rememberSharedContentState(
                    key = funcInfo.second.toString()
                ),
                animatedVisibilityScope = animatedVisibilityScope
            ).fillMaxWidth(1f).height(220.dp).padding(10.dp).clip(RoundedCornerShape(10.dp))
        )

        Column(
            Modifier
                .animateContentSize()
                .padding(bottom = 10.dp)
                .fillMaxWidth(1f)
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 10.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            CenterText(
                text = "点击开始录音",
                modifier = Modifier.clickable {
                    when (microPermissionState.value) {
                        PermissionState.Granted -> {
                            viewModel.startToProcessSpeechToText()
                        }

                        PermissionState.DeniedAlways -> {
                            viewModel.openPermissionSettings()
                        }

                        else -> {
                            viewModel.provideOrRequestMicrophonePermission()
                        }
                    }
                }
            )
        }
    }
}