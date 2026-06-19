package com.haochen.mhrquriousexplorer

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.haochen.mhrquriousexplorer.loader.CharmJsonLoader
import com.haochen.mhrquriousexplorer.loader.FileLoader
import com.haochen.mhrquriousexplorer.loader.QuriousCsvLoader
import com.haochen.mhrquriousexplorer.test.FakeData
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Regular
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.regular.Copy
import compose.icons.fontawesomeicons.solid.ChevronDown
import compose.icons.fontawesomeicons.solid.ChevronUp
import compose.icons.fontawesomeicons.solid.Equals
import compose.icons.fontawesomeicons.solid.GreaterThanEqual
import compose.icons.fontawesomeicons.solid.LessThanEqual
import compose.icons.fontawesomeicons.solid.Minus
import compose.icons.fontawesomeicons.solid.Plus
import kotlinx.coroutines.launch
import kotlinx.io.files.Path
import org.jetbrains.compose.ui.tooling.preview.Preview

private val ROUND_CORNER_SIZE = 8.dp

private enum class Type(
    val text: String,
    val loader: FileLoader,
) {
    Qurious("炼化", QuriousCsvLoader()),
    Charm("护石", CharmJsonLoader()),
}

private val TYPE_ORDER = listOf(
    Type.Qurious,
    Type.Charm,
)

@Composable
fun App(
    scanFilesVm: ScanFilesVm = viewModel { ScanFilesVm() },
    searchInputVm: SearchInputVm = viewModel { SearchInputVm() },
    searchQuriousVm: SearchQuriousVm = viewModel { SearchQuriousVm() },
) {
    var type by remember { mutableStateOf(TYPE_ORDER.first()) }
    val files = scanFilesVm.files.collectAsState()
    val groups = searchInputVm.groups.collectAsState()
    val results = searchQuriousVm.results.collectAsState()
    val allQurious = searchQuriousVm.allQurious.collectAsState()
    val selectedFileIndex = remember { mutableStateOf(0) }
    MainScreen(
        modifier = Modifier,
        type = type,
        files = files.value,
        groups = groups.value,
        results = results.value,
        totalCount = allQurious.value.size,
        selectedState = selectedFileIndex,
        onRefreshClick = {
            scanFilesVm.refreshFiles()
        },
        onTypeChange = {
            type = it
        },
        onAddGroupClick = {
            searchInputVm.createNewGroup()
        },
        onAddItemClick = { group ->
            searchInputVm.createNewItem(group)
        },
        onRemoveItemClick = { group, item ->
            searchInputVm.removeItem(group, item)
        },
        onItemUpdate = { group, oldItem, newItem ->
            searchInputVm.updateItem(group, oldItem, newItem)
        },
        onSearchClick = {
            files.value.getOrNull(selectedFileIndex.value)?.let { file ->
                searchQuriousVm.search(
                    file = file,
                    loader = type.loader,
                    conditions = groups.value,
                )
            }
        },
    )
}

@Composable
@Preview
private fun MainScreen(
    modifier: Modifier = Modifier,
    files: List<Path> = emptyList(),
    type: Type = Type.Qurious,
    groups: List<SearchGroup> = emptyList(),
    results: List<QuriousResult> = emptyList(),
    totalCount: Int = 0,
    selectedState: MutableState<Int> = remember { mutableStateOf(0) },
    onRefreshClick: () -> Unit = {},
    onTypeChange: (Type) -> Unit = {},
    onAddGroupClick: () -> Unit = {},
    onAddItemClick: (group: SearchGroup) -> Unit = {},
    onRemoveItemClick: (group: SearchGroup, item: SearchItem) -> Unit = { _, _ -> },
    onItemUpdate: (group: SearchGroup, oldItem: SearchItem, newItem: SearchItem) -> Unit = { _, _, _ -> },
    onSearchClick: () -> Unit = {},
) {
    MaterialTheme(
        typography = myTypography(),
    ) {
        MhrQuriousExplorer(
            modifier = modifier,
            files = files.ifPreview { FakeData.files },
            type = type,
            groups = groups.ifPreview { FakeData.groups },
            results = results.ifPreview { FakeData.results },
            totalCount = totalCount.ifPreview { FakeData.allQurious.size },
            selectedState = selectedState,
            onRefreshClick = onRefreshClick,
            onTypeChange = onTypeChange,
            onAddGroupClick = onAddGroupClick,
            onAddItemClick = onAddItemClick,
            onRemoveItemClick = onRemoveItemClick,
            onItemUpdate = onItemUpdate,
            onSearchClick = onSearchClick,
        )
    }
}

