package com.dron.notes.domain

import kotlinx.coroutines.flow.Flow

class GetNoteUseCase(
    private val repository: NotesRepository
) {
    suspend operator fun invoke(noteId: Int): Note {
        return repository.getNote(noteId)
    }
}