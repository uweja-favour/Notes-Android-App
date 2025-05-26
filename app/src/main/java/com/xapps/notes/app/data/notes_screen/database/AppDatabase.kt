package com.xapps.notes.app.data.notes_screen.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.xapps.notes.app.data.notes_screen.dao.NoteDao
import com.xapps.notes.app.data.notes_screen.dao.NotebookDao
import com.xapps.notes.app.data.notes_screen.local.Note
import com.xapps.notes.app.data.notes_screen.local.NoteBook

const val appDatabaseName = "app_db"

@Database(entities = [Note::class, NoteBook::class], version = 1, exportSchema = false)
abstract class AppDatabase: RoomDatabase() {
    abstract fun getNoteDao(): NoteDao
    abstract fun getNoteBookDao(): NotebookDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    appDatabaseName
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}