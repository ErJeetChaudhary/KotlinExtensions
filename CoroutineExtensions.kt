open class IOScopedObject : CoroutineScope {

    override val coroutineContext = Dispatchers.IO

}
