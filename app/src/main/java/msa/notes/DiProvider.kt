package msa.notes

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import msa.data.DataStoreFactory
import msa.data.NotesRepository
import msa.domain.repository.Repository
import msa.domain.statemachine.AppStateMachine
import msa.domain.usecases.*
import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

/**
 * Created by Abhi Muktheeswarar.
 */

val appModule = module {

    single { DataStoreFactory(get()) }
    single<Repository> { NotesRepository(get()) }
    single(name = "threadExecutor") { Schedulers.io() }
    single(name = "postExecutionScheduler") { AndroidSchedulers.mainThread() }
}

val stateMachineModule = module {

    factory { AppStateMachine(get(), get(), get(), get(), get()) }
}

val useCaseModule = module {

    factory { GetNotes(get(), get(name = "threadExecutor"), get(name = "postExecutionScheduler")) }
    factory { InsertNote(get(), get(name = "threadExecutor"), get(name = "postExecutionScheduler")) }
    factory { UpdateNote(get(), get(name = "threadExecutor"), get(name = "postExecutionScheduler")) }
    factory { DeleteNote(get(), get(name = "threadExecutor"), get(name = "postExecutionScheduler")) }
    factory { GetNote(get(), get(name = "threadExecutor"), get(name = "postExecutionScheduler")) }
}

val viewModelModule = module {

    viewModel { NotesViewModel(get()) }

}