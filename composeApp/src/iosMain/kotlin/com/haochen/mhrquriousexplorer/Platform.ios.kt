package com.haochen.mhrquriousexplorer

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.Clipboard
import kotlinx.io.files.Path
import platform.UIKit.UIDevice

class IOSPlatform: Platform {
    override val name: String = UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
}

actual fun getPlatform(): Platform = IOSPlatform()

actual fun getBaseDir(): Path? = null

actual fun Modifier.onScrollWheel(onScroll: (deltaX: Float, deltaY: Float) -> Unit): Modifier = this

@OptIn(ExperimentalComposeUiApi::class)
actual suspend fun Clipboard.setContent(content: String) {
    setClipEntry(ClipEntry.withPlainText(content))
}

@Composable
actual fun Modifier.lazyRowDragWithInertia(
    state: LazyListState,
    enabled: Boolean,
    frictionMultiplier: Float,
): Modifier = this