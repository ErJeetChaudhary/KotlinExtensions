import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.tuk.education.config.Logger
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

class AutoDisposable : LifecycleObserver {

    private lateinit var disposable: CompositeDisposable

    fun bindTo(lifecycle: Lifecycle) {
        lifecycle.addObserver(this)
        disposable = CompositeDisposable()
    }

    fun add(disposal: Disposable) {
        if (::disposable.isInitialized) {
            disposable.add(disposal)
        } else {
            throw NotImplementedError("must bind AutoDisposable to a lifecycle first")
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        Logger.wtf("AutoDisposable", "disposing disposable: ${disposable.size()}")
        disposable.dispose()
    }
}
