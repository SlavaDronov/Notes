// di/RepositoryModule.kt
package com.dron.notes.di

import com.dron.notes.data.NotesRepositoryImpl
import com.dron.notes.domain.NotesRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {

    @Binds
    @Singleton
    fun bindNotesRepository(
        impl: NotesRepositoryImpl
    ): NotesRepository
}