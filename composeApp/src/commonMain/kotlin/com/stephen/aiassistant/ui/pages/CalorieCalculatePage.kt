package com.stephen.aiassistant.ui.pages

import aiassistant.composeapp.generated.resources.Res
import aiassistant.composeapp.generated.resources.arrow_left
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.stephen.aiassistant.ui.component.CenterText
import com.stephen.aiassistant.ui.theme.darkCalorieColor
import com.stephen.aiassistant.ui.theme.lightCalorieColor
import com.stephen.aiassistant.ui.theme.titleSecondText
import com.stephen.aiassistant.vm.MainViewModel
import com.stephen.permissions.PermissionState
import io.github.aakira.napier.Napier
import io.github.alexzhirkevich.compottie.Compottie
import io.github.alexzhirkevich.compottie.LottieCompositionSpec
import io.github.alexzhirkevich.compottie.animateLottieCompositionAsState
import io.github.alexzhirkevich.compottie.rememberLottieComposition
import io.github.alexzhirkevich.compottie.rememberLottiePainter
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.path
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.getKoin

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.CalorieCalculatePage(
    viewModel: MainViewModel = getKoin().get(),
    funcInfo: Pair<String, DrawableResource>,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onBackpressed: () -> Unit = {}
) {
    // 食物和卡路里的对应关系
    val fakeList = remember { mutableStateListOf<Triple<String, Int, Int>>() }

    // lottie测试
    val composition by rememberLottieComposition {
        LottieCompositionSpec.JsonString(
            Res.readBytes("files/lottie_dish.json").decodeToString()
        )
    }

    val progress by animateLottieCompositionAsState(
        composition,
        iterations = Compottie.IterateForever,
        speed = 0.7f
    )

    val galleryPermissionState = viewModel.galleryPermissionState

    val scope = rememberCoroutineScope()

    val imageFile: MutableState<PlatformFile?> = remember { mutableStateOf(null) }

    val launcher = rememberFilePickerLauncher(
        type = FileKitType.Image,
    ) { image ->
        Napier.d("Image File Choosed imagefile: ${image?.path}")
        image?.let {
            imageFile.value = it
        }
    }

    LaunchedEffect(Unit) {
        fakeList.add(Triple("苹果", 200, 100))
        fakeList.add(Triple("可乐", 100, 200))
        fakeList.add(Triple("汉堡", 20, 300))
        fakeList.add(Triple("薯条", 20, 400))
        fakeList.add(Triple("炸鸡", 22, 400))
        fakeList.add(Triple("玉米", 100, 100))
        fakeList.add(Triple("白菜", 200, 100))
        fakeList.add(Triple("鸡蛋", 30, 200))
        fakeList.add(Triple("面包", 100, 400))
        fakeList.add(Triple("面包2", 100, 400))
        fakeList.add(Triple("面包34", 100, 400))
        fakeList.add(Triple("面包4", 100, 400))
        fakeList.add(Triple("面包5", 100, 400))
    }

    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(1f).padding(bottom = 20.dp)
        ) {
            // 返回图标
            Image(
                painter = painterResource(Res.drawable.arrow_left),
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimary),
                contentDescription = "back",
                modifier = Modifier
                    .padding(horizontal = 10.dp)
                    .size(28.dp)
                    .clickable { onBackpressed() }
            )
            CenterText(
                text = funcInfo.first,
                style = titleSecondText,
                modifier = Modifier.sharedElement(
                    sharedContentState = rememberSharedContentState(
                        key = funcInfo.first
                    ),
                    animatedVisibilityScope = animatedVisibilityScope
                )
            )
        }

        Image(
            painter = rememberLottiePainter(
                composition = composition,
                progress = { progress },
            ),
            contentScale = ContentScale.Inside,
            contentDescription = null,
            modifier = Modifier.sharedElement(
                sharedContentState = rememberSharedContentState(
                    key = funcInfo.second.toString()
                ),
                animatedVisibilityScope = animatedVisibilityScope
            ).fillMaxWidth(1f).heightIn(max = 140.dp).padding(10.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(if (isSystemInDarkTheme()) darkCalorieColor else lightCalorieColor)
        )

        Box(
            modifier = Modifier.animateContentSize().padding(10.dp).fillMaxWidth(1f)
                .heightIn(min = 200.dp, max = 300.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.surface).clickable {
                    when (galleryPermissionState.value) {
                        PermissionState.Granted -> {
                            println("Gallery permission granted!")
                            scope.launch {
                                launcher.launch()
                            }
                        }

                        PermissionState.DeniedAlways -> {
                            println("Permission was permanently declined.")
                            viewModel.openPermissionSettings()
                        }

                        else -> {
                            viewModel.provideOrRequestGalleryPermission()
                            println("Request permission")
                        }
                    }
                }.padding(10.dp),
            contentAlignment = Alignment.Center
        ) {
            CenterText(
                text = "点击上传照片",
            )
            imageFile.value?.let {
                AsyncImage(
                    model = it,
                    contentDescription = "user choosed image",
                    modifier = Modifier.fillMaxWidth(1f).clip(RoundedCornerShape(10.dp)),
                    contentScale = ContentScale.Crop,
                )
            }
        }

        Column(
            modifier = Modifier
                .animateContentSize()
                .padding(horizontal = 10.dp)
                .weight(1f)
                .fillMaxWidth(1f)
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.surface)
        ) {
            CenterText(
                text = "食物列表", modifier = Modifier.padding(10.dp),
                style = titleSecondText
            )
            LazyColumn {
                items(fakeList, key = { it.first }) {
                    FoodListItem(it.first, it.second, it.third)
                }
            }
        }
    }
}

@Composable
fun FoodListItem(
    name: String,
    weight: Int,
    calories: Int,
) {
    Row(
        modifier = Modifier
            .padding(vertical = 5.dp, horizontal = 10.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.onSurface)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CenterText(
                text = name,
                modifier = Modifier.padding(start = 10.dp),
            )
            Spacer(Modifier.weight(1f))
            CenterText(text = "$weight g")
        }
        Row(
            Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(Modifier.weight(1f))
            CenterText(
                text = "$calories kcal",
                modifier = Modifier.padding(end = 10.dp)
            )
        }
    }
}
