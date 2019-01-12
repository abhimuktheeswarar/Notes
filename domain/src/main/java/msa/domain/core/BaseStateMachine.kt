package msa.domain.core

import com.jakewharton.rxrelay2.Relay
import io.reactivex.Observable

/**
 * Created by Abhi Muktheeswarar.
 */

interface Action

interface State

interface BaseStateMachine<S : State> {

    val input: Relay<Action>

    val state: Observable<S>

    fun reducer(state: S, action: Action): S
}