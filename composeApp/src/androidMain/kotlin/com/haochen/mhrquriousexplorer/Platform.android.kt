package com.haochen.mhrquriousexplorer

import android.content.ClipData
import android.os.Build
import android.os.Environment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.Clipboard
import androidx.compose.ui.platform.toClipEntry
import kotlinx.io.files.Path

class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
}

actual fun getPlatform(): Platform = AndroidPlatform()

actual fun getBaseDir(): Path? = Path(Environment.getExternalStorageDirectory().absolutePath)

actual fun Modifier.onScrollWheel(onScroll: (deltaX: Float, deltaY: Float) -> Unit): Modifier = this

actual suspend fun Clipboard.setContent(content: String) {
    setClipEntry(ClipData.newPlainText("", content).toClipEntry())
}