package msa.data

import io.reactivex.Completable
import io.reactivex.Observable
import msa.domain.entities.Note
import msa.domain.repository.Repository

/**
 * Created by Abhi Muktheeswarar.
 */

class NotesRepository(private val dataStoreFactory: DataStoreFactory) : Repository {

    override fun getNotes(): Observable<List<Note>> {
        return dataStoreFactory.localDataStore.getNotes()
    }

    override fun insertNote(note: Note): Completable {
        return dataStoreFactory.localDataStore.insertNote(note)
    }

    override fun updateNote(note: Note): Completable {
        return dataStoreFactory.localDataStore.updateNote(note)
    }

    override fun deleteNote(note: Note): Completable {
        return dataStoreFactory.localDataStore.deleteNote(note)
    }

    override fun getNote(id: Int): Observable<Note> {
        return dataStoreFactory.localDataStore.getNote(id)
    }
}