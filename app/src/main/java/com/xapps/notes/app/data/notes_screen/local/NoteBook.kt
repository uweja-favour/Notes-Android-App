package com.xapps.notes.app.data.notes_screen.local

import androidx.compose.ui.graphics.Color
import androidx.room.Entity
import androidx.room.PrimaryKey

const val NOTE_BOOK_TABLE = "note_book_table"
@Entity(tableName = NOTE_BOOK_TABLE)
data class NoteBook(
    @PrimaryKey val noteBookId: String = "0",
    val noteBookTitle: String = "",
    val color: Color = Color.Blue
)
