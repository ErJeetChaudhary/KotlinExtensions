/**
 * Created by Jitendra on 10:48, 31-08-2020
 */
class EventObserver<T>(private val onEventUnhandledContent: (T) -> Unit) : Observer<Event<T>> {
    override fun onChanged(event: Event<T>?) {
        event?.getContentIfNotHandledOrNull()?.let { value ->
            onEventUnhandledContent(value)
        }
    }
}

open class Event<out T>(private val content: T) {

    var hasBeenHandled = false
        private set // Allow external read but not write

    /**
     * Returns the content and prevents its use again.
     */
    fun getContentIfNotHandledOrNull(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }


    /**
     * Returns the content, even if it's already been handled.
     */
    fun peekContent(): T = content

}

internal fun <T> LiveData<Event<T>>.eventObserver(owner: LifecycleOwner, event: (T) -> Unit) {
    observe(owner, EventObserver(event))
}
