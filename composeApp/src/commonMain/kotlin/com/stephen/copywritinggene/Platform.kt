package com.stephen.copywritinggene

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform

expect fun copyTextToClipBoard(text: String)