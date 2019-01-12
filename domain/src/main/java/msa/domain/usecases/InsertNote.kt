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
import java.util.*

/**
 * Created by Abhi Muktheeswarar.
 */

class InsertNote(
    private val repository: Repository,
    threadExecutor: Scheduler,
    postExecutionScheduler: Scheduler
) : UseCase(threadExecutor, postExecutionScheduler) {

    override fun buildUseCaseObservable(action: Action, state: State): Observable<Action> {
        val receivedAction = action as NoteAction.InsertNoteAction
        val note =
            Note(title = receivedAction.title, body = receivedAction.body, date = Date(System.currentTimeMillis()))
        return repository.insertNote(note).toObservable()
    }

    fun insertNoteSideEffect(actions: Observable<Action>, state: StateAccessor<State>): Observable<Action> =
        actions.ofType(NoteAction.InsertNoteAction::class.java)
            .switchMap { execute(it, state()) }

}