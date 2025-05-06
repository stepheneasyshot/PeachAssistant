package com.stephen.copywritinggene.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.stephen.copywritinggene.ui.pages.CalorieCalculatePage
import com.stephen.copywritinggene.ui.pages.CopyWritingGenePage
import com.stephen.copywritinggene.ui.pages.MainCardPage

@Composable
fun ContentView(
    navController: NavHostController = rememberNavController(),
) {
    Surface(Modifier.fillMaxSize(1f)) {
        Column(
            Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.background)
                .padding(top = 58.dp, start = 5.dp, end = 5.dp, bottom = 28.dp)
        ) {
            NavHost(
                navController, startDestination = PageNavKey.MAIN_PAGE.name,
            ) {
                composable(route = PageNavKey.MAIN_PAGE.name) {
                    MainCardPage {
                        navController.navigate(it.name)
                    }
                }
                composable(route = PageNavKey.COPY_WRITING_GENE.name) {
                    CopyWritingGenePage {
                        navController.navigate(PageNavKey.MAIN_PAGE.name)
                    }
                }
                composable(route = PageNavKey.CALORIE_CALCULATOR.name) {
                    CalorieCalculatePage {
                        navController.navigate(PageNavKey.MAIN_PAGE.name)
                    }
                }
            }
        }
    }
}

enum class PageNavKey(val content: String) {
    // 主页
    MAIN_PAGE("主页"),

    // 社交文案生成器
    COPY_WRITING_GENE("社交圈文案生成器"),

    // 卡路里计算器
    CALORIE_CALCULATOR("拍照计算食物卡路里"),
}