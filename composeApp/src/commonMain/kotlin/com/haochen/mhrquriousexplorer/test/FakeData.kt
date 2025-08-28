package com.haochen.mhrquriousexplorer.test

import com.haochen.mhrquriousexplorer.QuriousItem
import com.haochen.mhrquriousexplorer.QuriousResult
import com.haochen.mhrquriousexplorer.SearchGroup
import com.haochen.mhrquriousexplorer.SearchItem
import kotlinx.io.files.Path

object FakeData {
    val files: List<Path> = (1..20).map { Path("file_$it") }
    val groups: List<SearchGroup> = (1..3).map { groupIndex ->
        SearchGroup(
            items = (1..groupIndex).map {
                SearchItem(
                    name = "item_$it",
                    count = it,
                    comparator = if (it % 2 == 0) {
                        SearchItem.Comparator.GreaterEquals
                    } else {
                        SearchItem.Comparator.LessEquals
                    },
                )
            }
        )
    }
    val results: List<QuriousResult> = (1..50).map { seq ->
        QuriousResult(
            seq = seq,
            items = (1..(seq % 5)).map {
                QuriousItem(name = "item_$it", count = it)
            }
        )
    }
    val allQurious: List<QuriousResult> = (1..100).map { seq ->
        QuriousResult(
            seq = seq,
            items = (1..(seq % 5)).map {
                QuriousItem(name = "item_$it", count = it)
            }
        )
    }
}