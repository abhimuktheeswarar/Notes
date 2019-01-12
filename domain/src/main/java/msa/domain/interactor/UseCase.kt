package msa.domain.interactor

import io.reactivex.Observable
import io.reactivex.Scheduler
import msa.domain.core.Action
import msa.domain.core.State

/**
 * Created by Abhi Muktheeswarar.
 */

abstract class UseCase constructor(
    private val threadExecutor: Scheduler,
    private val postExecutionScheduler: Scheduler
) {

    abstract fun buildUseCaseObservable(action: Action, state: State): Observable<Action>

    fun execute(action: Action, state: State): Observable<Action> {
        return buildUseCaseObservable(action, state)
            .subscribeOn(threadExecutor)
            .observeOn(postExecutionScheduler)
    }
}

