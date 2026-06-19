package com.haochen.mhrquriousexplorer

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.Clipboard
import kotlinx.io.files.Path

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform

expect fun getBaseDir(): Path?

expect fun Modifier.onScrollWheel(onScroll: (deltaX: Float, deltaY: Float) -> Unit): Modifier

expect suspend fun Clipboard.setContent(content: String)

@Composable
expect fun Modifier.lazyRowDragWithInertia(
    state: LazyListState,
    enabled: Boolean = true,
    frictionMultiplier: Float = 1.0f,
): Modifier