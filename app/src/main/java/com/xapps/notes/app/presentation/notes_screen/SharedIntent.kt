package com.xapps.notes.app.presentation.notes_screen

import androidx.compose.ui.graphics.Color
import com.xapps.notes.app.data.notes_screen.local.NoteBook

sealed class SharedIntent {
    data class OnClickNoteBookCard(val noteBook: NoteBook) : SharedIntent()
    data class OnAddNewNoteBook(val noteBookName: String, val noteBookColor: Color) : SharedIntent()
    data class OnEditCheckedNoteBook(val checkedNoteBookId: String, val title: String, val color: Color, ) : SharedIntent()
    data class OnLockCheckedNoteBooks(val checkedNoteBooksIds: Set<String>) : SharedIntent()
    data class OnDeleteCheckedNoteBooks(val checkedNoteBooksIds: Set<String>) : SharedIntent()
    data class OnToggleNotesScreenEditMode(val editMode: Boolean) : SharedIntent()
    data class OnLockCheckedNotes(val checkedNotesIds: Set<String>) : SharedIntent()
    data class OnDeleteCheckedNotes(val checkedNotesIds: Set<String>) : SharedIntent()
    data class OnDeleteCheckedNotesForever(val checkedNotesIds: Set<String>) : SharedIntent()
    data class OnUnlockLockedNotes(val checkedNotesIds: Set<String>) : SharedIntent()
    data class OnMoveNoteToNotebook(val noteBookId: String, val notesIds: List<String>) : SharedIntent()
}