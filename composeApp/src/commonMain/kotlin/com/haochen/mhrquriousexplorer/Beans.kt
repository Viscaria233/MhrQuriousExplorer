package com.haochen.mhrquriousexplorer

import kotlin.concurrent.atomics.AtomicInt
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.concurrent.atomics.incrementAndFetch

@OptIn(ExperimentalAtomicApi::class)
private val idGenerator = AtomicInt(0)

@OptIn(ExperimentalAtomicApi::class)
data class SearchGroup(
    val id: Int = idGenerator.incrementAndFetch(),
    val items: List<QuriousItem> = emptyList()
)

@OptIn(ExperimentalAtomicApi::class)
data class QuriousItem(
    val id: Int = idGenerator.incrementAndFetch(),
    val name: String = "",
    val count: Int = 0,
)

@OptIn(ExperimentalAtomicApi::class)
data class QuriousResult(
    val seq: Int,
    val conditions: List<SearchGroup>,
    val items: List<QuriousItem>,
) {
    val overview: List<QuriousItem> = items.asSequence()
            .groupBy { it.name }
            .mapValues { entry -> entry.value.sumOf { it.count } }
            .filterValues { it != 0 }
            .map { (name, count) -> QuriousItem(name = name, count = count) }

    val text: String
        get() = StringBuilder().apply {
            append("# $seq\n")
            overview.forEach {
                append("  ${it.name}: ${it.count}\n")
            }
        }.toString()
}