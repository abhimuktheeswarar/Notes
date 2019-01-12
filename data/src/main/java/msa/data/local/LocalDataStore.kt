package msa.data.local

import io.reactivex.Completable
import io.reactivex.Observable
import msa.data.local.room.NoteMapper
import msa.data.local.room.NotesDatabase
import msa.domain.entities.Note

/**
 * Created by Abhi Muktheeswarar.
 */

class LocalDataStore(private val notesDatabase: NotesDatabase) {

    fun getNotes(): Observable<List<Note>> {
        return notesDatabase.noteDao().getNotes().map { notes -> notes.map { NoteMapper.transform(it) } }
    }

    fun insertNote(note: Note): Completable {
        return Completable.fromAction { notesDatabase.noteDao().insertNote(NoteMapper.transform(note)) }

    }

    fun updateNote(note: Note): Completable {
        return Completable.fromAction { notesDatabase.noteDao().updateNote(NoteMapper.transform(note)) }

    }

    fun deleteNote(note: Note): Completable {
        return Completable.fromAction { notesDatabase.noteDao().deleteNote(NoteMapper.transform(note)) }
    }

}