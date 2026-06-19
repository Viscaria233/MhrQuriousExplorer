package com.haochen.mhrquriousexplorer.loader

import com.haochen.mhrquriousexplorer.QuriousItem
import com.haochen.mhrquriousexplorer.QuriousResult
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.io.readLine

/**
 * Created by noahwu on 2026/6/19.
 * Copyright (c) Tencent. All rights reserved.
 */
class QuriousCsvLoader : FileLoader {
    override fun load(file: Path): List<QuriousResult> {
        val qurious = mutableMapOf<Int, MutableList<QuriousItem>>()
        SystemFileSystem.source(file).buffered().use {
            while (true) {
                val line = it.readLine() ?: break
                if (line.isEmpty()) {
                    continue
                }
                if (!line[0].isDigit()) {
                    continue
                }
                val (seq, content, count) = line.split(',')
                val (_, name) = content.split('`')
                qurious.getOrPut(seq.toInt()) { mutableListOf() }
                        .add(QuriousItem(name = name, count = count.toInt()))
            }
        }
        return qurious.map { QuriousResult(seq = it.key, items = it.value) }
    }
}