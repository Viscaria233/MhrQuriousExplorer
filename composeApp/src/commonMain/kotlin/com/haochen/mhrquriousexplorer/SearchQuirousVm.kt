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
                .map { condition -> condition.copy( items = condition.items.filter { it.name.isNotEmpty() }) }
                .filter { it.items.isNotEmpty() }
                .toList()
        logMsg.append("  conditions $filteredConditions (filtered)\n")
        _results.value = search(filteredConditions).also {
            logMsg.append("  search result size=${it.size}\n")
        }
        println(logMsg.toString())
    }

    private fun search(conditions: List<SearchGroup>): List<QuriousResult> {
        if (conditions.isEmpty()) {
            return _allQurious.value
        }
        return _allQurious.value.filter { it.meets(conditions) }
    }

    private fun QuriousResult.meets(conditions: List<SearchGroup>): Boolean {
        val overview = overview.toMutableList()
        for (condition in conditions) {
            var consumed: Pair<Int, QuriousItem>? = null
            var omittedConsumed: QuriousItem? = null
            for (conditionItem in condition.items) {
                if (consumed != null) {
                    break
                }
                var nameMatchedIndex: Int? = null
                for (index in overview.indices) {
                    val item = overview[index]
                    if (item.name.contains(conditionItem.name)) {
                        nameMatchedIndex = index
                        if (item.count >= conditionItem.count) {
                            consumed = index to item.copy(count = item.count - conditionItem.count)
                            break
                        }
                    }
                }
                if (nameMatchedIndex == null) {
                    if (conditionItem.count <= 0) {
                        omittedConsumed = QuriousItem(name = conditionItem.name, count = -conditionItem.count)
                    }
                }
            }
            if (consumed != null) {
                overview[consumed.first] = consumed.second
            } else if (omittedConsumed != null) {
                overview.add(omittedConsumed)
            } else {
                return false
            }
        }
        return true
    }

//    private fun QuriousResult.meets(conditions: List<SearchGroup>): Boolean {
//        val overview = overview.toMutableList()
//        return conditions.all { condition ->
//            val firstMeetsInfo = overview.firstMeets(condition) ?: return@all false
//            overview[firstMeetsInfo.index] = firstMeetsInfo.consumedItem
//            true
//        }
//    }

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
        _allQurious.value = qurious.map { QuriousResult(seq = it.key, conditions = emptyList(), items = it.value) }
    }
}