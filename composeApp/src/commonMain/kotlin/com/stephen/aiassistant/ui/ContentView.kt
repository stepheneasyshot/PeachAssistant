package com.stephen.aiassistant.ui

import aiassistant.composeapp.generated.resources.Res
import aiassistant.composeapp.generated.resources.bg_calorie_cal
import aiassistant.composeapp.generated.resources.bg_english_chat
import aiassistant.composeapp.generated.resources.bg_social_post_gene
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.stephen.aiassistant.data.ThemeState
import com.stephen.aiassistant.data.statusBarHeight
import com.stephen.aiassistant.platform.permissions.BindEffect
import com.stephen.aiassistant.platform.permissions.rememberPermissionsControllerFactory
import com.stephen.aiassistant.ui.pages.CalorieCalculatePage
import com.stephen.aiassistant.ui.pages.CopyWritingGenePage
import com.stephen.aiassistant.ui.pages.EnglishSpeakingPage
import com.stephen.aiassistant.ui.pages.MainCardPage
import com.stephen.aiassistant.ui.pages.PersonalCenterPage
import com.stephen.aiassistant.ui.pages.SideSettingsView
import com.stephen.aiassistant.ui.theme.DarkColorScheme
import com.stephen.aiassistant.ui.theme.LightColorScheme
import com.stephen.aiassistant.vm.MainViewModel
import org.koin.compose.getKoin

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun ContentView(
    viewModel: MainViewModel = getKoin().get(),
    navController: NavHostController = rememberNavController(),
    onThemeChange: (Int) -> Unit,
) {

    val functionMap = mapOf(
        PageNavKey.COPY_WRITING_GENE to Res.drawable.bg_social_post_gene,
        PageNavKey.CALORIE_CALCULATOR to Res.drawable.bg_calorie_cal,
        PageNavKey.SPEAKING_TRAINING to Res.drawable.bg_english_chat
    )

    val openSideSettingsViewState = viewModel.settingsViewOpenState.collectAsState()

    // 初始化权限管理器
    val factory = rememberPermissionsControllerFactory()

    val controller = remember(factory) {
        factory.createPermissionsController()
    }
    BindEffect(controller)

    val themeState = viewModel.themeState.collectAsState()

    LaunchedEffect(themeState.value) {
        onThemeChange(themeState.value)
    }

    LaunchedEffect(Unit) {
        viewModel.initPermissionController(controller)
        viewModel.updateLocalThemeState()
    }

    MaterialTheme(
        colorScheme = when (themeState.value) {
            ThemeState.DARK -> DarkColorScheme
            ThemeState.LIGHT -> LightColorScheme
            else -> if (isSystemInDarkTheme()) DarkColorScheme else LightColorScheme
        }
    ) {
        Surface(Modifier.fillMaxSize(1f)) {
            Column(
                Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.background)
                    .padding(top = statusBarHeight)
            ) {
                SharedTransitionLayout {
                    NavHost(
                        navController, startDestination = PageNavKey.MAIN_PAGE,
                        enterTransition = {
                            fadeIn()
                        },
                        exitTransition = {
                            fadeOut()
                        }
                    ) {
                        composable(route = PageNavKey.MAIN_PAGE) {
                            MainCardPage(
                                functionMap, animatedVisibilityScope = this@composable,
                                onClickMenuButton = {
                                    viewModel.setSettingsViewOpenState(true)
                                }) {
                                navController.navigate(it)
                            }
                        }
                        composable(route = PageNavKey.COPY_WRITING_GENE) {
                            CopyWritingGenePage(
                                funcInfo = Pair(
                                    PageNavKey.COPY_WRITING_GENE,
                                    functionMap[PageNavKey.COPY_WRITING_GENE]!!
                                ),
                                animatedVisibilityScope = this@composable
                            ) {
                                navController.navigate(PageNavKey.MAIN_PAGE)
                            }
                        }
                        composable(route = PageNavKey.CALORIE_CALCULATOR) {
                            CalorieCalculatePage(
                                funcInfo = Pair(
                                    PageNavKey.CALORIE_CALCULATOR,
                                    functionMap[PageNavKey.CALORIE_CALCULATOR]!!
                                ),
                                animatedVisibilityScope = this@composable,
                            ) {
                                navController.navigate(PageNavKey.MAIN_PAGE)
                            }
                        }
                        composable(route = PageNavKey.SPEAKING_TRAINING) {
                            EnglishSpeakingPage(
                                funcInfo = Pair(
                                    PageNavKey.SPEAKING_TRAINING,
                                    functionMap[PageNavKey.SPEAKING_TRAINING]!!
                                ),
                                animatedVisibilityScope = this@composable
                            ) {
                                navController.navigate(PageNavKey.MAIN_PAGE)
                            }
                        }
                        composable(route = PageNavKey.PERSONAL_CENTER) {
                            PersonalCenterPage(
                                animatedVisibilityScope = this@composable,
                                onBackPressed = {
                                    navController.navigate(PageNavKey.MAIN_PAGE)
                                }
                            )
                        }
                    }
                }
            }

            AnimatedVisibility(
                visible = openSideSettingsViewState.value,
                enter = fadeIn(
                    animationSpec = tween(500)
                ),
                exit = fadeOut(
                    animationSpec = tween(500)
                ),
            ) {
                Spacer(
                    modifier = Modifier.fillMaxSize(1f)
                        .background(Color.Black.copy(alpha = 0.4f))
                )
            }
            AnimatedVisibility(
                visible = openSideSettingsViewState.value,
                enter = fadeIn() + slideInHorizontally(),
                exit = fadeOut() + slideOutHorizontally(),
            ) {
                SideSettingsView(
                    modifier = Modifier.fillMaxSize(1f),
                    onClickPersonalSettings = {
                        viewModel.setSettingsViewOpenState(false)
                        navController.navigate(PageNavKey.PERSONAL_CENTER)
                    }) {
                    viewModel.setSettingsViewOpenState(false)
                }
            }
        }
    }
}

object PageNavKey {
    // 主页
    const val MAIN_PAGE = "主页"

    // 社交文案生成器
    const val COPY_WRITING_GENE = "社交圈文案生成器"

    // 卡路里计算器
    const val CALORIE_CALCULATOR = "拍照计算食物卡路里"

    // 英语口语训练
    const val SPEAKING_TRAINING = "英语口语训练"

    // 个人中心
    const val PERSONAL_CENTER = "个人中心"
}