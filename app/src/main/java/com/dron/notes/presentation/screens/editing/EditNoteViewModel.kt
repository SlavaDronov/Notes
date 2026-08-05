package com.dron.notes.presentation.screens.editing

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dron.notes.data.NotesRepositoryImpl
//import com.dron.notes.data.TestNotesRepositoryImpl
import com.dron.notes.domain.AddNoteUseCase
import com.dron.notes.domain.ContentItem
import com.dron.notes.domain.DeleteNoteUseCase
import com.dron.notes.domain.EditNoteUseCase
import com.dron.notes.domain.GetNoteUseCase
import com.dron.notes.domain.Note
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = EditNoteViewModel.Factory::class)
class EditNoteViewModel @AssistedInject constructor(
    //private val noteId: Int,
    private val getNoteUseCase: GetNoteUseCase,
    private val editNoteUseCase: EditNoteUseCase,
    private val deleteNoteUseCase: DeleteNoteUseCase,
    @Assisted("noteId") private val noteId: Int
): ViewModel() {

    private val _state = MutableStateFlow<EditNoteState>(EditNoteState.Initial)
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            _state.update {
                val note = getNoteUseCase(noteId)
                EditNoteState.Editing(note)
            }
        }
    }

    fun processCommand(command: EditNoteCommand) {
        when(command) {
            EditNoteCommand.Back -> {
                _state.update { EditNoteState.Finished }
            }
            is EditNoteCommand.InputContent -> {
                _state.update {previousState ->
                    if (previousState is EditNoteState.Editing) {
                        val newContent = ContentItem.Text(content = command.content)
                        val newNote = previousState.note.copy(content = listOf(newContent))
                        previousState.copy(note = newNote)

                    } else {
                        previousState
                    }
                }
            }
            is EditNoteCommand.InputTitle -> {
                _state.update {previousState ->
                    if (previousState is EditNoteState.Editing) {
                        val newNote = previousState.note.copy(title = command.title)
                        previousState.copy(note = newNote)

                    } else {
                        previousState
                    }
                }
            }
            EditNoteCommand.Save -> {
                viewModelScope.launch {
                    _state.update { previousState ->
                        if (previousState is EditNoteState.Editing) {
                            val note = previousState.note
                            editNoteUseCase(note)
                            EditNoteState.Finished
                        } else {
                            previousState
                        }
                    }
                }

            }
            EditNoteCommand.Delete -> {
                viewModelScope.launch {
                    _state.update { previousState ->
                        if (previousState is EditNoteState.Editing) {
                            val note = previousState.note
                            deleteNoteUseCase(note.id)
                            EditNoteState.Finished
                        } else {
                            previousState
                        }
                    }
                }
            }
        }
    }
    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("noteId") noteId: Int
        ): EditNoteViewModel
    }
}

sealed interface EditNoteCommand {
    data class InputTitle(val title: String): EditNoteCommand
    data class InputContent(val content: String): EditNoteCommand
    data object Save: EditNoteCommand
    data object Back: EditNoteCommand
    data object Delete: EditNoteCommand
}

sealed interface EditNoteState {

    data object Initial: EditNoteState

    data class Editing(
        val note: Note
    ): EditNoteState {
        val isSaveEnabled: Boolean
            get() {
                return when {
                    note.title.isBlank() -> false
                    note.content.isEmpty() -> false
                    else -> {
                        note.content.any{
                            it !is ContentItem.Text || it.content.isNotBlank()
                        }
                    }
                }
            }
    }

    data object Finished: EditNoteState
}