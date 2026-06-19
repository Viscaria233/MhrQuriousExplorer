package com.haochen.mhrquriousexplorer

import kotlin.concurrent.atomics.AtomicInt
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.concurrent.atomics.incrementAndFetch

@OptIn(ExperimentalAtomicApi::class)
private val idGenerator = AtomicInt(0)

@OptIn(ExperimentalAtomicApi::class)
data class SearchGroup(
    val id: Int = idGenerator.incrementAndFetch(),
    val items: List<SearchItem> = emptyList()
) {
    override fun toString(): String {
        return "$id@$items"
    }
}

@OptIn(ExperimentalAtomicApi::class)
data class SearchItem(
    val id: Int = idGenerator.incrementAndFetch(),
    val name: String = "",
    val count: Int = 0,
    val comparator: Comparator = Comparator.GreaterEquals,
) {
    enum class Comparator(val signature: String) {
        GreaterEquals(">=") {
            override fun Int.meetsComparingWith(target: Int): Boolean {
                return this >= target
            }
        },
        LessEquals("<=") {
            override fun Int.meetsComparingWith(target: Int): Boolean {
                return this <= target
            }
        },
        Equals("=") {
            override fun Int.meetsComparingWith(target: Int): Boolean {
                return this == target
            }
        },
        ;

        abstract infix fun Int.meetsComparingWith(target: Int): Boolean

        companion object {
            val order = listOf(
                GreaterEquals,
                LessEquals,
                Equals,
            )
        }
    }

    override fun toString(): String {
        return "[$name ${comparator.signature} $count]"
    }
}

@OptIn(ExperimentalAtomicApi::class)
data class QuriousItem(
    val id: Int = idGenerator.incrementAndFetch(),
    val name: String = "",
    val count: Int = 0,
) {
    override fun toString(): String {
        return "[$name: $count]"
    }
}

@OptIn(ExperimentalAtomicApi::class)
data class QuriousResult(
    val seq: Int,
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