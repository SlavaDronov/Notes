//// di/AppComponent.kt
//package com.dron.notes.di
//
//import android.content.Context
//import com.dron.notes.data.NotesDatabase
//import com.dron.notes.data.NotesRepositoryImpl
//import com.dron.notes.domain.*
//
//class AppComponent(private val context: Context) {
//
//    // 1. Database
//    //private val database: NotesDatabase by lazy {
//        //NotesDatabase.getInstance(context)
//    }
//
//    // 2. Dao
//    private val notesDao by lazy {
//        database.notesDao()
//    }
//
//    // 3. Repository
//    private val repository: NotesRepositoryImpl by lazy {
//        NotesRepositoryImpl(notesDao)
//    }
//
//    // 4. UseCases
//    val addNoteUseCase: AddNoteUseCase by lazy {
//        AddNoteUseCase(repository)
//    }
//
//    val deleteNoteUseCase: DeleteNoteUseCase by lazy {
//        DeleteNoteUseCase(repository)
//    }
//
//    val editNoteUseCase: EditNoteUseCase by lazy {
//        EditNoteUseCase(repository)
//    }
//
//    val getAllNotesUseCase: GetAllNotesUseCase by lazy {
//        GetAllNotesUseCase(repository)
//    }
//
//    val getNoteUseCase: GetNoteUseCase by lazy {
//        GetNoteUseCase(repository)
//    }
//
//    val searchNotesUseCase: SearchNotesUseCase by lazy {
//        SearchNotesUseCase(repository)
//    }
//
//    val switchPinnedStatusUseCase: SwitchPinnedStatusUseCase by lazy {
//        SwitchPinnedStatusUseCase(repository)
//    }
//}