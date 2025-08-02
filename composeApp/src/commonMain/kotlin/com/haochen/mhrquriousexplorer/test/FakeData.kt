package com.haochen.mhrquriousexplorer.test

import com.haochen.mhrquriousexplorer.QuriousItem
import com.haochen.mhrquriousexplorer.QuriousResult
import com.haochen.mhrquriousexplorer.SearchGroup
import kotlinx.io.files.Path

object FakeData {
    val files: List<Path> = (1..20).map { Path("file_$it") }
    val groups: List<SearchGroup> = (1..3).map { groupIndex ->
        SearchGroup(
            items = (1..groupIndex).map { QuriousItem(name = "item_$it", count = it) }
        )
    }
    val results: List<QuriousResult> = (1..50).map { seq ->
        QuriousResult(
            seq = seq,
            conditions = emptyList(),
            items = (1..(seq % 5)).map {
                QuriousItem(name = "item_$it", count = it)
            }
        )
    }
    val allQurious: List<QuriousResult> = (1..100).map { seq ->
        QuriousResult(
            seq = seq,
            conditions = emptyList(),
            items = (1..(seq % 5)).map {
                QuriousItem(name = "item_$it", count = it)
            }
        )
    }
}