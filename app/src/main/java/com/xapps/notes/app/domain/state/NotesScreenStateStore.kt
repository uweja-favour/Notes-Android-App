package com.xapps.notes.app.domain.state

import com.xapps.notes.app.data.notes_screen.local.Note
import com.xapps.notes.app.data.notes_screen.local.NoteBook
import com.xapps.notes.app.presentation.util.Constants.ALL_NOTES
import com.xapps.notes.app.presentation.util.Constants.DEFAULT_NOTE_BOOK_NAME
import com.xapps.notes.app.presentation.util.Constants.RECENTLY_DELETED_NOTEBOOK_NAME
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update


const val DEFAULT_NOTEBOOK_ID = "0"
const val RECENTLY_DELETED_NOTEBOOK_ID = "1"
const val ALL_NOTEBOOK_ID = "100"

val defaultNoteBook = NoteBook(
    noteBookTitle = DEFAULT_NOTE_BOOK_NAME,
    noteBookId = DEFAULT_NOTEBOOK_ID
)

val recentlyDeletedNoteBook = NoteBook(
   noteBookTitle = RECENTLY_DELETED_NOTEBOOK_NAME,
   noteBookId = RECENTLY_DELETED_NOTEBOOK_ID
)

val allNoteBook = NoteBook(
    noteBookTitle = ALL_NOTES,
    noteBookId = ALL_NOTEBOOK_ID
)

data class NotesScreenState(
    val notesScreenEditMode: Boolean = false,
    val noteBooks: List<NoteBook> = emptyList(),
    val notes: List<Note> = emptyList(),
    val currentNoteBook: NoteBook = allNoteBook,
    val isLoading: Boolean = false
)

object NotesScreenStateStore {
    private val _state: MutableStateFlow<NotesScreenState> = MutableStateFlow(NotesScreenState())
    val state: StateFlow<NotesScreenState> = _state.asStateFlow()

    fun update(transform: (NotesScreenState) -> NotesScreenState) {
        _state.update(transform)
    }

    fun updateIfChanged(transform: (NotesScreenState) -> NotesScreenState?) {
        val current = _state.value
        val newState = transform(current)
        if (newState != null && newState != current) {
            _state.value = newState
        }
    }
}