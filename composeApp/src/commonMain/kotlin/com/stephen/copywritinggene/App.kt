package com.stephen.copywritinggene

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import com.stephen.copywritinggene.ui.component.CenterText
import com.stephen.copywritinggene.ui.component.CommonButton
import com.stephen.copywritinggene.ui.component.rememberToastState
import com.stephen.copywritinggene.ui.theme.DarkColorScheme
import com.stephen.copywritinggene.ui.theme.LightColorScheme
import copywritinggene.composeapp.generated.resources.Res
import copywritinggene.composeapp.generated.resources.compose_multiplatform
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    MaterialTheme(if (isSystemInDarkTheme()) DarkColorScheme else LightColorScheme) {

        val toastState = rememberToastState()

        val greeting = remember { Greeting().greet() }

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
                        toastState.show("Compose: $greeting")
                    }
                }
            }
        }
    }
}