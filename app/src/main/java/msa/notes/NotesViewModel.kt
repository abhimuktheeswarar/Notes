package msa.notes

import msa.domain.statemachine.AppStateMachine
import msa.notes.base.BaseViewModel
import timber.log.Timber

/**
 * Created by Abhi Muktheeswarar.
 */

class NotesViewModel(appStateMachine: AppStateMachine) : BaseViewModel() {

    init {

        Timber.d("Init...")

        addDisposable(inputRelay.subscribe(appStateMachine.input))
        addDisposable(appStateMachine.state.subscribe { state -> mutableState.value = state })
    }


}