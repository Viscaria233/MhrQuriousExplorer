package com.haochen.mhrquriousexplorer

import kotlinx.io.files.Path

class JVMPlatform: Platform {
    override val name: String = "Java ${System.getProperty("java.version")}"
}

actual fun getPlatform(): Platform = JVMPlatform()

actual fun getBaseDir(): Path? = Path(".")