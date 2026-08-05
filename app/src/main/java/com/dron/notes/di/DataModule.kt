// di/DataModule.kt
package com.dron.notes.di

import android.content.Context
import androidx.room.Room
import com.dron.notes.data.NotesDao
import com.dron.notes.data.NotesDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideNotesDatabase(
        @ApplicationContext context: Context
    ): NotesDatabase {
        //return NotesDatabase.getInstance(context)
        return Room.databaseBuilder(
            context = context,
            klass = NotesDatabase::class.java,
            name = "notes.db"
        ).fallbackToDestructiveMigration(dropAllTables = true).build()
    }

    @Provides
    @Singleton
    fun provideNotesDao(
        database: NotesDatabase
    ): NotesDao {
        return database.notesDao()
    }
}