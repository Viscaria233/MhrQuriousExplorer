package com.haochen.mhrquriousexplorer

import kotlinx.io.files.Path

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform

expect fun getBaseDir(): Path?