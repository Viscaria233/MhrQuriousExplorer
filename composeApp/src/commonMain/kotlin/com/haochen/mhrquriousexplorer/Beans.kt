package com.haochen.mhrquriousexplorer

import kotlin.concurrent.atomics.AtomicInt
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.concurrent.atomics.incrementAndFetch

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
    val overview: Map<String, Int> = items.asSequence()
            .groupBy { it.name }
            .mapValues { entry -> entry.value.sumOf { it.count } }
            .filterValues { it != 0 }
}

fun QuriousResult.meets(condition: SearchGroup): Boolean {
    return condition.items.any { meets(it) }
}

fun QuriousResult.meets(condition: QuriousItem): Boolean {
    return overview.any { (name, count) -> name.contains(condition.name) && count >= condition.count }
}

@OptIn(ExperimentalAtomicApi::class)
private val idGenerator = AtomicInt(0)