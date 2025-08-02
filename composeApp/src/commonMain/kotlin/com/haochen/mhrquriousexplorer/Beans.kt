package com.haochen.mhrquriousexplorer

import kotlin.concurrent.atomics.AtomicInt
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.concurrent.atomics.incrementAndFetch

@OptIn(ExperimentalAtomicApi::class)
internal data class SearchGroup(
    val id: Int = idGenerator.incrementAndFetch(),
    val items: List<SearchItem> = emptyList()
)

@OptIn(ExperimentalAtomicApi::class)
internal data class SearchItem(
    val id: Int = idGenerator.incrementAndFetch(),
    val name: String = "",
    val count: Int = 0,
)

@OptIn(ExperimentalAtomicApi::class)
private val idGenerator = AtomicInt(0)