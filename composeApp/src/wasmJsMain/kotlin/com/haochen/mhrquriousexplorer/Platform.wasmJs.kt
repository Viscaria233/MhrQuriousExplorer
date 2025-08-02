package com.haochen.mhrquriousexplorer

import kotlinx.io.files.Path

class WasmPlatform: Platform {
    override val name: String = "Web with Kotlin/Wasm"
}

actual fun getPlatform(): Platform = WasmPlatform()

actual fun getBaseDir(): Path? = null