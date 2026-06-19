package com.haochen.mhrquriousexplorer

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.exponentialDecay
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.Clipboard
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.io.files.Path
import kotlin.math.abs

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

@Composable
actual fun Modifier.lazyRowDragWithInertia(
    state: LazyListState,
    enabled: Boolean,
    frictionMultiplier: Float,
): Modifier = this