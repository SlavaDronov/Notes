package com.dron.notes.domain

import androidx.room.Entity
import androidx.room.PrimaryKey


data class Note(
    val id: Int,
    val title: String,
    val content: List<ContentItem>,
    val updateAt: Long,
    val isPinned: Boolean
)
