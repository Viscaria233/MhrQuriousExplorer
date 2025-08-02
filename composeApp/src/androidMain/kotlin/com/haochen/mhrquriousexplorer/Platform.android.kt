package com.haochen.mhrquriousexplorer

import android.os.Build
import android.os.Environment
import kotlinx.io.files.Path

class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
}

actual fun getPlatform(): Platform = AndroidPlatform()

actual fun getBaseDir(): Path? = Path(Environment.getExternalStorageDirectory().absolutePath)