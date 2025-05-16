package com.stephen.aiassistant.ui.pages

import aiassistant.composeapp.generated.resources.Res
import aiassistant.composeapp.generated.resources.arrow_left
import aiassistant.composeapp.generated.resources.ic_camera
import aiassistant.composeapp.generated.resources.ic_close_small
import aiassistant.composeapp.generated.resources.ic_picture_album
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.stephen.aiassistant.data.navBarHeight
import com.stephen.aiassistant.platform.hardware.rememberVibrationManager
import com.stephen.aiassistant.ui.component.CenterText
import com.stephen.aiassistant.ui.component.CommonButton
import com.stephen.aiassistant.ui.component.DashedRoundedRectangle
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
import io.github.vinceglb.filekit.name
import io.github.vinceglb.filekit.path
import kotlinx.coroutines.Dispatchers
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
    onOpenCameraPreview: () -> Unit = {},
    onBackPressed: () -> Unit = {}
) {
    // 食物和卡路里的对应关系
    val foodList = viewModel.foodListStateFlow.collectAsState()

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
    val cameraPermissionState = viewModel.cameraPermissionState
    val storagePermissionState = viewModel.storagePermissionState
    val writeStoragePermissionState = viewModel.writeStoragePermissionState
    val expandPicturaAddState = remember { mutableStateOf(false) }

    val imageTempByteArrayState = viewModel.imageTempByteArrayState.collectAsState()

    val scope = rememberCoroutineScope()

    val imageFileState: MutableState<PlatformFile?> = remember { mutableStateOf(null) }

    val launcher = rememberFilePickerLauncher(
        type = FileKitType.Image,
    ) { imageFile ->
        Napier.i("Chose imageFile: name: ${imageFile?.name}, path:${imageFile?.path}")
        imageFile?.let {
            imageFileState.value = it
        }
    }

    val vibrationManager = rememberVibrationManager()

    LaunchedEffect(Unit) {
        launch(Dispatchers.Main) {
            val vibrateArrayPattern = longArrayOf(1100L, 60, 120, 60, 120, 60, 120, 60, 120, 60)
            vibrationManager.vibratePattern(vibrateArrayPattern)
        }
    }

    LazyColumn(modifier = Modifier.padding(bottom = navBarHeight)) {
        item {
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
                        .clickable { onBackPressed() }
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
                    .heightIn(min = 200.dp, max = 250.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                    ) {
                        expandPicturaAddState.value = true
                    }.padding(10.dp),
                contentAlignment = Alignment.Center
            ) {
                DashedRoundedRectangle(
                    modifier = Modifier.size(1000.dp).padding(10.dp),
                    color = MaterialTheme.colorScheme.onSecondary,
                )
                CenterText(
                    text = "点击添加图片",
                )
                imageTempByteArrayState.value?.let {
                    AsyncImage(
                        model = it,
                        contentDescription = "user chose image",
                        modifier = Modifier.fillMaxWidth(1f).clip(RoundedCornerShape(10.dp)),
                        contentScale = ContentScale.Crop,
                    )
                    Image(
                        painter = painterResource(Res.drawable.ic_close_small),
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.background),
                        contentDescription = "unselect file",
                        modifier = Modifier
                            .size(30.dp)
                            .clip(RoundedCornerShape(50))
                            .background(MaterialTheme.colorScheme.onPrimary)
                            .align(Alignment.TopEnd)
                            .clickable {
                                viewModel.clearTempPicture()
                            }
                    )
                    CommonButton(
                        text = "上传",
                        modifier = Modifier
                            .fillMaxWidth(0.3f)
                            .align(Alignment.BottomCenter)
                            .clip(RoundedCornerShape(10.dp)),
                        onClick = {
                            viewModel.calCalorieUsingTemppicture()
                        }
                    )
                }
                imageFileState.value?.let {
                    AsyncImage(
                        model = it,
                        contentDescription = "user chose image",
                        modifier = Modifier.fillMaxWidth(1f).clip(RoundedCornerShape(10.dp)),
                        contentScale = ContentScale.Crop,
                    )
                    Image(
                        painter = painterResource(Res.drawable.ic_close_small),
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.background),
                        contentDescription = "unselect file",
                        modifier = Modifier
                            .size(30.dp)
                            .clip(RoundedCornerShape(50))
                            .background(MaterialTheme.colorScheme.onPrimary)
                            .align(Alignment.TopEnd)
                            .clickable {
                                imageFileState.value = null
                            }
                    )
                    CommonButton(
                        text = "上传",
                        modifier = Modifier
                            .fillMaxWidth(0.3f)
                            .align(Alignment.BottomCenter)
                            .clip(RoundedCornerShape(10.dp)),
                        onClick = {
                            // 非空，上传照片，开始分析卡路里
                            imageFileState.value?.let { file ->
                                viewModel.calCalorieByAI(file)
                            }
                        }
                    )
                }
            }

            Column(
                modifier = Modifier
                    .animateContentSize()
                    .padding(horizontal = 10.dp)
                    .fillMaxWidth(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                foodList.value.apply {
                    if (isNotEmpty()) {
                        CenterText(
                            text = "食物列表", modifier = Modifier.padding(10.dp),
                            style = titleSecondText
                        )
                        forEach {
                            FoodListItem(it.name, it.weight, it.calorie)
                        }
                    }
                }
            }
        }
    }

    // 定义从底部滑入和滑出的动画
    val enterTransition: EnterTransition = slideInVertically(
        initialOffsetY = { it },
        animationSpec = tween(durationMillis = 300)
    )
    val exitTransition: ExitTransition = slideOutVertically(
        targetOffsetY = { it },
        animationSpec = tween(durationMillis = 300)
    )

    AnimatedVisibility(
        visible = expandPicturaAddState.value,
        enter = enterTransition, exit = exitTransition,
        modifier = Modifier.fillMaxSize(1f)
    ) {
        CameraGallerySelectView(
            onCameraClick = {
                expandPicturaAddState.value = false
                // 三权合一，才可以进行下一步操作，拍照、存储、写入存储
                if (cameraPermissionState.value == PermissionState.Granted
                    && storagePermissionState.value == PermissionState.Granted
                    && writeStoragePermissionState.value == PermissionState.Granted
                ) {
                    onOpenCameraPreview()
                }
                when (cameraPermissionState.value) {
                    PermissionState.Granted -> {
                        Napier.i("Camera permission granted!")
                    }

                    PermissionState.DeniedAlways -> {
                        Napier.i("Permission was permanently declined.")
                        viewModel.openPermissionSettings()
                    }

                    else -> {
                        Napier.i("Request permission")
                        viewModel.provideOrRequestCameraPermission()
                    }
                }
                when (storagePermissionState.value) {
                    PermissionState.Granted -> {
                        Napier.i("Storage permission granted!")
                    }

                    PermissionState.DeniedAlways -> {
                        Napier.i("Permission was permanently declined.")
                        viewModel.openPermissionSettings()
                    }

                    else -> {
                        Napier.i("Request permission")
                        viewModel.provideOrRequestStoragePermission()
                    }
                }
                when (writeStoragePermissionState.value) {
                    PermissionState.Granted -> {
                        Napier.i("WriteStorage permission granted!")
                    }

                    PermissionState.DeniedAlways -> {
                        Napier.i("Permission was permanently declined.")
                        viewModel.openPermissionSettings()
                    }

                    else -> {
                        Napier.i("Request permission")
                        viewModel.provideOrRequestWriteStoragePermission()
                    }
                }
            },
            onGalleryClick = {
                expandPicturaAddState.value = false
                when (galleryPermissionState.value) {
                    PermissionState.Granted -> {
                        Napier.i("Gallery permission granted!")
                        scope.launch {
                            launcher.launch()
                        }
                    }

                    PermissionState.DeniedAlways -> {
                        Napier.i("Permission was permanently declined.")
                        viewModel.openPermissionSettings()
                    }

                    else -> {
                        viewModel.provideOrRequestGalleryPermission()
                        Napier.i("Request permission")
                    }
                }
            },
            onDismissRequest = {
                expandPicturaAddState.value = false
            }
        )
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

/**
 * 选择是打开图库还是拍照
 */
@Composable
fun CameraGallerySelectView(
    onCameraClick: () -> Unit,
    onGalleryClick: () -> Unit,
    onDismissRequest: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(1f).clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
        ) {
            onDismissRequest()
        },
    ) {
        Box(modifier = Modifier.fillMaxWidth(1f).align(Alignment.BottomCenter)) {
            Row(
                modifier = Modifier.align(Alignment.Center)
                    .fillMaxWidth(1f)
                    .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(vertical = 56.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Column(
                    modifier = Modifier.clickable {
                        onCameraClick()
                    }.padding(end = 78.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(Res.drawable.ic_camera),
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimary),
                        modifier = Modifier.size(32.dp),
                        contentDescription = "camera"
                    )
                    CenterText(text = "拍照")
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable {
                        onGalleryClick()
                    }
                ) {
                    Image(
                        painter = painterResource(Res.drawable.ic_picture_album),
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimary),
                        modifier = Modifier.size(32.dp),
                        contentDescription = "gallery"
                    )
                    CenterText(text = "相册")
                }
            }
            Spacer(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 10.dp)
                    .fillMaxWidth(0.1f).height(5.dp)
                    .clip(RoundedCornerShape(50))
                    .background(MaterialTheme.colorScheme.onSecondary)
            )
        }
    }
}