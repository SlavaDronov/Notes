package com.dron.notes.presentation.screens.creation

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dron.notes.data.NotesRepositoryImpl
//import com.dron.notes.data.TestNotesRepositoryImpl
import com.dron.notes.domain.AddNoteUseCase
import com.dron.notes.domain.ContentItem
import com.dron.notes.domain.ContentItem.*
import com.dron.notes.presentation.screens.creation.CreateNoteState.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateNoteViewModel @Inject constructor(
    private val addNoteUseCase: AddNoteUseCase  // ← Приходит из Component
): ViewModel() {

    private val _state = MutableStateFlow<CreateNoteState>(CreateNoteState.Creation())
    val state = _state.asStateFlow()

    fun processCommand(command: CreateNoteCommand) {
        when(command) {
            CreateNoteCommand.Back -> {
                _state.update { Finished }
            }
            is CreateNoteCommand.InputContent -> {
                _state.update {previousState ->
                    if (previousState is Creation) {
                        val newContent = previousState.content
                            .mapIndexed { index, contentItem ->
                                if (index == command.index && contentItem is Text) {
                                    contentItem.copy(content = command.content)
                                } else {
                                    contentItem
                                }
                            }
                        previousState.copy(
                        content = newContent
                        )
                    } else {
                        previousState
                    }
                }
            }
            is CreateNoteCommand.InputTitle -> {
                _state.update {previousState ->
                    if (previousState is Creation) {
                        previousState.copy(
                            title = command.title
                        )
                    } else {
                        previousState
                    }
                }
            }

            CreateNoteCommand.Save -> {
                viewModelScope.launch {
                    _state.update { previousState ->
                        if (previousState is Creation) {
                            val title = previousState.title
                            val content = previousState.content.filter {
                                it !is Text || it.content.isNotBlank()
                            }
                            addNoteUseCase(title, content)
                            Finished
                        } else {
                            previousState
                        }
                    }
                }

            }

            is CreateNoteCommand.AddImage -> {
                _state.update {previousState ->
                    if (previousState is Creation) {
                        previousState.content.toMutableList().apply {
                            val lastItem = last()
                            if (lastItem is Text && lastItem.content.isBlank()) {
                                removeAt(lastIndex)
                            }
                            add(Image(command.uri.toString()))
                            add(Text(""))
                        }.let {
                            previousState.copy(content = it)
                        }


                    } else {
                        previousState
                    }
                }
            }

            is CreateNoteCommand.DeleteImage -> {
                _state.update {previousState ->
                    if (previousState is Creation) {
                        previousState.content.toMutableList().apply {
                            removeAt(command.index)
                        }.let {
                            previousState.copy(content = it)
                        }


                    } else {
                        previousState
                    }
                }
            }
        }
    }
}

sealed interface CreateNoteCommand {
    data class InputTitle(val title: String): CreateNoteCommand
    data class InputContent(val content: String, val index: Int): CreateNoteCommand
    data class AddImage(val uri: Uri): CreateNoteCommand

    data class DeleteImage(val index: Int): CreateNoteCommand

    data object Save: CreateNoteCommand
    data object Back: CreateNoteCommand
}

sealed interface CreateNoteState {
    data class Creation(
        val title: String = "",
        val content: List<ContentItem> = listOf(ContentItem.Text(""))
    ): CreateNoteState {
        val isSaveEnabled: Boolean
            get() {
                return when {
                    title.isBlank() -> false
                    content.isEmpty() -> false
                    else -> {
                        content.any{
                            it !is ContentItem.Text || it.content.isNotBlank()
                        }
                    }
                }
            }
    }

    data object Finished: CreateNoteState
}