@Composable
private fun MhrQuriousExplorer(
    modifier: Modifier = Modifier,
    files: List<Path>,
    type: Type,
    groups: List<SearchGroup>,
    results: List<QuriousResult>,
    totalCount: Int,
    selectedState: MutableState<Int>,
    onRefreshClick: () -> Unit,
    onTypeChange: (Type) -> Unit,
    onAddGroupClick: () -> Unit,
    onAddItemClick: (group: SearchGroup) -> Unit,
    onRemoveItemClick: (group: SearchGroup, item: SearchItem) -> Unit,
    onItemUpdate: (group: SearchGroup, oldItem: SearchItem, newItem: SearchItem) -> Unit,
    onSearchClick: () -> Unit,
) {
    Column(
        modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        SearchResult(
            modifier = Modifier
                    .fillMaxWidth()
                    .weight(4f),
            results = results,
            totalCount = totalCount,
        )
        Row(
            modifier = Modifier
                    .fillMaxWidth()
                    .weight(3f)
                    .padding(start = 12.dp, end = 12.dp, bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            FileList(
                modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                        .clip(RoundedCornerShape(ROUND_CORNER_SIZE)),
                files = files.map { it.name },
                selectedState = selectedState,
                onRefreshClick = onRefreshClick,
            )
            SearchBox(
                modifier = Modifier
                        .fillMaxHeight()
                        .weight(2f)
                        .clip(RoundedCornerShape(ROUND_CORNER_SIZE)),
                type = type,
                groups = groups,
                onTypeChange = onTypeChange,
                onAddGroupClick = onAddGroupClick,
                onAddItemClick = onAddItemClick,
                onRemoveItemClick = onRemoveItemClick,
                onItemUpdate = onItemUpdate,
                onSearchClick = onSearchClick,
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
    type: Type,
    groups: List<SearchGroup>,
    onTypeChange: (Type) -> Unit,
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
        Row(
            modifier = Modifier
                    .fillMaxWidth(),
        ) {
            TypeSelector(
                modifier = Modifier
                        .clip(RoundedCornerShape(topStart = ROUND_CORNER_SIZE, bottomEnd = ROUND_CORNER_SIZE))
                        .border(
                            width = 2.dp,
                            color = MaterialTheme.colorScheme.outlineVariant,
                            shape = RoundedCornerShape(topStart = ROUND_CORNER_SIZE, bottomEnd = ROUND_CORNER_SIZE)
                        )
                        .background(MaterialTheme.colorScheme.tertiaryContainer),
                type = type,
                onTypeChange = onTypeChange,
            )
            Spacer(Modifier.weight(1f))
            Image(
                modifier = Modifier
                        .size(30.dp)
                        .clip(RoundedCornerShape(bottomStart = ROUND_CORNER_SIZE))
                        .background(MaterialTheme.colorScheme.tertiaryContainer)
                        .clickable {
                            onAddGroupClick()
                        }
                        .padding(8.dp),
                imageVector = FontAwesomeIcons.Solid.Plus,
                contentDescription = null,
            )
        }
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
        Image(
            modifier = Modifier
                    .padding(start = 12.dp)
                    .size(30.dp)
                    .clip(RoundedCornerShape(bottomStart = ROUND_CORNER_SIZE))
                    .background(MaterialTheme.colorScheme.tertiaryContainer)
                    .align(Alignment.Top)
                    .clickable {
                        onAddItemClick(group)
                    }
                    .padding(8.dp),
            imageVector = FontAwesomeIcons.Solid.Plus,
            contentDescription = null,
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
        verticalAlignment = Alignment.CenterVertically,
    ) {
        var precision by remember { mutableStateOf(false) }
        val lineColor = MaterialTheme.colorScheme.outlineVariant
        Text(
            modifier = Modifier
                    .padding(start = 2.dp)
                    .height(20.dp)
                    .clip(RoundedCornerShape(100))
                    .then(
                        if (precision) Modifier else Modifier.drawWithCache {
                            onDrawWithContent {
                                drawContent()
                                drawLine(
                                    color = lineColor,
                                    start = Offset(0f, 0f),
                                    end = Offset(size.width, size.height),
                                    strokeWidth = 6.dp.value,
                                )
                            }
                        }
                    )
                    .background(MaterialTheme.colorScheme.tertiaryContainer)
                    .then(if (precision) Modifier else Modifier.alpha(0.2f))
                    .clickable {
                        precision = !precision
                        onItemUpdate(item, item.copy(precision = precision))
                    }
                    .padding(horizontal = 8.dp),
            text = "精确",
            textAlign = TextAlign.Center,
            style = TextStyle.Default.copy(fontFamily = LocalTextStyle.current.fontFamily),
            maxLines = 1,
        )
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
        var comparatorIndex by remember { mutableStateOf(0) }
        IconButton(
            modifier = Modifier
                    .size(20.dp),
            onClick = {
                comparatorIndex = (comparatorIndex + 1) % SearchItem.Comparator.order.size
                onItemUpdate(item, item.copy(comparator = SearchItem.Comparator.order[comparatorIndex]))
            },
        ) {
            Icon(
                modifier = Modifier
                        .size(20.dp)
                        .clip(RoundedCornerShape(100))
                        .align(Alignment.CenterVertically)
                        .background(MaterialTheme.colorScheme.tertiaryContainer)
                        .padding(all = 5.dp),
                imageVector = when (SearchItem.Comparator.order[comparatorIndex]) {
                    SearchItem.Comparator.GreaterEquals -> FontAwesomeIcons.Solid.GreaterThanEqual
                    SearchItem.Comparator.LessEquals -> FontAwesomeIcons.Solid.LessThanEqual
                    SearchItem.Comparator.Equals -> FontAwesomeIcons.Solid.Equals
                },
                contentDescription = "",
                tint = MaterialTheme.colorScheme.onTertiaryContainer,
            )
        }
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
        Image(
            modifier = Modifier
                    .padding(end = 2.dp)
                    .size(20.dp)
                    .clip(RoundedCornerShape(100))
                    .background(MaterialTheme.colorScheme.tertiaryContainer)
                    .clickable {
                        onRemoveItemClick(item)
                    }
                    .padding(6.dp),
            imageVector = FontAwesomeIcons.Solid.Minus,
            contentDescription = null,
        )
    }
}

@Composable
private fun SearchResult(
    modifier: Modifier = Modifier,
    results: List<QuriousResult>,
    totalCount: Int,
) {
    Column(
        modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.primaryContainer)
    ) {
        Row(
            modifier = Modifier
                    .fillMaxWidth(),
        ) {
            Text(
                modifier = Modifier
                        .padding(start = 12.dp, top = 8.dp),
                text = "已显示 ${results.size} / $totalCount",
                fontWeight = FontWeight.Normal,
            )
            Spacer(Modifier.weight(1f))
            Text(
                modifier = Modifier
                        .padding(top = 4.dp, end = 4.dp),
                text = BuildConfig.APP_VERSION,
                fontWeight = FontWeight.Light,
                fontSize = 10.sp,
                style = TextStyle.Default.copy(fontFamily = myFontFamily()),
            )
        }
        QuriousResultList(
            modifier = Modifier,
            results = results,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TypeSelector(
    modifier: Modifier = Modifier,
    type: Type,
    onTypeChange: (Type) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        modifier = modifier,
        expanded = expanded,
        onExpandedChange = {
            // 用 clickable 修改 expanded，这里不处理
        },
    ) {
        Row(
            modifier = Modifier
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable, false)
                    .clickable {
                        expanded = !expanded
                    }
                    .padding(horizontal = 16.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = type.text,
                color = MaterialTheme.colorScheme.onTertiaryContainer,
                fontWeight = FontWeight.Bold,
            )
            Image(
                modifier = Modifier
                        .padding(start = 4.dp)
                        .size(12.dp),
                imageVector = if (expanded) FontAwesomeIcons.Solid.ChevronUp else FontAwesomeIcons.Solid.ChevronDown,
                contentDescription = null,
            )
        }
        ExposedDropdownMenu(
            modifier = Modifier,
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            TYPE_ORDER.forEach {
                DropdownMenuItem(
                    text = { Text(it.text) },
                    onClick = {
                        onTypeChange(it)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun QuriousResultList(
    modifier: Modifier = Modifier,
    results: List<QuriousResult>,
) {
    val lazyListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    LazyRow (
        modifier = modifier
                .fillMaxSize()
                .lazyRowDragWithInertia(
                    state = lazyListState,
                )
                .onScrollWheel { deltaX, deltaY ->
                    coroutineScope.launch {
                        lazyListState.scrollBy(deltaY * 30)
                    }
                },
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(12.dp),
        state = lazyListState,
    ) {
        items(
            items = results,
            key = { it.seq },
        ) {
            QuriousResultCard(
                modifier = Modifier,
                result = it,
            )
        }
    }
    LaunchedEffect(results) {
        lazyListState.scrollToItem(0)
    }
}

@Composable
private fun QuriousResultCard(
    modifier: Modifier = Modifier,
    result: QuriousResult,
) {
    Column(
        modifier = modifier
                .widthIn(min = 100.dp)
                .width(IntrinsicSize.Max)
                .wrapContentHeight()
                .clip(RoundedCornerShape(ROUND_CORNER_SIZE))
                .background(MaterialTheme.colorScheme.inversePrimary),
        horizontalAlignment = Alignment.Start,
    ) {
        Row(
            modifier = Modifier
                    .fillMaxWidth(),
        ) {
            Text(
                modifier = Modifier
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                text = "# ${result.seq}",
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.weight(1f))
            val clipboard = LocalClipboard.current
            val coroutineScope = rememberCoroutineScope()
            Image(
                modifier = Modifier
                        .size(30.dp)
                        .clip(RoundedCornerShape(bottomStart = ROUND_CORNER_SIZE))
                        .background(MaterialTheme.colorScheme.tertiaryContainer)
                        .clickable {
                            coroutineScope.launch {
                                clipboard.setContent(result.text)
                            }
                        }
                        .padding(8.dp),
                imageVector = FontAwesomeIcons.Regular.Copy,
                contentDescription = null,
            )
        }
        result.overview.forEach { item ->
            Text(
                modifier = Modifier
                        .padding(horizontal = 24.dp)
                        .padding(bottom = 12.dp)
                        .align(Alignment.Start),
                text = "${item.name}: ${item.count}",
                fontWeight = FontWeight.Normal,
            )
        }
    }
}