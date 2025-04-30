package com.stephen.copywritinggene.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.stephen.copywritinggene.ui.component.CenterText
import com.stephen.copywritinggene.ui.component.CommonButton
import com.stephen.copywritinggene.ui.component.WrappedEditText
import com.stephen.copywritinggene.ui.component.rememberToastState
import com.stephen.copywritinggene.ui.theme.titleText
import com.stephen.copywritinggene.vm.MainViewModel
import org.koin.compose.getKoin

@Composable
fun ContentView(
    viewModel: MainViewModel = getKoin().get(),
    navController: NavHostController = rememberNavController(),
) {

    val toastState = rememberToastState()

    val copyWritingState = viewModel.copyWritingStateFlow.collectAsState()

    var themeString by remember { mutableStateOf("") }
    var styleString by remember { mutableStateOf("") }

    Surface(Modifier.fillMaxSize(1f)) {
        Column(
            Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.background)
                .padding(top = 48.dp, start = 5.dp, end = 5.dp, bottom = 10.dp)
        ) {
            CenterText(
                text = "社交圈文案生成器",
                style = titleText,
                modifier = Modifier.padding(bottom = 10.dp)
            )
            Column(
                Modifier
                    .padding(bottom = 10.dp)
                    .fillMaxWidth(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
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
                        modifier = Modifier.fillMaxWidth(1f)
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
                        modifier = Modifier.fillMaxWidth(1f)
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
                            viewModel.generateCopyWriting(themeString, styleString)
                        }
                    },
                    modifier = Modifier.padding(bottom = 10.dp)
                )
            }

            Column(
                Modifier
                    .weight(1f)
                    .fillMaxWidth(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (copyWritingState.value.isLoading) {
                    CircularProgressIndicator()
                } else if (copyWritingState.value.isError) {
                    CenterText(
                        text = "生成文案失败",
                        style = titleText,
                        modifier = Modifier.padding(bottom = 10.dp)
                    )
                } else if (copyWritingState.value.result.isNotEmpty()) {
                    CenterText(
                        text = copyWritingState.value.result,
                        style = titleText,
                        modifier = Modifier.padding(bottom = 10.dp)
                    )
                }
            }
        }
    }
}