package com.xapps.notes.di

import android.content.Context
import com.xapps.notes.app.data.notes_screen.NotesScreenRepoImpl
import com.xapps.notes.app.domain.model.notes_screen.NotesScreenRepo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepoModule {

    @Provides
    @Singleton
    fun provideNotesScreenRepo(
        @ApplicationContext context: Context
    ): NotesScreenRepo {
        return NotesScreenRepoImpl(
            context = context
        )
    }
}