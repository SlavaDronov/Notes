package com.dron.notes.domain

class EditNoteUseCase(
    private val repository: NotesRepository
) {
    suspend operator fun invoke(note: Note) {
        repository.editNote(
            note.copy(
                updateAt = System.currentTimeMillis()
            )
        )
    }
}