package msa.domain.usecases

import com.freeletics.rxredux.StateAccessor
import io.reactivex.Observable
import io.reactivex.Scheduler
import msa.domain.core.Action
import msa.domain.core.State
import msa.domain.entities.Note
import msa.domain.interactor.UseCase
import msa.domain.repository.Repository
import msa.domain.statemachine.NoteAction
import msa.domain.statemachine.NoteState
import msa.domain.statemachine.OrderBy
import msa.domain.statemachine.SortBy

/**
 * Created by Abhi Muktheeswarar.
 */

class GetNotes(
    private val repository: Repository,
    threadExecutor: Scheduler,
    postExecutionScheduler: Scheduler
) : UseCase(threadExecutor, postExecutionScheduler) {

    override fun buildUseCaseObservable(action: Action, state: State): Observable<Action> {
        return repository.getNotes().map {

            val noteState = state as NoteState

            //println("sortyBy = ${noteState.sortBy}, orderBy = ${noteState.orderBy}")

            NoteAction.NotesLoadedAction(reArrange(it, noteState.sortBy, noteState.orderBy))
        }
    }

    fun getNotesSideEffect(actions: Observable<Action>, state: StateAccessor<State>): Observable<Action> =
        actions.ofType(NoteAction.GetNotesAction::class.java)
            .filter { (state() as NoteState).notes == null }
            .doOnNext { println("Got SortOrderNotesAction") }
            .switchMap { execute(it, state()).startWith(NoteAction.LoadingNotesAction) }

    fun sortOrderNotesSideEffect(actions: Observable<Action>, state: StateAccessor<State>): Observable<Action> =
        actions.ofType(NoteAction.SortOrderNotesAction::class.java)
            .doOnNext { println("Got SortOrderNotesAction") }
            .switchMap { execute(it, state()) }

    private fun reArrange(notes: List<Note>, sortBy: SortBy, orderBy: OrderBy): List<Note> {

        return when (orderBy) {

            OrderBy.NA -> notes.sortedBy { it.id }

            OrderBy.DATE -> {

                when (sortBy) {

                    SortBy.NA -> notes
                    SortBy.ASC -> notes.sortedBy { it.date }
                    SortBy.DESC -> notes.sortedByDescending { it.date }
                }
            }
            OrderBy.TITLE -> when (sortBy) {

                SortBy.NA -> notes
                SortBy.ASC -> notes.sortedBy { it.title }
                SortBy.DESC -> notes.sortedByDescending { it.title }
            }
        }
    }


}