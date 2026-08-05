@file:OptIn(ExperimentalCoroutinesApi::class)

package com.dron.notes.presentation.screens.notes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dron.notes.domain.GetAllNotesUseCase
import com.dron.notes.domain.Note
import com.dron.notes.domain.SearchNotesUseCase
import com.dron.notes.domain.SwitchPinnedStatusUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotesViewModel @Inject constructor(
    private val getAllNotesUseCase: GetAllNotesUseCase,
    private val searchNotesUseCase: SearchNotesUseCase,
    private val switchPinnedStatusUseCase: SwitchPinnedStatusUseCase
): ViewModel() {

    private val query = MutableStateFlow("")
    private val _state = MutableStateFlow(NotesScreenState())
    val state = _state.asStateFlow()

        init {
            query
            .onEach {input ->
                _state.update { it.copy(query = input) }
            }
            .flatMapLatest {input ->
                if (input.isBlank()) {
                    getAllNotesUseCase()
                } else {
                    searchNotesUseCase(input)
                }
            }
            .onEach {notes ->
                val pinnedNotes = notes.filter { it.isPinned }
                val otherNotes = notes.filter { !it.isPinned }
              _state.update { it.copy(pinnedNotes = pinnedNotes, otherNotes = otherNotes) }
            }
            .launchIn(viewModelScope)
    }


    fun processCommand(command: NotesCommand) {
        viewModelScope.launch {
            when (command){
                is NotesCommand.InputSearchQuery -> {
                    query.update { command.query.trim() }
                }
                is NotesCommand.SwitchPinnedStatus -> {
                    switchPinnedStatusUseCase(command.noteId)
                }
            }
        }
    }
}



sealed interface NotesCommand {
    data class InputSearchQuery(val query: String): NotesCommand
    data class SwitchPinnedStatus(val noteId: Int): NotesCommand

}

data class NotesScreenState(
    val query: String = "",
    val pinnedNotes: List<Note> = listOf(),
    val otherNotes: List<Note> = listOf()

)