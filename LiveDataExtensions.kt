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
