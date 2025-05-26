package com.xapps.notes.app.data.notes_screen.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.xapps.notes.app.data.notes_screen.local.Note
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertNote(note: Note)

    @Upsert
    suspend fun upsertNotes(notes: List<Note>)

    @Query("SELECT * FROM note_table")
    fun getAllNotes(): Flow<List<Note>>

    @Query("DELETE FROM note_table")
    suspend fun deleteAllNotes()

    @Query("DELETE FROM note_table WHERE noteId IN (:noteIds)")
    suspend fun deleteNotesByIds(noteIds: List<String>)

    @Query("DELETE FROM note_table WHERE noteId =:noteId")
    suspend fun deleteNoteById(noteId: String)

    @Query("DELETE FROM note_table WHERE noteBookId =:noteBookId")
    suspend fun deleteNotesByNotebookId(noteBookId: String)

    @Query("DELETE FROM note_table WHERE noteBookId IN (:noteBookIds)")
    suspend fun deleteNotesByNotebookIds(noteBookIds: List<String>)

    @Query("DELETE FROM note_table WHERE isLocked = :locked")
    suspend fun deleteLockedNotes(locked: Boolean = true)

}