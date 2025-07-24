package com.stephen.aiassistant.platform.permissions

import com.stephen.aiassistant.platform.permissions.avfoundation.AVCaptureDelegate
import com.stephen.permissions.Permission
import com.stephen.permissions.PermissionDelegate
import platform.AVFoundation.AVMediaTypeAudio

actual val recordAudioDelegate: PermissionDelegate = AVCaptureDelegate(
    AVMediaTypeAudio,
    Permission.RECORD_AUDIO
)
