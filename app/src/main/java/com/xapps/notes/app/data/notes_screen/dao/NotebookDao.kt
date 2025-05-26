package com.xapps.notes.app.data.notes_screen.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.xapps.notes.app.data.notes_screen.local.NoteBook
import kotlinx.coroutines.flow.Flow

@Dao
interface NotebookDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertNotebook(noteBook: NoteBook)

    @Upsert
    suspend fun upsertNotebooks(noteBooks: List<NoteBook>)

    @Query("DELETE FROM note_book_table")
    suspend fun deleteAllNotebooks()

    @Query("DELETE FROM note_book_table WHERE noteBookId IN (:noteBookIds)")
    suspend fun deleteNoteBooksByIds(noteBookIds: List<String>)

    @Query("SELECT * FROM note_book_table")
    fun getAllNoteBooks(): Flow<List<NoteBook>>
}