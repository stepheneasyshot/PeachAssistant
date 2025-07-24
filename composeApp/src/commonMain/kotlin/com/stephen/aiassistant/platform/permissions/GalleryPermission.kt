package com.stephen.aiassistant.platform.permissions

import com.stephen.permissions.Permission
import com.stephen.permissions.PermissionDelegate

internal expect val galleryDelegate: PermissionDelegate

object GalleryPermission : Permission {
    override val delegate get() = galleryDelegate
}

val Permission.Companion.GALLERY get() = GalleryPermission
