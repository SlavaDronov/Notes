package com.dron.notes.domain

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetNoteUseCase @Inject constructor(
    private val repository: NotesRepository
) {
    suspend operator fun invoke(noteId: Int): Note {
        return repository.getNote(noteId)
    }
}