package com.stephen.aiassistant.platform.permissions

import com.stephen.permissions.Permission
import com.stephen.permissions.PermissionDelegate

internal expect val recordAudioDelegate: PermissionDelegate

object RecordAudioPermission : Permission {
    override val delegate get() = recordAudioDelegate
}

val Permission.Companion.RECORD_AUDIO get() = RecordAudioPermission
