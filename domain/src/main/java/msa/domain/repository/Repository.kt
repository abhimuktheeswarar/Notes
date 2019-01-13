package msa.domain.repository

import io.reactivex.Completable
import io.reactivex.Observable
import msa.domain.entities.Note

/**
 * Created by Abhi Muktheeswarar.
 */

interface Repository {

    fun getNotes(): Observable<List<Note>>

    fun insertNote(note: Note): Completable

    fun updateNote(note: Note): Completable

    fun deleteNote(note: Note): Completable

    fun getNote(id: Int): Observable<Note>
}