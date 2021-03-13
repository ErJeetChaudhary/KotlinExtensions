internal fun <T> LiveData<T>.distinctUntilChanged(): LiveData<T> = MediatorLiveData<T>().also { mediator ->

    mediator.addSource(this, object : Observer<T> {

        private var isInitialised = false
        private var cachedValue: T? = null

        override fun onChanged(newValue: T) {
            val wasInitialised = isInitialised
            if (!isInitialised) {
                isInitialised = true
            }
            if (!wasInitialised || newValue != cachedValue) {
                cachedValue = newValue
                mediator.postValue(newValue)
            }
        }
    })
}


class LiveEvent<T> : MutableLiveData<T>() {

    private val pending = AtomicBoolean(false)
    
    @MainThread
    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
        if (hasActiveObservers()) Logger.wtf(TAG, "Multiple observers registered but only one will be notified of changes.")
        // Observe the internal MutableLiveData
        super.observe(owner, Observer<T> { t ->
            if (pending.compareAndSet(true, false)) observer.onChanged(t)
        })
    }

    override fun observeForever(observer: Observer<in T>) {
        super.observeForever { if (pending.compareAndSet(true, false)) observer.onChanged(it) }
    }

    @MainThread
    override fun setValue(@Nullable t: T?) {
        pending.set(true)
        super.setValue(t)
    }

    @MainThread
    fun clear() = super.setValue(null)
}

class DebounceLiveData<T>(source: LiveData<T>, duration: Long) : MediatorLiveData<T>() {

    private val handler = Handler()

    private val runnable = Runnable { this@DebounceLiveData.postValue(source.value) }

    init {
        addSource(source) {
            handler.removeCallbacks(runnable)
            handler.postDelayed(runnable, duration)
        }
    }
}

internal fun <T> LiveData<Event<T>>.eventObserver(owner: LifecycleOwner, event: (T) -> Unit) {
    observe(owner, EventObserver(event))
}
