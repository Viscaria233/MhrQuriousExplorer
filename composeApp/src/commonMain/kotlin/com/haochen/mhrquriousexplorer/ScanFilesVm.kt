package com.haochen.mhrquriousexplorer

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem

class ScanFilesVm : ViewModel() {
    private val _files = MutableStateFlow<List<Path>>(emptyList())
    val files = _files.asStateFlow()

    init {
        refreshFiles()
    }

    fun refreshFiles() {
        _files.value = scanFiles()
    }

    private fun scanFiles(): List<Path> {
        val baseDir = getBaseDir() ?: run {
            println("scanFiles not supported on platform ${getPlatform()}")
            return emptyList()
        }
        return SystemFileSystem.list(baseDir).filter { it.name.endsWith(SCAN_FILE_EXT) }.also {
            println("scanFiles\n  $it")
        }
    }

    companion object Companion {
        private const val SCAN_FILE_EXT = ".csv"
    }
}