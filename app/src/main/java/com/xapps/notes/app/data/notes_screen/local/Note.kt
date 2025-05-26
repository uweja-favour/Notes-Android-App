package com.xapps.notes.app.data.notes_screen.local

import androidx.room.Entity
import androidx.room.PrimaryKey

const val NOTE_TABLE = "note_table"

@Entity(tableName = NOTE_TABLE)
data class Note(
    val heading: String = "",
    val content: String = "",
    val dateModified: String = "",
    val timeModified: String = "",
    @PrimaryKey val noteId: String,
    val noteBookId: String = "0", // 0 for Default Notebook
    val noteBookName: String = "",
    val isLocked: Boolean = false
)
