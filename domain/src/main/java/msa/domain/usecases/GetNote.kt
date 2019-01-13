package msa.domain.usecases

import com.freeletics.rxredux.StateAccessor
import io.reactivex.Observable
import io.reactivex.Scheduler
import msa.domain.core.Action
import msa.domain.core.State
import msa.domain.interactor.UseCase
import msa.domain.repository.Repository
import msa.domain.statemachine.NoteAction

/**
 * Created by Abhi Muktheeswarar.
 */

class GetNote(
    private val repository: Repository,
    threadExecutor: Scheduler,
    postExecutionScheduler: Scheduler
) : UseCase(threadExecutor, postExecutionScheduler) {

    override fun buildUseCaseObservable(action: Action, state: State): Observable<Action> {
        val id = (action as NoteAction.ViewNoteDetailAction).id
        return repository.getNote(id).map {

            NoteAction.NoteLoadedAction(it)
        }
    }

    fun getNoteSideEffect(actions: Observable<Action>, state: StateAccessor<State>): Observable<Action> =
        actions.ofType(NoteAction.ViewNoteDetailAction::class.java)
            .switchMap { execute(it, state()) }


}