package com.stephen.aiassistant.ui

import aiassistant.composeapp.generated.resources.Res
import aiassistant.composeapp.generated.resources.bg_calorie_cal
import aiassistant.composeapp.generated.resources.bg_social_post_gene
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import com.stephen.aiassistant.ui.pages.CalorieCalculatePage
import com.stephen.aiassistant.ui.pages.CopyWritingGenePage
import com.stephen.aiassistant.ui.pages.MainCardPage

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun ContentView(
    navController: NavHostController = rememberNavController(),
) {

    val funcList = listOf(
        Pair(PageNavKey.COPY_WRITING_GENE, Res.drawable.bg_social_post_gene),
        Pair(PageNavKey.CALORIE_CALCULATOR, Res.drawable.bg_calorie_cal),
    )

    Surface(Modifier.fillMaxSize(1f)) {
        Column(
            Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.background)
                .padding(top = 58.dp, start = 5.dp, end = 5.dp, bottom = 28.dp)
        ) {
            SharedTransitionLayout {
                NavHost(
                    navController, startDestination = PageNavKey.MAIN_PAGE.name,
                    enterTransition = {
                        fadeIn()
                    },
                    exitTransition = {
                        fadeOut()
                    }
                ) {
                    composable(route = PageNavKey.MAIN_PAGE.name) {
                        MainCardPage(funcList, animatedVisibilityScope = this@composable) {
                            navController.navigate(it.name)
                        }
                    }
                    composable(route = PageNavKey.COPY_WRITING_GENE.name) {
                        CopyWritingGenePage(
                            funcInfo = funcList[0],
                            animatedVisibilityScope = this@composable
                        ) {
                            navController.navigate(PageNavKey.MAIN_PAGE.name)
                        }
                    }
                    composable(route = PageNavKey.CALORIE_CALCULATOR.name) {
                        CalorieCalculatePage(
                            funcInfo = funcList[1],
                            animatedVisibilityScope = this@composable
                        ) {
                            navController.navigate(PageNavKey.MAIN_PAGE.name)
                        }
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