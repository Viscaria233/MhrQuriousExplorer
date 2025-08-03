package com.haochen.mhrquriousexplorer

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.Clipboard
import androidx.compose.ui.platform.LocalClipboard
import kotlinx.io.files.Path
import java.awt.datatransfer.StringSelection

class JVMPlatform: Platform {
    override val name: String = "Java ${System.getProperty("java.version")}"
}

actual fun getPlatform(): Platform = JVMPlatform()

actual fun getBaseDir(): Path? = Path(".")

@OptIn(ExperimentalComposeUiApi::class)
actual fun Modifier.onScrollWheel(onScroll: (deltaX: Float, deltaY: Float) -> Unit): Modifier = onPointerEvent(
    eventType = PointerEventType.Scroll,
    onEvent = { event ->
        val delta = event.changes.first().scrollDelta
        onScroll(delta.x, delta.y)
    }
)

actual suspend fun Clipboard.setContent(content: String) {
    (nativeClipboard as? java.awt.datatransfer.Clipboard)?.setContents(StringSelection(content), null)
}