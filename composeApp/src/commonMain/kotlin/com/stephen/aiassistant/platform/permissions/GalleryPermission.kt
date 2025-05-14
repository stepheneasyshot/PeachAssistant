/*
 * Copyright 2025 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package com.stephen.aiassistant.platform.permissions

import com.stephen.permissions.Permission
import com.stephen.permissions.PermissionDelegate

internal expect val galleryDelegate: PermissionDelegate

object GalleryPermission : Permission {
    override val delegate get() = galleryDelegate
}

val Permission.Companion.GALLERY get() = GalleryPermission
