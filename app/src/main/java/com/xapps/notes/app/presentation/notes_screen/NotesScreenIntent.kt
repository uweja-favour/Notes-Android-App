package com.xapps.notes.app.presentation.notes_screen

import androidx.compose.ui.graphics.Color
import com.xapps.notes.app.domain.state.NoteBook

sealed class NotesScreenIntent {
    data class OnClickNoteBookCard(val noteBook: NoteBook) : NotesScreenIntent()
    data class OnAddNewNoteBook(val noteBookName: String, val noteBookColor: Color) : NotesScreenIntent()
    data class OnEditCheckedNoteBook(val checkedNoteBookId: String, val title: String, val color: Color, ) : NotesScreenIntent()
    data class OnLockCheckedNoteBooks(val checkedNoteBooksIds: Set<String>) : NotesScreenIntent()
    data class OnDeleteCheckedNoteBooks(val checkedNoteBooksIds: Set<String>) : NotesScreenIntent()
}