package com.stephen.copywritinggene.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.stephen.copywritinggene.ui.component.CommonButton
import com.stephen.copywritinggene.ui.component.rememberToastState
import com.stephen.copywritinggene.vm.MainViewModel
import org.koin.compose.getKoin

@Composable
fun ContentView(
    viewModel: MainViewModel = getKoin().get(),
    navController: NavHostController = rememberNavController(),
) {

    val toastState = rememberToastState()

    LaunchedEffect(Unit){
        viewModel.chatWithAI("你好")
    }

    Surface(Modifier.fillMaxSize(1f)) {
        Box(
            Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.background)
                .padding(5.dp)
        ) {
            Column(
                Modifier.padding(top = 48.dp)
                    .fillMaxWidth(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CommonButton(text = "Click me!") {
                    toastState.show("Hello, Compose Multiplatform!")
                }
            }
        }
    }

}