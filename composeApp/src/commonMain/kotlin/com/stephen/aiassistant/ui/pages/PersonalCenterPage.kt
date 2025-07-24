package com.stephen.aiassistant.ui.pages

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.stephen.aiassistant.ui.component.CenterText
import com.stephen.aiassistant.ui.component.rememberToastState
import com.stephen.aiassistant.vm.MainViewModel
import org.koin.compose.getKoin

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.PersonalCenterPage(
    viewModel: MainViewModel = getKoin().get(),
    animatedVisibilityScope: AnimatedVisibilityScope,
    onBackPressed: () -> Unit = {}
) {
    val toastState = rememberToastState()

    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier.fillMaxSize(1f),
        contentAlignment = Alignment.Center
    ) {
        CenterText(text = "个人中心占位")
    }
}