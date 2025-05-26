package com.xapps.notes.app.data.notes_screen

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.xapps.notes.app.Logger
import com.xapps.notes.app.data.notes_screen.local.Note
import com.xapps.notes.app.data.notes_screen.local.NoteBook
import com.xapps.notes.app.domain.model.notes_screen.NotesScreenRepo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

const val NOTES_LIST = "notes_list"
const val NOTE_BOOKS_LIST = "note_books_list"

class NotesScreenRepoImpl(
    private val context: Context
) : NotesScreenRepo {
    private val gson = Gson()
    private val notesPref = context.applicationContext.getSharedPreferences("NOTES_PREFS", Context.MODE_PRIVATE)
    private val noteBooksPref = context.applicationContext.getSharedPreferences("NOTE_BOOKS_PREFS", Context.MODE_PRIVATE)

    private val _notesFlow: MutableStateFlow<List<Note>> = MutableStateFlow(emptyList())
    private val notesFlow: StateFlow<List<Note>> = _notesFlow

    private val _noteBookFlow: MutableStateFlow<List<NoteBook>> = MutableStateFlow(emptyList())
    private val noteBookFlow: StateFlow<List<NoteBook>> = _noteBookFlow

    init {
        CoroutineScope(Dispatchers.IO).launch {
            _notesFlow.value = fetchNotes()
            _noteBookFlow.value = fetchNoteBooks()
        }
    }

    override suspend fun addNote(note: Note): Boolean {
        val allNotes = fetchNotes()
        val existingNoteIndex = allNotes.indexOfFirst { it.noteId == note.noteId }

        Logger.logData("id of new note is: ${note.noteId} id of existingNoteIndex is: $existingNoteIndex")
        Logger.logData("allNotes is: $allNotes")
        if (existingNoteIndex != -1) {
            val existingNote = allNotes[existingNoteIndex]
            // If the note already exists and is exactly the same, do nothing
            if (existingNote.heading == note.heading && existingNote.content == note.content) {
                return false
            }
            // Update the note since content or heading is different
            val updatedNotes = allNotes.toMutableList()
            updatedNotes[existingNoteIndex] = note
            saveNotes(updatedNotes)
            return true
        }

        // Note doesn't exist; add new
        val updatedNotes = allNotes + note
        saveNotes(updatedNotes)
        return true
    }

    override suspend fun replaceAllNotes(newNoteList: () -> List<Note>) = saveNotes(newNoteList())

    override suspend fun deleteNotes(noteIds: List<String>?, noteBookIds: List<String>?) {
        val updatedNotes = fetchNotes().filterNot {
            if (noteIds != null) it.noteId in noteIds
            else noteBookIds?.contains(it.noteBookId) == true
        }
        saveNotes(updatedNotes)
    }

    override suspend fun retrieveNotes(): List<Note> = fetchNotes()

    override fun observeNotes(): Flow<List<Note>> = notesFlow

    override suspend fun deleteAllNotes(cleanDelete: Boolean, deleteLockedNotes: Boolean, noteBookId: String) {
        val allNotes = fetchNotes()
        val filteredNotes = when {
            cleanDelete -> allNotes.filter { it.isLocked }
            deleteLockedNotes -> allNotes.filterNot { it.isLocked }
            noteBookId != "-1" -> allNotes.filterNot { it.noteBookId == noteBookId }
            else -> return // No valid condition, nothing to do
        }
        saveNotes(filteredNotes)
    }

    private suspend fun fetchNotes(): List<Note> = withContext(Dispatchers.IO) {
        val data = notesPref.getString(NOTES_LIST, null)
        Logger.logData("list of notes retrieved is $data")
        return@withContext try {
            val jsonString = data ?: gson.toJson(emptyList<Note>())
            val type = object : TypeToken<List<Note>>() {}.type
            gson.fromJson<List<Note>>(jsonString, type)
        } catch (e: Exception) {
            Logger.logError("An error occurred in fetchNotes: $e")
            emptyList()
        }
    }

    private suspend fun saveNotes(notes: List<Note>) = withContext(Dispatchers.IO) {
        val data = gson.toJson(notes)
        val success = notesPref.edit().putString(NOTES_LIST, data).commit()
        if (!success) Logger.logError("Failed to save notes") else {
            Logger.logData("Saved notes successfully")
            _notesFlow.update { notes }
        }
    }

    /* NOTEBOOKS */

    override suspend fun addNoteBook(noteBook: NoteBook): Boolean {
        val allNoteBooks = fetchNoteBooks()
        val existingNoteBookIndex = allNoteBooks.indexOfFirst { it.noteBookId == noteBook.noteBookId }

        if (existingNoteBookIndex != -1) {
            val existingNoteBook = allNoteBooks[existingNoteBookIndex]
            if (existingNoteBook === noteBook) {
                return false
            }

            val updatedNoteBooks = allNoteBooks.toMutableList()
            updatedNoteBooks[existingNoteBookIndex] = noteBook
            saveNoteBooks(updatedNoteBooks)
            return true
        }

        // New Note book did not exist before now, add new one
        saveNoteBooks(allNoteBooks + noteBook)
        return true
    }

    override suspend fun replaceAllNoteBooks(newNoteBookList: () -> List<NoteBook>) = saveNoteBooks(newNoteBookList())

    override suspend fun deleteNoteBooks(noteBookIds: List<String>) {
        if (noteBookIds.isEmpty()) return

        val idsToDelete = noteBookIds.toSet() // Optimize lookups

        val (noteBooks, notes) = withContext(Dispatchers.IO) {
            val fetchedNoteBooks = fetchNoteBooks()
            val fetchedNotes = fetchNotes()
            Pair(fetchedNoteBooks, fetchedNotes)
        }

        val filteredNoteBooks = noteBooks.filterNot { it.noteBookId in idsToDelete }
        val filteredNotes = notes.filterNot { it.noteBookId in idsToDelete }

        withContext(Dispatchers.IO) {
            saveNoteBooks(filteredNoteBooks)
            saveNotes(filteredNotes)
        }
    }

    override suspend fun retrieveNoteBooks(): List<NoteBook> = fetchNoteBooks()

    override fun observeNoteBooks() = noteBookFlow

    private suspend fun fetchNoteBooks(): List<NoteBook> = withContext(Dispatchers.IO) {
        val data = noteBooksPref.getString(NOTE_BOOKS_LIST, null)
        Logger.logData("List of notebooks retrieved: $data")
        return@withContext try {
            val jsonString = data ?: gson.toJson(emptyList<NoteBook>())
            val type = object : TypeToken<List<NoteBook>>() {}.type
            gson.fromJson<List<NoteBook>>(jsonString, type)
        } catch (e: Exception) {
            Logger.logError("An error occurred in fetchNoteBooks: $e")
            emptyList()
        }
    }

    private suspend fun saveNoteBooks(allNoteBooks: List<NoteBook>) = withContext(Dispatchers.IO) {
        val data = gson.toJson(allNoteBooks)
        val success = noteBooksPref.edit().putString(NOTE_BOOKS_LIST, data).commit()
        if (!success) Logger.logError("Failed to save notebooks") else {
            Logger.logData("Saved notebooks successfully")
            _noteBookFlow.update { allNoteBooks }
        }
    }
}
