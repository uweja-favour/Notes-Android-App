package com.xapps.notes.di

import com.xapps.notes.app.data.notes_screen.NotesScreenRepoImpl
import com.xapps.notes.app.domain.model.notes_screen.NotesScreenRepo
import com.xapps.notes.app.presentation.note_books_screen.NoteBookScreenVM
import com.xapps.notes.app.presentation.note_view_screen.NoteViewVM
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
    viewModel { NoteViewVM(get()) }
    viewModel { NoteBookScreenVM(get()) }

    single<NotesScreenRepo> { NotesScreenRepoImpl(get()) }
}
