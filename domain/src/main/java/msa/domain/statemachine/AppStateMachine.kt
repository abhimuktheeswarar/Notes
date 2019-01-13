package msa.domain.statemachine

import com.freeletics.rxredux.reduxStore
import com.jakewharton.rxrelay2.PublishRelay
import com.jakewharton.rxrelay2.Relay
import io.reactivex.Observable
import msa.domain.core.Action
import msa.domain.core.BaseStateMachine
import msa.domain.core.State
import msa.domain.entities.Note
import msa.domain.usecases.*
import java.util.*

/**
 * Created by Abhi Muktheeswarar.
 */

sealed class NoteAction : Action {

    object GetNotesAction : NoteAction()

    data class SortOrderNotesAction(val sortBy: SortBy = SortBy.NA, val orderBy: OrderBy = OrderBy.NA) : NoteAction()

    object LoadingNotesAction : NoteAction()

    //object LoadingNoteAction : NoteAction()

    data class NotesLoadedAction(val notes: List<Note>) : NoteAction()

    data class NoteLoadedAction(val note: Note) : NoteAction()

    object ShowNotesSortOptionsAction : NoteAction()

    object HideNotesSortOptionsAction : NoteAction()

    data class InsertNoteAction(val title: String, val body: String, val date: Date, val imagePath: String?) :
        NoteAction()

    data class UpdateNoteAction(
        val id: Int,
        val title: String,
        val body: String,
        val date: Date,
        val imagePath: String?
    ) : NoteAction()

    data class DeleteNoteAction(
        val id: Int,
        val title: String,
        val body: String,
        val date: Date,
        val imagePath: String? = null
    ) : NoteAction()

    data class ViewNoteDetailAction(val id: Int) : NoteAction()

    object OpenEditNoteAction : NoteAction()

    object CloseEditNoteAction : NoteAction()

    object CloseNoteDetailAction : NoteAction()

    object HidingNotesSortOptionsAction : NoteAction()

    data class SaveEditNoteProgressAction(val editNote: Note) : NoteAction()
}

enum class SortBy {

    NA, ASC, DESC

}

enum class OrderBy {

    NA, DATE, TITLE
}

data class NoteState(
    val loading: Boolean = false,
    val notes: List<Note>? = null,
    val sortBy: SortBy = SortBy.NA,
    val orderBy: OrderBy = OrderBy.NA,
    val exception: Exception? = null,
    val showSortOption: Boolean = false,
    val noteId: Int? = null,
    val note: Note? = null,
    val editingNote: Note? = null
) : State

class AppStateMachine(
    getNotes: GetNotes,
    insertNote: InsertNote,
    updateNote: UpdateNote,
    deleteNote: DeleteNote,
    getNote: GetNote
) :
    BaseStateMachine<NoteState> {

    override val input: Relay<Action> = PublishRelay.create()

    override val state: Observable<NoteState> = input
        .doOnNext { println("Input Action ${it.javaClass.simpleName}") }
        .reduxStore(
            initialState = NoteState(),
            sideEffects = listOf(
                getNotes::getNotesSideEffect,
                getNotes::sortOrderNotesSideEffect,
                insertNote::insertNoteSideEffect,
                updateNote::updateNoteSideEffect,
                deleteNote::deleteNoteSideEffect,
                getNote::getNoteSideEffect
            ),
            reducer = ::reducer
        )
        .distinctUntilChanged()
        .doOnNext { println("RxStore state ${it.javaClass.simpleName}") }

    override fun reducer(state: NoteState, action: Action): NoteState {

        return when (action) {

            is NoteAction.GetNotesAction -> state.copy(
                showSortOption = false,
                noteId = null,
                note = null,
                editingNote = null
            )

            is NoteAction.SortOrderNotesAction -> state.copy(
                loading = true,
                sortBy = action.sortBy,
                orderBy = action.orderBy,
                showSortOption = false
            )

            is NoteAction.LoadingNotesAction -> state.copy(loading = true, showSortOption = false)

            is NoteAction.NotesLoadedAction -> state.copy(loading = false, notes = action.notes, showSortOption = false)

            is NoteAction.ShowNotesSortOptionsAction -> state.copy(showSortOption = true)

            is NoteAction.HideNotesSortOptionsAction, is NoteAction.HidingNotesSortOptionsAction -> state.copy(
                showSortOption = false
            )

            is NoteAction.InsertNoteAction -> state.copy(loading = false)

            is NoteAction.ViewNoteDetailAction -> state.copy(noteId = action.id, note = null, editingNote = null)

            is NoteAction.NoteLoadedAction -> state.copy(loading = false, note = action.note)

            is NoteAction.OpenEditNoteAction -> state.copy(editingNote = state.note)

            is NoteAction.CloseEditNoteAction -> state.copy(editingNote = null)

            is NoteAction.SaveEditNoteProgressAction -> state.copy(editingNote = action.editNote)

            //is NoteAction.CloseNoteDetailAction -> state.copy(noteId = null, note = null)

            else -> state
        }
    }
}



