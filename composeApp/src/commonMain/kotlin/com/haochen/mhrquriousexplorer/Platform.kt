package com.haochen.mhrquriousexplorer

import androidx.compose.ui.Modifier
import kotlinx.io.files.Path

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform

expect fun getBaseDir(): Path?

expect fun Modifier.onScrollWheel(onScroll: (deltaX: Float, deltaY: Float) -> Unit): Modifier