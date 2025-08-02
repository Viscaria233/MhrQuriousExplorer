package com.haochen.mhrquriousexplorer

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class SearchInputVm : ViewModel() {
    private val _groups = MutableStateFlow<List<SearchGroup>>(emptyList())
    val groups = _groups.asStateFlow()

    fun createNewGroup() {
        _groups.value = _groups.value.toMutableList().apply { add(SearchGroup(items = listOf(QuriousItem()))) }
    }

    fun createNewItem(group: SearchGroup) {
        val currentGroups = _groups.value.toMutableList()
        val index = currentGroups.indexOf(group).takeIf { it >= 0 } ?: return
        currentGroups[index] = group.copy(items = group.items.toMutableList().apply { add(QuriousItem()) })
        _groups.value = currentGroups
    }

    fun removeItem(group: SearchGroup, item: QuriousItem) {
        val currentGroups = _groups.value.toMutableList()
        val index = currentGroups.indexOf(group).takeIf { it >= 0 } ?: return
        val newGroup = group.copy(items = group.items.toMutableList().apply { remove(item) })
        if (newGroup.items.isEmpty()) {
            currentGroups.removeAt(index)
        } else {
            currentGroups[index] = newGroup
        }
        _groups.value = currentGroups
    }

    fun updateItem(group: SearchGroup, oldItem: QuriousItem, newItem: QuriousItem) {
        val currentGroups = _groups.value.toMutableList()
        val index = currentGroups.indexOf(group).takeIf { it >= 0 } ?: return
        currentGroups[index] = group.copy(items = group.items.toMutableList().apply {
            val itemIndex = indexOf(oldItem).takeIf { it >= 0 } ?: return
            this[itemIndex] = newItem
        })
        _groups.value = currentGroups
    }
}