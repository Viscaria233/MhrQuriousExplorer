package com.haochen.mhrquriousexplorer

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.io.readLine

class SearchQuriousVm : ViewModel() {
    private var currentFile: Path? = null

    private val _allQurious = MutableStateFlow<List<QuriousResult>>(emptyList())
    val allQurious = _allQurious.asStateFlow()

    private val _results = MutableStateFlow<List<QuriousResult>>(emptyList())
    val results = _results.asStateFlow()

    fun search(file: Path, conditions: List<SearchGroup>) {
        val logMsg = StringBuilder("search\n  file=$file\n  conditions $conditions (input)\n")
        if (currentFile != file) {
            try {
                loadQurious(file)
                currentFile = file
                logMsg.append("  loadQurious size=${_allQurious.value.size}\n")
            } catch (e: Exception) {
                logMsg.append("  loadQurious error. ${e.logMsg}\n")
            }
        }
        val filteredConditions = conditions.asSequence()
                .map { condition -> condition.copy(items = condition.items.filter { it.name.isNotEmpty() }) }
                .filter { it.items.isNotEmpty() }
                .toList()
        logMsg.append("  conditions $filteredConditions (filtered)\n")
        _results.value = search(filteredConditions, logMsg).also {
            logMsg.append("  search result size=${it.size}\n")
        }
        println(logMsg.toString())
    }

    private fun search(conditions: List<SearchGroup>, logMsg: StringBuilder): List<QuriousResult> {
        if (conditions.isEmpty()) {
            return _allQurious.value
        }
        val allCombinations = conditions.allCombinations().also { logMsg.append("  allCombinations=$it\n") }
        return _allQurious.value.filter { result ->
            allCombinations.any { combination ->
                combination.all { result.meets(it) }
            }
        }
    }

    private fun QuriousResult.meets(condition: SearchItem): Boolean {
        val nameMatchedItems = overview.filter { it.name.contains(condition.name) }
        return if (nameMatchedItems.isEmpty()) {
            when (condition.comparator) {
                SearchItem.Comparator.GreaterEquals -> 0 >= condition.count
                SearchItem.Comparator.LessEquals -> 0 <= condition.count
            }
        } else {
            nameMatchedItems.any { nameMatchedItem ->
                when (condition.comparator) {
                    SearchItem.Comparator.GreaterEquals -> nameMatchedItem.count >= condition.count
                    SearchItem.Comparator.LessEquals -> nameMatchedItem.count <= condition.count
                }
            }
        }
    }

    private fun loadQurious(file: Path) {
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
        _allQurious.value = qurious.map { QuriousResult(seq = it.key, items = it.value) }
    }
}

private fun <T> List<Set<T>>.cartesianProduct(): Set<List<T>> {
    return fold(listOf(listOf<T>())) { acc, set ->
        acc.flatMap { list ->
            set.map { i -> list + i }
        }
    }.toSet()
}

private fun List<SearchGroup>.allCombinations(): Set<List<SearchItem>> {
    val comparator = compareBy(SearchItem::name, SearchItem::count)
    return map { group ->
        group.items.mapTo(mutableSetOf()) { it.copy(id = 0) }
    }.cartesianProduct().mapTo(mutableSetOf()) {
        it.sortedWith(comparator)
    }.map { searchItems ->
        searchItems.groupBy { it.name }.mapNotNull { (name, items) ->
            val comparators = items.mapTo(mutableSetOf()) { it.comparator }
            if (comparators.size > 1) {
                null
            } else {
                SearchItem(id = 0, name = name, count = items.sumOf { it.count }, comparator = comparators.first())
            }
        }.sortedWith(comparator)
    }.filterTo(mutableSetOf()) { it.isNotEmpty() }
}