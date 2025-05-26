package com.xapps.notes.di

import com.xapps.notes.app.data.notes_screen.NotesScreenRepoImpl
import com.xapps.notes.app.domain.model.notes_screen.NotesScreenRepo
import com.xapps.notes.app.presentation.add_note_screen.AddNoteScreen
import com.xapps.notes.app.presentation.add_note_screen.AddNoteScreenVM
import com.xapps.notes.app.presentation.note_books_screen.NoteBookScreenVM
import com.xapps.notes.app.presentation.note_view_screen.NoteViewScreenVM
import com.xapps.notes.app.presentation.notes_screen.SharedViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

//@Module
//@InstallIn(SingletonComponent::class)
//object AppModule {
//
//    @Singleton
//    @Provides
//    fun provideApplication(@ApplicationContext context: Context) = context as NoteApplication
//}

val appModule = module {
    viewModel { SharedViewModel(get()) }
    viewModel { NoteBookScreenVM(get()) }

    viewModel { (noteId: String) ->
        NoteViewScreenVM(get(), noteId)
    }

    viewModel { (currentNotebookId: String) ->
        AddNoteScreenVM(get(), currentNotebookId)
    }

    single<NotesScreenRepo> { NotesScreenRepoImpl(get()) }
}
