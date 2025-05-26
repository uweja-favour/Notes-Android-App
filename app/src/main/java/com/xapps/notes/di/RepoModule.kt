package com.xapps.notes.di

import android.content.Context

//@Module
//@InstallIn(SingletonComponent::class)
//object RepoModule {
//
//    @Provides
//    @Singleton
//    fun provideNotesScreenRepo(
//        @ApplicationContext context: Context
//    ): NotesScreenRepo {
//        return NotesScreenRepoImpl(
//            context = context
//        )
//    }
//
//    @Provides
//    @Singleton
//    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
//        Room.databaseBuilder(context, AppDatabase::class.java, appDatabaseName)
//            .fallbackToDestructiveMigration()
//            .build()
//
//
//    @Provides
//    @Singleton
//    fun providesNoteDao(appDatabase: AppDatabase) = appDatabase.getNoteDao()
//
//    @Provides
//    @Singleton
//    fun providesNoteBookDao(appDatabase: AppDatabase) = appDatabase.getNoteBookDao()
//}