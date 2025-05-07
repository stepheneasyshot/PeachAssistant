package com.stephen.aiassistant.ui.pages

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.stephen.aiassistant.ui.PageNavKey
import com.stephen.aiassistant.ui.component.CenterText
import com.stephen.aiassistant.ui.theme.titleFirstText
import com.stephen.aiassistant.ui.theme.titleSecondText
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.MainCardPage(
    funcList: List<Pair<PageNavKey, DrawableResource>>,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onItemChoosed: (PageNavKey) -> Unit = {}
) {
    Column {
        CenterText(
            text = "功能列表", modifier = Modifier.padding(10.dp),
            style = titleFirstText
        )
        LazyColumn {
            items(funcList, key = { it.first.name }) {
                FunctionCard(
                    funcInfo = it,
                    animatedVisibilityScope = animatedVisibilityScope,
                ) {
                    onItemChoosed(it.first)
                }
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.FunctionCard(
    funcInfo: Pair<PageNavKey, DrawableResource>,
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
            text = funcInfo.first.content,
            style = titleSecondText,
            modifier = Modifier.padding(20.dp).align(Alignment.BottomEnd).sharedElement(
                sharedContentState = rememberSharedContentState(
                    key = funcInfo.first.content
                ),
                animatedVisibilityScope = animatedVisibilityScope
            )
        )
    }
}