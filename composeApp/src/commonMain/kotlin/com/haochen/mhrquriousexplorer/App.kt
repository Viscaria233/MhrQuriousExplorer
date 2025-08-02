package com.haochen.mhrquriousexplorer

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    MaterialTheme(
        typography = myTypography(),
    ) {
        MhrQuriousExplorer()
    }
}

@Composable
private fun MhrQuriousExplorer(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SearchResult(
            modifier = Modifier
                    .fillMaxWidth()
                    .weight(2f)
        )
        Row(
            modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(start = 12.dp, end = 12.dp, bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val files = FakeData.files
            val selectedFileIndex = remember { mutableStateOf(0) }
            FileList(
                modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp)),
                files = withPreview(files) { FakeData.files },
                selectedState = selectedFileIndex,
                onRefreshClick = {
                    println("refresh")
                },
            )
            val groups = remember { mutableStateOf(FakeData.groups) }
            SearchBox(
                modifier = Modifier
                        .fillMaxHeight()
                        .weight(2f)
                        .clip(RoundedCornerShape(8.dp)),
                groups = withPreview(groups.value) { FakeData.groups },
                onAddGroupClick = {
                    groups.value = groups.value.toMutableList().apply { add(SearchGroup()) }
                },
                onAddItemClick = block@{ group ->
                    val currentGroups = groups.value.toMutableList()
                    val index = currentGroups.indexOf(group).takeIf { it >= 0 } ?: return@block
                    currentGroups[index] = group.copy(items = group.items.toMutableList().apply { add(SearchItem()) })
                    groups.value = currentGroups
                },
                onRemoveItemClick = block@{ group, item ->
                    val currentGroups = groups.value.toMutableList()
                    val index = currentGroups.indexOf(group).takeIf { it >= 0 } ?: return@block
                    val newGroup = group.copy(items = group.items.toMutableList().apply { remove(item) })
                    if (newGroup.items.isEmpty()) {
                        currentGroups.removeAt(index)
                    } else {
                        currentGroups[index] = newGroup
                    }
                    groups.value = currentGroups
                },
                onItemUpdate = block@{ group: SearchGroup, oldItem: SearchItem, newItem: SearchItem ->
                    val currentGroups = groups.value.toMutableList()
                    val index = currentGroups.indexOf(group).takeIf { it >= 0 } ?: return@block
                    currentGroups[index] = group.copy(items = group.items.toMutableList().apply {
                        val itemIndex = indexOf(oldItem).takeIf { it >= 0 } ?: return@block
                        this[itemIndex] = newItem
                    })
                    groups.value = currentGroups
                },
                onSearchClick = {
                    println("search\n  file=${files.getOrNull(selectedFileIndex.value)}\n  ${groups.value}")
                }
            )
        }
    }
}

