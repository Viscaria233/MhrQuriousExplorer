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
import androidx.compose.ui.platform.LocalClipboard
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.io.files.Path
import java.awt.datatransfer.StringSelection
import kotlin.math.abs

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

@Composable
actual fun Modifier.lazyRowDragWithInertia(
    state: LazyListState,
    enabled: Boolean,
    frictionMultiplier: Float,
): Modifier {
    val scope = rememberCoroutineScope()

    val decay = remember(frictionMultiplier) {
        exponentialDecay<Float>(
            frictionMultiplier = frictionMultiplier
        )
    }

    var flingJob by remember {
        mutableStateOf<Job?>(null)
    }

    val dragState = rememberDraggableState { delta ->
        // 鼠标向右拖时，LazyRow 应该向左滚动偏移减少，所以这里取负
        flingJob?.cancel()
        state.dispatchRawDelta(-delta)
    }

    return this.draggable(
        state = dragState,
        orientation = Orientation.Horizontal,
        enabled = enabled,
        onDragStarted = {
            flingJob?.cancel()
        },
        onDragStopped = { velocity ->
            flingJob?.cancel()

            flingJob = scope.launch {
                var lastValue = 0f

                Animatable(0f).animateDecay(
                    initialVelocity = -velocity,
                    animationSpec = decay
                ) {
                    val delta = value - lastValue
                    lastValue = value

                    val consumed = state.dispatchRawDelta(delta)

                    // 到边界时停止动画，避免空转
                    if (abs(consumed - delta) > 0.5f) {
                        this@launch.cancel()
                    }
                }
            }
        }
    )
}