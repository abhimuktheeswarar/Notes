package msa.domain.statemachine

import com.freeletics.rxredux.reduxStore
import com.jakewharton.rxrelay2.PublishRelay
import com.jakewharton.rxrelay2.Relay
import io.reactivex.Observable
import msa.domain.core.Action
import msa.domain.core.BaseStateMachine
import msa.domain.core.State
import msa.domain.entities.Note
import msa.domain.usecases.DeleteNote
import msa.domain.usecases.GetNotes
import msa.domain.usecases.InsertNote
import msa.domain.usecases.UpdateNote
import java.util.*

/**
 * Created by Abhi Muktheeswarar.
 */

sealed class NoteAction : Action {

    data class GetNotesAction(val sortBy: SortBy = SortBy.NA, val orderBy: OrderBy = OrderBy.NA) : NoteAction()

    object LoadingNotesAction : NoteAction()

    data class NotesLoadedAction(val notes: List<Note>) : NoteAction()

    data class InsertNoteAction(val title: String, val body: String, val date: Date) : NoteAction()

    data class UpdateNoteAction(val id: Int, val title: String, val body: String, val date: Date) : NoteAction()

    data class DeleteNoteAction(val id: Int, val title: String, val body: String, val date: Date) : NoteAction()

    data class ViewNoteDetailAction(val id: Int) : NoteAction()
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
    val exception: Exception? = null
) : State

class AppStateMachine(getNotes: GetNotes, insertNote: InsertNote, updateNote: UpdateNote, deleteNote: DeleteNote) :
    BaseStateMachine<NoteState> {

    override val input: Relay<Action> = PublishRelay.create()

    override val state: Observable<NoteState> = input
        .doOnNext { println("Input Action ${it.javaClass.simpleName}") }
        .reduxStore(
            initialState = NoteState(),
            sideEffects = listOf(
                getNotes::getNotesSideEffect,
                insertNote::insertNoteSideEffect,
                updateNote::updateNoteSideEffect,
                deleteNote::deleteNoteSideEffect
            ),
            reducer = ::reducer
        )
        .distinctUntilChanged()
        .doOnNext { println("RxStore state ${it.javaClass.simpleName}") }

    override fun reducer(state: NoteState, action: Action): NoteState {

        return when (action) {

            is NoteAction.GetNotesAction -> state.copy(sortBy = action.sortBy, orderBy = action.orderBy)

            is NoteAction.LoadingNotesAction -> state.copy(loading = true)

            is NoteAction.NotesLoadedAction -> state.copy(loading = false, notes = action.notes)

            is NoteAction.InsertNoteAction -> state.copy(loading = false)

            else -> state
        }
    }
}



