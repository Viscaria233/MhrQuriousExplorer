package com.haochen.mhrquriousexplorer.loader

import com.haochen.mhrquriousexplorer.QuriousResult
import kotlinx.io.files.Path

/**
 * Created by noahwu on 2026/6/19.
 * Copyright (c) Tencent. All rights reserved.
 */
interface FileLoader {
    fun load(file: Path): List<QuriousResult>
}