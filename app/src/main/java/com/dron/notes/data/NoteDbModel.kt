package com.dron.notes.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("Notes")
data class NoteDbModel(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val title: String,
    val updateAt: Long,
    val isPinned: Boolean
)
