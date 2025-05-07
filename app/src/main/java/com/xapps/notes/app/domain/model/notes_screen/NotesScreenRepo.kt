package com.xapps.notes.app.domain.model.notes_screen

import com.xapps.notes.app.domain.state.Note
import com.xapps.notes.app.domain.state.NoteBook
import kotlinx.coroutines.flow.Flow

interface NotesScreenRepo {
    suspend fun addNote(note: Note): Boolean
    suspend fun replaceAllNotes(newNoteList: () -> List<Note>)
    suspend fun deleteNotes(noteIds: List<String>?, noteBookIds: List<String>?)
    suspend fun retrieveNotes(): List<Note>
    suspend fun deleteAllNotes(cleanDelete: Boolean, deleteLockedNotes: Boolean, noteBookId: String = "-1")
    fun observeNotes(): Flow<List<Note>>

    suspend fun addNoteBook(noteBook: NoteBook): Boolean
    suspend fun replaceAllNoteBooks(newNoteBookList: () -> List<NoteBook>)
    suspend fun deleteNoteBooks(noteBookIds: List<String>)
    suspend fun retrieveNoteBooks(): List<NoteBook>
    fun observeNoteBooks(): Flow<List<NoteBook>>
}