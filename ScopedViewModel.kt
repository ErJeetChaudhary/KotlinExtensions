abstract class ScopedModel : ViewModel(), CoroutineScope {

    private val job = SupervisorJob()

    val refresh = MutableLiveData<Boolean>()

    val status = MutableLiveData<Event<Status>>()

    override val coroutineContext = Dispatchers.IO + job

    fun refreshing(isRefreshing: Boolean) {
        refresh.postValue(isRefreshing)
    }

    fun setStatus(status: Status) {
        this.status.postValue(Event(status))
    }

    inline fun doOnNetwork(crossinline run: suspend () -> Unit, noinline error: (message: String?) -> Unit = {}) {
        launch(getJobErrorHandler(error)) { run() }
    }

    inline fun doOnRefreshNetwork(crossinline block: suspend () -> Unit, noinline error: (message: String?) -> Unit = {}) {
        refreshing(true)
        launch(getJobErrorHandler(error)) {
            block()
            refreshing(false)
        }
    }

    fun getJobErrorHandler(error: (message: String?) -> Unit = {}) = CoroutineExceptionHandler { _, throwable ->
        error.invoke(throwable.message)
        refreshing(false)
        throwable.printStackTrace()
        Logger.wtf("ScopedModel", "getJobErrorHandler() error: $throwable")
        FirebaseCrashlytics.getInstance().recordException(throwable)
    }

    open fun onNetworkCall() {

    }

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }
}
