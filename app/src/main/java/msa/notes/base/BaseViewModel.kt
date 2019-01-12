package msa.notes.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.jakewharton.rxrelay2.PublishRelay
import com.jakewharton.rxrelay2.Relay
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import msa.domain.core.Action
import msa.domain.core.State

/**
 * Created by Abhi Muktheeswarar.
 */

abstract class BaseViewModel : ViewModel() {

    protected val inputRelay: Relay<Action> = PublishRelay.create()
    protected val mutableState = MutableLiveData<State>()

    private var compositeDisposable = CompositeDisposable()

    val input: Consumer<Action> = inputRelay
    val state: LiveData<State> = mutableState

    protected fun addDisposable(disposable: Disposable) {
        compositeDisposable.add(disposable)
    }
}