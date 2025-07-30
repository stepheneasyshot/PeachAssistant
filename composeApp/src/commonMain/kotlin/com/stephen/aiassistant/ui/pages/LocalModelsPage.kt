package com.stephen.aiassistant.ui.pages

import aiassistant.composeapp.generated.resources.Res
import aiassistant.composeapp.generated.resources.arrow_left
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import com.stephen.aiassistant.data.navBarHeight
import com.stephen.aiassistant.platform.KeyboardHeightProvider
import com.stephen.aiassistant.ui.component.CenterText
import com.stephen.aiassistant.ui.component.CommonButton
import com.stephen.aiassistant.ui.component.WrappedEditText
import com.stephen.aiassistant.ui.theme.titleSecondText
import com.stephen.aiassistant.vm.MainViewModel
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.getKoin

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.LocalModelsPage(
    viewModel: MainViewModel = getKoin().get(),
    funcInfo: Pair<String, DrawableResource>,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onBackPressed: () -> Unit = {}
) {
    val listState = rememberLazyListState()

    val keyboardHeightProvider = remember { KeyboardHeightProvider() }
    val keyboardHeight by keyboardHeightProvider.getKeyboardHeightAsState()

    val chatEditText = remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    val chatListState = viewModel.localChatListStateFlow.collectAsState()

    LaunchedEffect(chatListState.value.chatList.size) {
        // 每次列表更新，都滚动到最底部
        if (chatListState.value.chatList.isNotEmpty())
            listState.animateScrollToItem(chatListState.value.chatList.size - 1)
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
                .fillMaxWidth(1f)
                .clip(RoundedCornerShape(20.dp))
                .padding(horizontal = 10.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LazyColumn(
                modifier = Modifier
                    .animateContentSize()
                    .fillMaxWidth(1f)
                    .weight(1f)
                    .padding(vertical = 10.dp)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) {
                        focusManager.clearFocus()
                    },
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
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxWidth(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                WrappedEditText(
                    modifier = Modifier
                        .padding(10.dp)
                        .weight(1f)
                        .focusRequester(focusRequester),
                    value = chatEditText.value,
                    onValueChange = {
                        chatEditText.value = it
                    },
                    tipText = "请输入对话内容",
                    onEnterPressed = {
                        if (chatEditText.value.isNotEmpty()) {
                            viewModel.localLLMChatRequest(chatEditText.value)
                            chatEditText.value = ""
                        }
                    })

                CommonButton(onClick = {
                    if (chatEditText.value.isNotEmpty()) {
                        viewModel.localLLMChatRequest(chatEditText.value)
                        chatEditText.value = ""
                    }
                }) {
                    CenterText(text = "发送")
                }
            }
        }
    }
}
