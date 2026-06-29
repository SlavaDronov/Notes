package com.dron.notes.domain

import kotlinx.coroutines.flow.Flow

class GetAllNotesCaseUse(
    private val repository: NotesRepository
) {
    operator fun invoke(): Flow<List<Note>> {
       return repository.getAllNotes()
    }
}