@Composable
private fun FileList(
    modifier: Modifier = Modifier,
    files: List<String>,
    selectedState: MutableState<Int>,
    onRefreshClick: () -> Unit,
) {
    Column(
        modifier = modifier
                .background(MaterialTheme.colorScheme.primaryContainer)
    ) {
        LazyColumn(
            modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
        ) {
            itemsIndexed(items = files) { index, file ->
                Text(
                    modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = if (index == selectedState.value) {
                                    MaterialTheme.colorScheme.inversePrimary
                                } else {
                                    Color.Transparent
                                }
                            )
                            .clickable {
                                selectedState.value = index
                            }
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                    text = file,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Start,
                )
            }
        }

        Text(
            modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.tertiaryContainer)
                    .clickable {
                        onRefreshClick()
                    }
                    .padding(vertical = 8.dp),
            text = "刷新",
            color = MaterialTheme.colorScheme.onTertiaryContainer,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun SearchBox(
    modifier: Modifier = Modifier,
    groups: List<SearchGroup>,
    onAddGroupClick: () -> Unit,
    onAddItemClick: (group: SearchGroup) -> Unit,
    onRemoveItemClick: (group: SearchGroup, item: SearchItem) -> Unit,
    onItemUpdate: (group: SearchGroup, oldItem: SearchItem, newItem: SearchItem) -> Unit,
    onSearchClick: () -> Unit,
) {
    Column(
        modifier = modifier
                .background(MaterialTheme.colorScheme.primaryContainer)
    ) {
        TextButton(
            modifier = Modifier
                    .size(30.dp)
                    .clip(RoundedCornerShape(bottomStart = 8.dp))
                    .background(MaterialTheme.colorScheme.tertiaryContainer)
                    .align(Alignment.End)
                    .clickable {
                        onAddGroupClick()
                    },
            style = TextStyle.Default,
            text = "+",
        )
        SearchGroupList(
            modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
                    .weight(1f),
            groups = groups,
            onAddItemClick = { group ->
                onAddItemClick(group)
            },
            onRemoveItemClick = { group, item ->
                onRemoveItemClick(group, item)
            },
            onItemUpdate = { groups, oldItem, newItem ->
                onItemUpdate(groups, oldItem, newItem)
            }
        )
        Text(
            modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.tertiaryContainer)
                    .clickable {
                        onSearchClick()
                    }
                    .padding(vertical = 8.dp),
            text = "搜索",
            color = MaterialTheme.colorScheme.onTertiaryContainer,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun SearchGroupList(
    modifier: Modifier = Modifier,
    groups: List<SearchGroup>,
    onAddItemClick: (group: SearchGroup) -> Unit,
    onRemoveItemClick: (group: SearchGroup, item: SearchItem) -> Unit,
    onItemUpdate: (group: SearchGroup, oldItem: SearchItem, newItem: SearchItem) -> Unit,
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(
            items = groups,
            key = { it.id },
        ) { group ->
            SearchGroup(
                modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.inversePrimary),
                group = group,
                onAddItemClick = { group ->
                    onAddItemClick(group)
                },
                onRemoveItemClick = { group, item ->
                    onRemoveItemClick(group, item)
                },
                onItemUpdate = { oldItem, newItem ->
                    onItemUpdate(group, oldItem, newItem)
                }
            )
        }
    }
}

@Composable
private fun SearchGroup(
    modifier: Modifier = Modifier,
    group: SearchGroup,
    onAddItemClick: (group: SearchGroup) -> Unit,
    onRemoveItemClick: (group: SearchGroup, item: SearchItem) -> Unit,
    onItemUpdate: (oldItem: SearchItem, newItem: SearchItem) -> Unit,
) {
    Row(
        modifier = modifier,
    ) {
        FlowRow(
            modifier = Modifier
                    .wrapContentHeight()
                    .weight(1f)
                    .align(Alignment.CenterVertically),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
            itemVerticalAlignment = Alignment.CenterVertically,
        ) {
            group.items.forEach { item ->
                key(item.id) {
                    SearchItemEditor(
                        modifier = Modifier
                                .wrapContentSize()
                                .border(
                                    width = 1.dp,
                                    color = MaterialTheme.colorScheme.outline,
                                    shape = RoundedCornerShape(100)
                                )
                                .align(Alignment.CenterVertically),
                        item = item,
                        onRemoveItemClick = { item ->
                            onRemoveItemClick(group, item)
                        },
                        onItemUpdate = { oldItem, newItem ->
                            onItemUpdate(oldItem, newItem)
                        }
                    )
                }
            }
        }
        TextButton(
            modifier = Modifier
                    .padding(start = 12.dp)
                    .size(30.dp)
                    .clip(RoundedCornerShape(bottomStart = 8.dp))
                    .background(MaterialTheme.colorScheme.tertiaryContainer)
                    .align(Alignment.Top)
                    .clickable {
                        onAddItemClick(group)
                    },
            style = TextStyle.Default,
            text = "+",
        )
    }
}

@Composable
private fun SearchItemEditor(
    modifier: Modifier = Modifier,
    item: SearchItem,
    onRemoveItemClick: (item: SearchItem) -> Unit,
    onItemUpdate: (oldItem: SearchItem, newItem: SearchItem) -> Unit,
) {
    Row(
        modifier = modifier,
    ) {
        val nameState = rememberTextFieldState(item.name)
        BasicTextField(
            modifier = Modifier
                    .wrapContentHeight()
                    .weight(3f)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                    .onFocusChanged {
                        if (it.isFocused) {
                            return@onFocusChanged
                        }
                        onItemUpdate(item, item.copy(name = nameState.text.toString()))
                    },
            textStyle = TextStyle.Default.copy(fontFamily = LocalTextStyle.current.fontFamily),
            lineLimits = TextFieldLineLimits.SingleLine,
            state = nameState,
        )
        val countState = rememberTextFieldState(item.count.toString())
        BasicTextField(
            modifier = Modifier
                    .wrapContentHeight()
                    .weight(1f)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                    .onFocusChanged {
                        if (it.isFocused) {
                            return@onFocusChanged
                        }
                        val count = countState.text.toString().toIntOrNull() ?: run {
                            countState.edit {
                                replace(0, length, item.count.toString())
                            }
                            return@onFocusChanged
                        }
                        countState.edit {
                            replace(0, length, count.toString())
                        }
                        onItemUpdate(item, item.copy(count = count))
                    },
            textStyle = TextStyle.Default.copy(fontFamily = LocalTextStyle.current.fontFamily),
            lineLimits = TextFieldLineLimits.SingleLine,
            state = countState,
        )
        TextButton(
            modifier = Modifier
                    .padding(end = 2.dp)
                    .size(20.dp)
                    .clip(RoundedCornerShape(100))
                    .background(MaterialTheme.colorScheme.tertiaryContainer)
                    .align(Alignment.CenterVertically)
                    .clickable {
                        onRemoveItemClick(item)
                    },
            style = TextStyle.Default,
            text = "-",
        )
    }
}

@Composable
private fun SearchResult(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
                .background(MaterialTheme.colorScheme.primaryContainer)
    ) {

    }
}

private object FakeData {
    val files: List<String> = (1..20).map { "file_$it" }
    val groups: List<SearchGroup> = (1..3).map { groupIndex ->
        SearchGroup(
            items = (1..groupIndex).map { SearchItem(name = "item_$it", count = it) }
        )
    }
}