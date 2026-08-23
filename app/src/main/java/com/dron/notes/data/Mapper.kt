package com.dron.notes.data

import com.dron.notes.domain.ContentItem
import com.dron.notes.domain.ContentItem.*
import com.dron.notes.domain.Note
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

fun Note.toDbModel(): NoteDbModel {

    return NoteDbModel(id, title, updateAt, isPinned)
}

fun List<ContentItem>.toContentItemDbModels(noteId: Int): List<ContentItemDbModel> {
    return mapIndexed { index, contentItem ->
        when(contentItem) {
            is ContentItem.Image -> {
                ContentItemDbModel(
                    noteId = noteId,
                    contentType = ContentType.IMAGE,
                    content = contentItem.url,
                    order = index
                )
            }
            is ContentItem.Text -> {
                ContentItemDbModel(
                    noteId = noteId,
                    contentType = ContentType.TEXT,
                    content = contentItem.content,
                    order = index
                )
            }
        }

    }
}

fun List<ContentItemDbModel>.toContentItems(): List<ContentItem> {
    return map {
            contentItem ->
        when(contentItem.contentType) {
            ContentType.TEXT -> {
                ContentItem.Text(content = contentItem.content)
            }

            ContentType.IMAGE -> {
                ContentItem.Image(url = contentItem.content)
            }
        }

    }
}

fun NoteWithContentDbModel.toEntity(): Note{
    return Note(
        id = noteDbModel.id,
        title = noteDbModel.title,
        content = content.toContentItems(),
        updateAt = noteDbModel.updateAt,
        isPinned = noteDbModel.isPinned
    )
}

fun List<NoteWithContentDbModel>.toEntities(): List<Note> {
    return this.map {
        it.toEntity()
    }
}