package com.stephen.aiassistant.ui.pages

import aiassistant.composeapp.generated.resources.Res
import aiassistant.composeapp.generated.resources.ic_check
import aiassistant.composeapp.generated.resources.ic_close_small
import aiassistant.composeapp.generated.resources.ic_redo
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
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
import com.kashif.cameraK.controller.CameraController
import com.kashif.cameraK.enums.CameraLens
import com.kashif.cameraK.enums.Directory
import com.kashif.cameraK.enums.FlashMode
import com.kashif.cameraK.enums.ImageFormat
import com.kashif.cameraK.result.ImageCaptureResult
import com.kashif.cameraK.ui.CameraPreview
import com.stephen.aiassistant.ui.component.bounceClick
import com.stephen.aiassistant.vm.MainViewModel
import io.github.aakira.napier.Napier
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.getKoin

@Composable
fun CameraPreviewPage(
    viewModel: MainViewModel = getKoin().get(),
    onConfirmCameraShot: () -> Unit,
    onCloseCameraPage: () -> Unit,
) {

    val cameraController = remember { mutableStateOf<CameraController?>(null) }

    val imageCaptureResult = remember { mutableStateOf<ImageCaptureResult?>(null) }

    val buttonPaddinAnim by animateDpAsState(
        if (imageCaptureResult.value != null) 200.dp else 0.dp,
        label = "button padding anim"
    )

    val clickedConfirmState = remember { mutableStateOf(false) }

    val previewPicSahreAnim by animateFloatAsState(
        if (clickedConfirmState.value) 0.3f else 1f,
        animationSpec = tween(500),
        label = "preview pic share anim"
    )

    val scope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize(1f)) {
        CameraPreview(
            modifier = Modifier.fillMaxSize(),
            cameraConfiguration = {
                setCameraLens(CameraLens.BACK)
                setFlashMode(FlashMode.OFF)
                setImageFormat(ImageFormat.JPEG)
                setDirectory(Directory.PICTURES)
            },
            onCameraControllerReady = {
                cameraController.value = it
            }
        )

        if (imageCaptureResult.value != null) {
            Box(
                modifier = Modifier.fillMaxSize(1f)
                    .background(MaterialTheme.colorScheme.background),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = (imageCaptureResult.value as ImageCaptureResult.Success).byteArray,
                    contentScale = ContentScale.FillWidth,
                    contentDescription = "temp photo preview",
                    modifier = Modifier.padding(bottom = 100.dp)
                        .fillMaxWidth(1f).fillMaxHeight(previewPicSahreAnim)
                )
            }
        }
        // 关闭按钮
        Image(
            painter = painterResource(Res.drawable.ic_close_small),
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.background),
            contentDescription = "exit camera page",
            modifier = Modifier
                .padding(10.dp)
                .size(40.dp)
                .clip(RoundedCornerShape(50))
                .background(MaterialTheme.colorScheme.onPrimary)
                .align(Alignment.TopStart)
                .clickable {
                    onCloseCameraPage()
                }
        )

        Box(
            modifier = Modifier.align(Alignment.BottomCenter)
                .fillMaxWidth(1f)
                .background(MaterialTheme.colorScheme.surface)
                .padding(vertical = 32.dp),
        ) {
            // 为 Spacer 设置圆形边框背景
            Box(
                modifier = Modifier.align(Alignment.Center)
                    .padding(end = buttonPaddinAnim)
                    .size(84.dp).clip(RoundedCornerShape(50))
                    .background(MaterialTheme.colorScheme.surface)
                    .border(
                        4.dp,
                        MaterialTheme.colorScheme.onPrimary,
                        RoundedCornerShape(50)
                    ),
                contentAlignment = Alignment.Center
            ) {
                AnimatedVisibility(visible = (imageCaptureResult.value != null)) {
                    Image(
                        painter = painterResource(Res.drawable.ic_redo),
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimary),
                        contentDescription = "retry take pic",
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(50))
                            .clickable {
                                imageCaptureResult.value = null
                            }
                    )
                }
            }
            Box(
                modifier = Modifier.align(Alignment.Center)
                    .padding(start = buttonPaddinAnim)
                    .size(72.dp)
                    .bounceClick(onUp = {
                        scope.launch {
                            imageCaptureResult.value?.let {
                                when (imageCaptureResult.value) {
                                    is ImageCaptureResult.Success -> {
                                        Napier.i("Image Capture Success!")
                                        viewModel.saveTempPicture((imageCaptureResult.value as ImageCaptureResult.Success).byteArray)
                                    }

                                    is ImageCaptureResult.Error -> {
                                        Napier.i("Image Capture Error: ${(imageCaptureResult.value as ImageCaptureResult.Error).exception.message}")
                                    }

                                    null -> {
                                        Napier.e("CameraController is null")
                                    }
                                }
                                scope.launch {
                                    clickedConfirmState.value = true
                                    delay(500L)
                                    onConfirmCameraShot()
                                }
                            } ?: run {
                                imageCaptureResult.value = cameraController.value?.takePicture()
                            }
                        }
                    })
                    .clip(RoundedCornerShape(50))
                    .background(MaterialTheme.colorScheme.onPrimary),
                contentAlignment = Alignment.Center
            ) {
                AnimatedVisibility(visible = (imageCaptureResult.value != null)) {
                    Image(
                        painter = painterResource(Res.drawable.ic_check),
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.background),
                        contentDescription = "check pic",
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(50))
                    )
                }
            }
        }
    }
}