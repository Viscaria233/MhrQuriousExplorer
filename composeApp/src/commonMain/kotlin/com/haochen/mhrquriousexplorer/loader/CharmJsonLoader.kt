package com.haochen.mhrquriousexplorer.loader

import com.haochen.mhrquriousexplorer.QuriousItem
import com.haochen.mhrquriousexplorer.QuriousResult
import com.haochen.mhrquriousexplorer.SKILL_ID_MAP
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNamingStrategy
import kotlinx.serialization.json.io.decodeFromSource

/**
 * Created by noahwu on 2026/6/19.
 * Copyright (c) Tencent. All rights reserved.
 */
class CharmJsonLoader : FileLoader {
    @OptIn(ExperimentalSerializationApi::class)
    override fun load(file: Path): List<QuriousResult> {
        return SystemFileSystem.source(file).buffered().use {
            json.decodeFromSource<CharmLoadResult>(it).asSequence()
                    .flatten()
                    .mapIndexed { index, charm ->
                        QuriousResult(
                            seq = index + 1,
                            items = buildList {
                                if (charm.skill1 > 0 && charm.skill1Level > 0) {
                                    add(QuriousItem(name = charm.skill1Name, count = charm.skill1Level))
                                }
                                if (charm.skill2 > 0 && charm.skill2Level > 0) {
                                    add(QuriousItem(name = charm.skill2Name, count = charm.skill2Level))
                                }
                                add(QuriousItem(name = "Slot", count = charm.slots))
                            },
                        )
                    }
                    .toList()
        }
    }
}

private typealias CharmLoadResult = List<List<Charm>>

@OptIn(ExperimentalSerializationApi::class)
private val json = Json {
    namingStrategy = JsonNamingStrategy.SnakeCase
}

@Serializable
data class Charm(
    val skill1: Int = 0,
    val skill1Level: Int = 0,
    val skill2: Int = 0,
    val skill2Level: Int = 0,
    val slot1: Int = 0,
    val slot2: Int = 0,
    val slot3: Int = 0,
) {
    val skill1Name: String get() = SKILL_ID_MAP[skill1] ?: skill1.toString()
    val skill2Name: String get() = SKILL_ID_MAP[skill2] ?: skill2.toString()
    val slots: Int get() = listOf(slot1, slot2, slot3).sortedDescending().let {
        it[0] * 100 + it[1] * 10 + it[2]
    }
}