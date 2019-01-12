package msa.notes

import android.app.Application
import org.koin.android.ext.android.startKoin
import timber.log.Timber

/**
 * Created by Abhi Muktheeswarar.
 */

class NotesApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        startKoin(this, listOf(appModule, stateMachineModule, useCaseModule, viewModelModule))
    }
}