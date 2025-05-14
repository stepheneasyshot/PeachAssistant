package com.stephen.permissions

class RequestCanceledException(
    val permission: Permission,
    message: String? = null
) : Exception(message)
