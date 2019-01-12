package msa.notes.base

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject
import timber.log.Timber

/**
 * Created by Abhi Muktheeswarar.
 */

abstract class BaseFragment : Fragment() {

    private var compositeDisposable = CompositeDisposable()

    private lateinit var connectivityManager: ConnectivityManager

    private val networkCallback: ConnectivityManager.NetworkCallback by lazy {
        object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                Timber.d("onAvailable")
                networkSubject.onNext(true)

            }

            override fun onUnavailable() {
                super.onUnavailable()
                Timber.d("onUnavailable")
                networkSubject.onNext(false)
            }


            override fun onLost(network: Network) {
                super.onLost(network)
                Timber.d("onLost")
                networkSubject.onNext(false)
            }

        }
    }

    private val networkSubject by lazy { PublishSubject.create<Boolean>() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return if (getLayoutId() != -1) inflater.inflate(getLayoutId(), container, false)
        else super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
        if (::connectivityManager.isInitialized) {
            connectivityManager.unregisterNetworkCallback(networkCallback)
            networkSubject.onComplete()
        }
    }

    protected fun addDisposable(disposable: Disposable) {
        compositeDisposable.add(disposable)
    }

    protected fun observeNetworkConnectivity(): Observable<Boolean> {

        connectivityManager =
                context!!.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkRequestBuilder = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        connectivityManager.registerNetworkCallback(networkRequestBuilder.build(), networkCallback)

        return networkSubject
    }

    @LayoutRes
    protected abstract fun getLayoutId(): Int
}