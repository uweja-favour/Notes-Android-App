package com.xapps.notes.app.domain.state

import androidx.compose.ui.graphics.Color
import com.xapps.notes.app.presentation.util.Constants.ALL_NOTES
import com.xapps.notes.app.presentation.util.Constants.DEFAULT_NOTE_BOOK
import com.xapps.notes.app.presentation.util.Constants.RECENTLY_DELETED
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

val defaultNoteBook = NoteBook(
    title = DEFAULT_NOTE_BOOK,
    noteBookId = "0"
)

val recentlyDeletedNoteBook = NoteBook(
   title = RECENTLY_DELETED,
   noteBookId = "1"
)

val allNoteBook = NoteBook(
    title = ALL_NOTES,
    noteBookId = "100"
)

data class NotesScreenState(
    val noteBooks: List<NoteBook> = emptyList(),
    val notes: List<Note> = emptyList(),
    val currentNoteBook: NoteBook = allNoteBook,
    val isLoading: Boolean = true
)

data class NoteBook(
    val noteBookId: String = "0",
    val title: String = "",
    val color: Color = Color.Blue
)

data class Note(
    val heading: String = "",
    val content: String = "",
    val dateModified: String = "",
    val timeModified: String = "",
    val noteId: String,
    val noteBookId: String = "0", // 0 for Default Notebook
    val noteBookName: String = "",
    val isLocked: Boolean = false
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