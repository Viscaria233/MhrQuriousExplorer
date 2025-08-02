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
}

data class MeetsInfo(
    val index: Int,
    val decreasedItem: QuriousItem,
)

fun List<QuriousItem>.firstMeets(condition: SearchGroup): MeetsInfo? {
    forEachIndexed { index, item ->
        val decreased = item.decreaseIfMeets(condition) ?: return@forEachIndexed
        return MeetsInfo(index, decreased)
    }
    return null
}

private fun QuriousItem.decreaseIfMeets(condition: SearchGroup): QuriousItem? {
    val metItem = condition.items.firstOrNull { meets(it) } ?: return null
    val decreasedCount = count - metItem.count
    return copy(count = decreasedCount)
}

private fun QuriousItem.meets(condition: QuriousItem): Boolean {
    return name.contains(condition.name) && count >= condition.count
}