package com.stephen.aiassistant.ui.pages

import aiassistant.composeapp.generated.resources.Res
import aiassistant.composeapp.generated.resources.arrow_left
import aiassistant.composeapp.generated.resources.bg_calorie_cal
import aiassistant.composeapp.generated.resources.ic_camera
import aiassistant.composeapp.generated.resources.ic_close_small
import aiassistant.composeapp.generated.resources.ic_close_with_circle
import aiassistant.composeapp.generated.resources.ic_picture_album
import aiassistant.composeapp.generated.resources.ic_upload
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.stephen.aiassistant.data.navBarHeight
import com.stephen.aiassistant.platform.hardware.rememberVibrationManager
import com.stephen.aiassistant.ui.component.BlueAreaScanEffect
import com.stephen.aiassistant.ui.component.CenterText
import com.stephen.aiassistant.ui.component.CommonButton
import com.stephen.aiassistant.ui.component.DashedRoundedRectangle
import com.stephen.aiassistant.ui.component.rememberToastState
import com.stephen.aiassistant.ui.theme.CooperFontFamily
import com.stephen.aiassistant.ui.theme.darkCalorieCardBgColor
import com.stephen.aiassistant.ui.theme.infoText
import com.stephen.aiassistant.ui.theme.lightCalorieCardBgColor
import com.stephen.aiassistant.ui.theme.titleSecondText
import com.stephen.aiassistant.vm.MainViewModel
import com.stephen.permissions.PermissionState
import io.github.aakira.napier.Napier
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
    onBackPressed: () -> Unit = {}
) {

    val toastState = rememberToastState()

    // 食物和卡路里的对应关系
    val foodListState = viewModel.foodListStateFlow.collectAsState()

    // lottie测试
    val composition by rememberLottieComposition {
        LottieCompositionSpec.JsonString(
            Res.readBytes("files/lottie_dish.json").decodeToString()
        )
    }

    val progress by animateLottieCompositionAsState(
        composition,
        iterations = 1,
        speed = 0.7f
    )

    val startTransitionAnim = remember { mutableStateOf(false) }
    val dishJsonBgColorAlphaAnim =
        animateColorAsState(
            targetValue = if (startTransitionAnim.value) {
                if (viewModel.isDarkTheme()) darkCalorieCardBgColor else lightCalorieCardBgColor
            } else Color.Transparent,
            animationSpec = tween(2000)
        )
    val dishJsonPaddingAnim =
        animateDpAsState(
            targetValue = if (startTransitionAnim.value) 160.dp else 0.dp,
            animationSpec = tween(2000)
        )

    val vibrationManager = rememberVibrationManager()

    LaunchedEffect(Unit) {
        startTransitionAnim.value = true
        launch(Dispatchers.Main) {
            val vibrateArrayPattern = longArrayOf(1100L, 60, 120, 60, 120, 60, 120, 60, 120, 60)
            vibrationManager.vibratePattern(vibrateArrayPattern)
        }
    }

    val galleryPermissionState = viewModel.galleryPermissionState
    val cameraPermissionState = viewModel.cameraPermissionState
    val storagePermissionState = viewModel.storagePermissionState
    val writeStoragePermissionState = viewModel.writeStoragePermissionState
    val expandPictureAddState = remember { mutableStateOf(false) }
    val openCameraPreviewState = remember { mutableStateOf(false) }

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

            Box(
                modifier = Modifier.padding(10.dp).fillMaxWidth(1f).height(140.dp)
                    .clip(RoundedCornerShape(20.dp)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(Res.drawable.bg_calorie_cal),
                    contentScale = ContentScale.FillWidth,
                    contentDescription = null,
                    modifier = Modifier.sharedElement(
                        sharedContentState = rememberSharedContentState(
                            key = funcInfo.second.toString()
                        ),
                        animatedVisibilityScope = animatedVisibilityScope
                    ).fillMaxWidth(1f).heightIn(max = 140.dp)
                        .clip(RoundedCornerShape(20.dp))
                )
                Box(
                    modifier = Modifier.fillParentMaxSize(1f)
                        .background(dishJsonBgColorAlphaAnim.value),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = rememberLottiePainter(
                            composition = composition,
                            progress = { progress },
                        ),
                        contentScale = ContentScale.Fit,
                        contentDescription = "json animation",
                        modifier = Modifier
                            .padding(end = dishJsonPaddingAnim.value)
                            .size(120.dp)
                    )

                    AnimatedVisibility(
                        visible = startTransitionAnim.value
                    ) {
                        Text(
                            text = "Calorie\nCalculator",
                            style = titleSecondText,
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontFamily = CooperFontFamily(),
                            modifier = Modifier.padding(start = dishJsonPaddingAnim.value)
                        )
                    }
                }
            }

            Box(
                modifier = Modifier.animateContentSize().padding(10.dp).fillMaxWidth(1f)
                    .heightIn(min = 200.dp, max = 250.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                    ) {
                        expandPictureAddState.value = true
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

                // 二者只要有一个非空，就显示上传和重选的控件
                if (imageTempByteArrayState.value != null || imageFileState.value != null) {
                    AsyncImage(
                        model = imageTempByteArrayState.value ?: imageFileState.value,
                        contentDescription = "user chose image",
                        modifier = Modifier.fillMaxWidth(1f).clip(RoundedCornerShape(20.dp)),
                        contentScale = ContentScale.Crop,
                    )
                    if (foodListState.value.loading) {
                        BlueAreaScanEffect(
                            modifier = Modifier.fillMaxWidth(1f).clip(RoundedCornerShape(20.dp))
                        )
                    }
                    // 叉号，清空重选
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
                                // 相机拍照清除
                                viewModel.clearTempPicture()
                                // 相册选取的清除
                                imageFileState.value = null
                                // 重置加载状态
                                viewModel.onReselectPicture()
                            }
                    )
                    CommonButton(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .clip(RoundedCornerShape(20.dp))
                            .padding(horizontal = 10.dp),
                        onClick = {
                            if (foodListState.value.loading) {
                                toastState.show("正在识别食物，请稍后再试...")
                            } else {
                                // 相机拍照临时图上传分析
                                imageTempByteArrayState.value?.let {
                                    viewModel.calculateCalorieByAI(isUsingCameraTemp = true)
                                }

                                // 非空，上传照片，开始分析卡路里
                                imageFileState.value?.let { file ->
                                    viewModel.calculateCalorieByAI(galleryImageFile = file)
                                }
                            }
                        }
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = painterResource(Res.drawable.ic_upload),
                                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimary),
                                contentDescription = "upload image",
                                modifier = Modifier.size(20.dp)
                            )
                            CenterText(
                                text = "上传",
                                modifier = Modifier.padding(horizontal = 5.dp)
                            )
                        }
                    }
                }
            }

            Column(
                modifier = Modifier
                    .animateContentSize()
                    .padding(horizontal = 10.dp)
                    .fillMaxWidth(1f)
                    .clip(RoundedCornerShape(20.dp))
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                // error
                if (foodListState.value.errorMessage.isNotEmpty()) {
                    CenterText(
                        text = foodListState.value.errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.fillMaxWidth(1f).padding(10.dp),
                    )
                } else if (foodListState.value.loading) {
                    // loading
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth(1f).padding(10.dp)
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.padding(end = 10.dp).size(24.dp)
                        )
                        CenterText(text = "正在识别食物...")
                    }
                } else {
                    foodListState.value.foodList?.apply {
                        // 有结果
                        if (isNotEmpty()) {
                            CenterText(
                                text = "食物列表", modifier = Modifier.padding(10.dp),
                                style = titleSecondText
                            )
                            forEach {
                                FoodListItem(it.name, it.weight, it.calorie)
                            }
                        } else {
                            // 没有结果，未识别到食物
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier.fillMaxWidth(1f).padding(10.dp)
                            ) {
                                Image(
                                    painter = painterResource(Res.drawable.ic_close_with_circle),
                                    contentDescription = "no food",
                                    modifier = Modifier.padding(end = 10.dp).size(24.dp),
                                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.error)
                                )
                                CenterText(text = "图中未识别到食物")
                            }
                        }
                    }
                }
            }
        }
    }

    AnimatedVisibility(
        visible = openCameraPreviewState.value,
        enter = fadeIn(),
        exit = ExitTransition.None,
        modifier = Modifier.fillMaxSize(1f)
    ) {
        CameraPreviewPage(
            onConfirmCameraShot = {
                openCameraPreviewState.value = false
            },
            onCloseCameraPage = {
                openCameraPreviewState.value = false
            })
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
        visible = expandPictureAddState.value,
        enter = enterTransition, exit = exitTransition,
        modifier = Modifier.fillMaxSize(1f)
    ) {
        CameraGallerySelectView(
            onCameraClick = {
                expandPictureAddState.value = false
                // 三权合一，才可以进行下一步操作，拍照、存储、写入存储
                if (cameraPermissionState.value == PermissionState.Granted
                    && storagePermissionState.value == PermissionState.Granted
                    && writeStoragePermissionState.value == PermissionState.Granted
                ) {
                    openCameraPreviewState.value = true
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
                        viewModel.checkCameraPermission()
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
                        viewModel.checkStoragePermission()
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
                        viewModel.checkWriteStoragePermission()
                    }
                }
            },
            onGalleryClick = {
                expandPictureAddState.value = false
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
                        viewModel.checkGalleryPermission()
                        Napier.i("Request permission")
                    }
                }
            },
            onDismissRequest = {
                expandPictureAddState.value = false
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
            .clip(RoundedCornerShape(20.dp))
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
        Box(
            modifier = Modifier.fillMaxWidth(1f).align(Alignment.BottomCenter)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                ) {
                    Napier.d("click empty area, do nothing")
                },
        ) {
            Row(
                modifier = Modifier.align(Alignment.Center)
                    .fillMaxWidth(1f)
                    .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(vertical = 56.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Column(
                    modifier = Modifier.padding(end = 60.dp)
                        .size(72.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(MaterialTheme.colorScheme.onSurface)
                        .clickable {
                            onCameraClick()
                        },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(Res.drawable.ic_camera),
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimary),
                        modifier = Modifier.size(28.dp),
                        contentDescription = "camera"
                    )
                    CenterText(text = "拍照", style = infoText)
                }
                Column(
                    modifier = Modifier.size(72.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(MaterialTheme.colorScheme.onSurface)
                        .clickable {
                            onGalleryClick()
                        },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(Res.drawable.ic_picture_album),
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimary),
                        modifier = Modifier.size(28.dp),
                        contentDescription = "gallery"
                    )
                    CenterText(text = "相册", style = infoText)
                }
            }
            Spacer(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth(0.1f).height(15.dp)
                    // 监听下滑事件
                    .pointerInput(Unit) {
                        detectDragGestures { _, dragAmount ->
                            if (dragAmount.y > 0) {
                                onDismissRequest()
                            }
                        }
                    }
                    .padding(top = 10.dp)
                    .clip(RoundedCornerShape(50))
                    .background(MaterialTheme.colorScheme.onSecondary)
            )
        }
    }
}