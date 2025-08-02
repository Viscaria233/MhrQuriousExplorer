package com.haochen.mhrquriousexplorer

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import kotlinx.io.files.Path

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