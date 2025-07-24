package com.stephen.permissions

interface Permission {
    val delegate: PermissionDelegate

    // Extended by individual permission delegates
    companion object
}
