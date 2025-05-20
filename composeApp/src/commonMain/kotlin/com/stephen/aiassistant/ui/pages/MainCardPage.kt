package com.stephen.aiassistant.ui.pages

import aiassistant.composeapp.generated.resources.Res
import aiassistant.composeapp.generated.resources.ic_menu
import aiassistant.composeapp.generated.resources.ic_peach
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.stephen.aiassistant.data.isNetworkMeteredTipShowed
import com.stephen.aiassistant.data.navBarHeight
import com.stephen.aiassistant.ui.component.CenterText
import com.stephen.aiassistant.ui.component.rememberToastState
import com.stephen.aiassistant.ui.theme.peachIconColor
import com.stephen.aiassistant.ui.theme.titleFirstText
import com.stephen.aiassistant.ui.theme.titleSecondText
import com.stephen.aiassistant.vm.MainViewModel
import io.github.aakira.napier.Napier
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.getKoin

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.MainCardPage(
    functionMap: Map<String, DrawableResource>,
    viewModel: MainViewModel = getKoin().get(),
    animatedVisibilityScope: AnimatedVisibilityScope,
    onClickMenuButton: () -> Unit,
    onItemChoosed: (String) -> Unit = {}
) {

    val networkConnectionState = viewModel.networkConnectivityState.collectAsState()

    val toastState = rememberToastState()

    LaunchedEffect(networkConnectionState.value) {
        if (networkConnectionState.value.isExpensive && !isNetworkMeteredTipShowed) {
            isNetworkMeteredTipShowed = true
            Napier.i("当前使用的是付费网络")
            toastState.show("当前使用的是付费网络，请注意流量消耗")
        } else if (networkConnectionState.value.isConstrained) {
            Napier.i("当前网络受限制，部分服务可能不可用")
        } else if (networkConnectionState.value.isDisconnected) {
            Napier.e("当前网络已断开!")
        }
    }

    Column(
        modifier = Modifier.padding(bottom = navBarHeight),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Box(modifier = Modifier.fillMaxWidth(1f)) {
            Image(
                painter = painterResource(Res.drawable.ic_menu),
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimary),
                contentDescription = null,
                modifier = Modifier.align(Alignment.CenterStart)
                    .padding(start = 10.dp).size(30.dp)
                    .clickable {
                        onClickMenuButton()
                    }
            )
            Row(
                modifier = Modifier.align(Alignment.Center),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(Res.drawable.ic_peach),
                    colorFilter = ColorFilter.tint(peachIconColor),
                    contentDescription = null,
                    modifier = Modifier.size(28.dp)
                )
                CenterText(
                    text = "桃子助手", modifier = Modifier.padding(10.dp),
                    style = titleFirstText
                )
            }
        }

        AnimatedVisibility(
            visible = networkConnectionState.value.isDisconnected,
        ) {
            CenterText(
                text = "网络连接异常",
                modifier = Modifier.padding(bottom = 10.dp)
                    .fillMaxWidth(0.6f)
                    .clip(RoundedCornerShape(20))
                    .background(MaterialTheme.colorScheme.errorContainer)
                    .border(1.dp, MaterialTheme.colorScheme.error, shape = RoundedCornerShape(20))
                    .padding(vertical = 5.dp),
            )
        }

        LazyColumn {
            items(functionMap.keys.toList(), key = { it }) {
                FunctionCard(
                    funcInfo = Pair(it, functionMap[it]!!),
                    animatedVisibilityScope = animatedVisibilityScope,
                ) {
                    onItemChoosed(it)
                }
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.FunctionCard(
    funcInfo: Pair<String, DrawableResource>,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier.clickable {
            onClick()
        }) {
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
        CenterText(
            text = funcInfo.first,
            style = titleSecondText,
            modifier = Modifier.padding(20.dp).align(Alignment.BottomEnd).sharedElement(
                sharedContentState = rememberSharedContentState(
                    key = funcInfo.first
                ),
                animatedVisibilityScope = animatedVisibilityScope
            )
        )
    }
}