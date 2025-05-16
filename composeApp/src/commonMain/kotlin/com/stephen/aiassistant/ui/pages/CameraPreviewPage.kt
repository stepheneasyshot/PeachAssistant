package com.stephen.aiassistant.ui.pages

import aiassistant.composeapp.generated.resources.Res
import aiassistant.composeapp.generated.resources.ic_close_small
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import com.kashif.cameraK.controller.CameraController
import com.kashif.cameraK.enums.CameraLens
import com.kashif.cameraK.enums.Directory
import com.kashif.cameraK.enums.FlashMode
import com.kashif.cameraK.enums.ImageFormat
import com.kashif.cameraK.result.ImageCaptureResult
import com.kashif.cameraK.ui.CameraPreview
import com.kashif.imagesaverplugin.ImageSaverConfig
import com.kashif.imagesaverplugin.rememberImageSaverPlugin
import com.stephen.aiassistant.platform.getSystemCurrentMills
import com.stephen.aiassistant.ui.component.bounceClick
import io.github.aakira.napier.Napier
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource

@Composable
fun CameraPreviewPage(
    onShotImageBytesBack: (ByteArray) -> Unit,
    onCloseCameraPage: () -> Unit,
) {

    val cameraController = remember { mutableStateOf<CameraController?>(null) }

    // Configure plugins if needed
    val imageSaverPlugin = rememberImageSaverPlugin(
        config = ImageSaverConfig(
            isAutoSave = false,
            prefix = "PeachAssistant_",
            directory = Directory.PICTURES,
            customFolderName = "PeachAssistant"  // Android only
        )
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
                addPlugin(imageSaverPlugin)
            },
            onCameraControllerReady = {
                cameraController.value = it
            }
        )

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
                .padding(vertical = 30.dp),
            contentAlignment = Alignment.Center
        ) {
            // 为 Spacer 设置圆形边框背景
            Spacer(
                modifier = Modifier.size(88.dp)
                    .clip(RoundedCornerShape(50))
                    .background(MaterialTheme.colorScheme.surface)
                    .border(
                        2.dp,
                        MaterialTheme.colorScheme.onPrimary,
                        RoundedCornerShape(50)
                    )
            )
            Spacer(
                modifier = Modifier.size(78.dp)
                    .bounceClick(onUp = {
                        scope.launch {
                            when (val result = cameraController.value?.takePicture()) {
                                is ImageCaptureResult.Success -> {
                                    Napier.i("Image Capture Success!")
                                    // Manually save the image if auto-save is disabled
                                    if (!imageSaverPlugin.config.isAutoSave) {
                                        imageSaverPlugin.saveImage(
                                            byteArray = result.byteArray,
                                            imageName = "PeachAssistant_${getSystemCurrentMills()}"
                                        )
                                    }
                                    // Handle the captured image
                                    onShotImageBytesBack(result.byteArray)
                                }

                                is ImageCaptureResult.Error -> {
                                    Napier.i("Image Capture Error: ${result.exception.message}")
                                }

                                null -> {
                                    Napier.e("CameraController is null")
                                }
                            }
                        }
                    })
                    .clip(RoundedCornerShape(50))
                    .background(MaterialTheme.colorScheme.onPrimary)
            )
        }
    }
}