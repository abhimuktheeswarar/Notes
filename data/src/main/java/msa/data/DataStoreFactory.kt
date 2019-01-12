package msa.data

import android.content.Context
import androidx.room.Room
import msa.data.local.LocalDataStore
import msa.data.local.room.NotesDatabase

/**
 * Created by Abhi Muktheeswarar.
 */

class DataStoreFactory(applicationContext: Context) {

    val localDataStore: LocalDataStore

    init {
        val notesDatabase = Room.databaseBuilder(
            applicationContext,
            NotesDatabase::class.java, "notes"
        ).build()

        localDataStore = LocalDataStore(notesDatabase)
    }
}