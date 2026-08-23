package com.dron.notes.data

import android.content.Context
import com.dron.notes.domain.ContentItem
import com.dron.notes.domain.Note
import com.dron.notes.domain.NotesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotesRepositoryImpl @Inject constructor(
    private val notesDao: NotesDao,
    private val imageFileManager: ImageFileManager
): NotesRepository {

    override suspend fun addNote(
        title: String,
        content: List<ContentItem>,
        isPinned: Boolean,
        updateAt: Long
    ) {
        val processedContent = content.processForStorage()
        val noteDbModel = NoteDbModel(0, title, updateAt, isPinned)
        notesDao.addNoteWithContent(noteDbModel, processedContent)
    }

    override suspend fun deleteNote(noteId: Int) {
        val note = notesDao.getNote(noteId).toEntity()
        notesDao.deleteNote(noteId)

        note.content
            .filterIsInstance<ContentItem.Image>()
            .map { it.url }
            .forEach {
            imageFileManager.deleteImage(it)
        }
    }

    override suspend fun editNote(note: Note) {
        val oldNote = notesDao.getNote(note.id ).toEntity()
        val oldUrls = oldNote.content.filterIsInstance<ContentItem.Image>().map { it.url }
        val newUrls = note.content.filterIsInstance<ContentItem.Image>().map { it.url }
        val removeUrls = oldUrls - newUrls
        removeUrls.forEach {
            imageFileManager.deleteImage(it)
        }
        val processContent = note.content.processForStorage()
        val processNote = note.copy(content = processContent)
        // ✅ ТРАНЗАКЦИЯ для редактирования
        notesDao.editNoteWithContent(
            processNote.toDbModel(),
            processContent.toContentItemDbModels(note.id)
        )
    }

    override fun getAllNotes(): Flow<List<Note>> {
        return notesDao.getAllNotes().map {
            it.toEntities()
        }
    }

    override suspend fun getNote(noteId: Int): Note {
        return notesDao.getNote(noteId).toEntity()
    }

    override fun searchNotes(query: String): Flow<List<Note>> {
        return notesDao.searchNotes(query).map { it.toEntities() }
    }

    override suspend fun switchPinnedStatus(noteId: Int) {
        notesDao.switchPinnedStatus(noteId)
    }

    private suspend fun List<ContentItem>.processForStorage(): List<ContentItem> {
        return map { contentItem ->
            when(contentItem) {
                is ContentItem.Image -> {
                    if (imageFileManager.isInternal(contentItem.url)) {
                        contentItem
                    } else {
                        val internalPath = imageFileManager.copyImageToInternalStorage(contentItem.url)
                        ContentItem.Image(internalPath)
                    }
                }
                is ContentItem.Text -> contentItem
            }
        }
    }
}