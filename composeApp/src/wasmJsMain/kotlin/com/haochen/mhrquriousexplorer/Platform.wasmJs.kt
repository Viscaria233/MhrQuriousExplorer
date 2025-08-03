package com.haochen.mhrquriousexplorer

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.Clipboard
import kotlinx.io.files.Path

class WasmPlatform: Platform {
    override val name: String = "Web with Kotlin/Wasm"
}

actual fun getPlatform(): Platform = WasmPlatform()

actual fun getBaseDir(): Path? = null

@OptIn(ExperimentalComposeUiApi::class)
actual fun Modifier.onScrollWheel(onScroll: (deltaX: Float, deltaY: Float) -> Unit): Modifier = onPointerEvent(
    eventType = PointerEventType.Scroll,
    onEvent = { event ->
        val delta = event.changes.first().scrollDelta
        onScroll(delta.x, delta.y)
    }
)

actual suspend fun Clipboard.setContent(content: String) {
    setClipEntry(ClipEntry.withPlainText(content))
}