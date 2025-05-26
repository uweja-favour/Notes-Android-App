package com.xapps.notes.app.presentation.note_books_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xapps.notes.app.data.notes_screen.local.Note
import com.xapps.notes.app.data.notes_screen.local.NoteBook
import com.xapps.notes.app.domain.model.notes_screen.NotesScreenRepo
import com.xapps.notes.app.domain.state.ALL_NOTEBOOK_ID
import com.xapps.notes.app.domain.state.DEFAULT_NOTEBOOK_ID
import com.xapps.notes.app.domain.state.RECENTLY_DELETED_NOTEBOOK_ID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class NoteBookScreenState(
    val checkBoxActiveState: Boolean = false,
    val noteList: List<Note> = emptyList(),
    val noteBookList: List<NoteBook> = emptyList()
)

class NoteBookScreenVM(
    private val repo: NotesScreenRepo
) : ViewModel() {
    private val _state: MutableStateFlow<NoteBookScreenState> = MutableStateFlow(NoteBookScreenState())
    val state: StateFlow<NoteBookScreenState> = _state.asStateFlow()

    init {
        observeNotesUpdates()
        observeNoteBookUpdates()
    }

    private fun observeNotesUpdates() {
        viewModelScope.launch(Dispatchers.IO) {
            repo.observeNotes()
                .collectLatest { notes ->
                    _state.update { it.copy(
                        noteList = notes
                    ) }
                }
        }
    }

    private fun observeNoteBookUpdates() {
        viewModelScope.launch(Dispatchers.IO) {
            repo.observeNoteBooks()
                .collectLatest { noteBooks ->
                    _state.update { it.copy(
                        noteBookList = noteBooks.filterNot { noteBook ->
                            noteBook.noteBookId in setOf(
                                DEFAULT_NOTEBOOK_ID,
                                RECENTLY_DELETED_NOTEBOOK_ID,
                                ALL_NOTEBOOK_ID
                            )
                        }
                    ) }
                }
        }
    }

    fun dispatch(intent: NoteBooksScreenIntent): Boolean {
        return when(intent) {
            is NoteBooksScreenIntent.OnToggleCheckboxActiveState -> {
                toggleSelectionMode(enabled = intent.enabled)
                true
            }
        }
    }

    private fun toggleSelectionMode(enabled: Boolean) {
        _state.update { it.copy(
            checkBoxActiveState = enabled
        ) }
    }
}


sealed class NoteBooksScreenIntent {
    data class OnToggleCheckboxActiveState(val enabled: Boolean) : NoteBooksScreenIntent()
}