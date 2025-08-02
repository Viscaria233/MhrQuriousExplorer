package com.haochen.mhrquriousexplorer

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalInspectionMode

@Composable
fun <T> withPreview(realData: T, previewData: () -> T): T {
    return if (LocalInspectionMode.current) previewData() else realData
}

val Exception.logMsg: String
    get() = "${this::class.qualifiedName}: ${message}\n${stackTraceToString()}"