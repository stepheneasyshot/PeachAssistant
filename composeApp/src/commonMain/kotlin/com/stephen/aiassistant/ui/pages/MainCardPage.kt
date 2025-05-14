package com.stephen.aiassistant.ui.pages

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.stephen.aiassistant.platform.permissions.BindEffect
import com.stephen.aiassistant.platform.permissions.rememberPermissionsControllerFactory
import com.stephen.aiassistant.ui.component.CenterText
import com.stephen.aiassistant.ui.theme.titleFirstText
import com.stephen.aiassistant.ui.theme.titleSecondText
import com.stephen.aiassistant.vm.MainViewModel
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.getKoin

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.MainCardPage(
    functionMap: Map<String, DrawableResource>,
    animatedVisibilityScope: AnimatedVisibilityScope,
    viewModel: MainViewModel = getKoin().get(),
    onItemChoosed: (String) -> Unit = {}
) {
    val factory = rememberPermissionsControllerFactory()
    val controller = remember(factory) {
        factory.createPermissionsController()
    }
    BindEffect(controller)

    LaunchedEffect(Unit) {
        viewModel.setPermissionController(controller)
    }

    Column {
        CenterText(
            text = "功能列表", modifier = Modifier.padding(10.dp),
            style = titleFirstText
        )
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
            contentScale = ContentScale.FillBounds,
            contentDescription = null,
            modifier = Modifier.sharedElement(
                sharedContentState = rememberSharedContentState(
                    key = funcInfo.second.toString()
                ),
                animatedVisibilityScope = animatedVisibilityScope
            ).fillMaxWidth(1f).height(220.dp).padding(10.dp).clip(RoundedCornerShape(10.dp))
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