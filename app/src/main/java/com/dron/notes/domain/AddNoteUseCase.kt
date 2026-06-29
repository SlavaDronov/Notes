package com.dron.notes.domain

class AddNoteUseCase (
    private val repository: NotesRepository
){
    suspend operator fun invoke(
        title: String,
        content: String
    ) {
      repository.addNote(
          title,
          content,
          isPinned = false,
          updateAt = System.currentTimeMillis()
      )
    }
}