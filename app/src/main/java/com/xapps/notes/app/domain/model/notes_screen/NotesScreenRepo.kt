package com.xapps.notes.app.domain.model.notes_screen

import com.xapps.notes.app.data.notes_screen.local.Note
import com.xapps.notes.app.data.notes_screen.local.NoteBook
import kotlinx.coroutines.flow.Flow

//
//class NotesScreenRepo @Inject constructor(
//    private val noteDao: NoteDao,
//    private val noteBookDao: NotebookDao
//) {
//    suspend fun addNote(note: Note) = noteDao.upsertNote(note)
//    suspend fun replaceAllNotes(newNoteList: () -> List<Note>) {
//        noteDao.upsertNotes(newNoteList())
//    }
//    suspend fun deleteNotesByNoteIds(noteIds: List<String>) = noteDao.deleteNotesByIds(noteIds)
//    suspend fun deleteNotesByNoteBookIds(noteBookIds: List<String>) = noteDao.deleteNotesByNotebookIds(noteBookIds)
//    fun getAllNotes(): Flow<List<Note>> = noteDao.getAllNotes()
//    suspend fun deleteAllNotes() = noteDao.deleteAllNotes()
//    suspend fun deleteLockedNotes() = noteDao.deleteLockedNotes()
//    suspend fun deleteAllNotesByNotebookId(noteBookId: String = "-1") = noteDao.deleteNotesByNotebookId(noteBookId)
//
//
//
//    suspend fun addNoteBook(noteBook: NoteBook) = noteBookDao.upsertNotebook(noteBook)
//    suspend fun replaceAllNoteBooks(newNoteBookList: () -> List<NoteBook>) {
//        noteBookDao.upsertNotebooks(newNoteBookList())
//    }
//    suspend fun deleteNoteBooks(noteBookIds: List<String>) = noteBookDao.deleteNoteBooksByIds(noteBookIds)
//    fun getAllNotebooks(): Flow<List<NoteBook>> = noteBookDao.getAllNoteBooks()
//}

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