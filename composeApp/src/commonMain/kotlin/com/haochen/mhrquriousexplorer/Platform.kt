package com.haochen.mhrquriousexplorer

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform