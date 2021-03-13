/**
 * Created by Jitendra on 17:20, 29-01-2020
 */
abstract class BasePageKeyedDataSource<Key, Value> : PageKeyedDataSource<Key, Value>(), CoroutineScope {

    protected val count = 20

    protected val _resultStatus = MutableLiveData<Event<ResultStatus>>()

    val resultStatus: LiveData<Event<ResultStatus>> = _resultStatus

    override val coroutineContext = Dispatchers.IO + SupervisorJob() + CoroutineExceptionHandler { _, throwable ->
        Logger.wtf(TAG, "error: $throwable")
        _resultStatus.postValue(Event(ResultStatus.Error))
    }
}

class SearchDataSource(private val api: UserUseCase, private val keyword: String? = null) : BasePageKeyedDataSource<Long, Article>() {

    override fun loadInitial(params: LoadInitialParams<Long>, callback: LoadInitialCallback<Long, Article>) {
        launch {
            if (!keyword.isNullOrEmpty()) {
                _resultStatus.postValue(Event(ResultStatus.Refresh))
                val data = api.getSearchResult(count, 0, keyword)
                if (data.status) {
                    _resultStatus.postValue(Event(ResultStatus.NotEmpty))
                    callback.onResult(data.articles!!, null, 1)
                } else {
                    _resultStatus.postValue(Event(ResultStatus.Empty))
                }
            }
        }
    }

    override fun loadBefore(params: LoadParams<Long>, callback: LoadCallback<Long, Article>) {

    }

    override fun loadAfter(params: LoadParams<Long>, callback: LoadCallback<Long, Article>) {
        launch {
            if (!keyword.isNullOrEmpty()) {
                val data = api.getSearchResult(count, count * params.key, keyword)
                if (data.status) {
                    callback.onResult(data.articles!!, params.key + 1)
                }
            }
        }
    }
}

class SearchSourceFactory(private val api: UserUseCase) : DataSource.Factory<Long, Article>() {

    private lateinit var mDataSource: SearchDataSource

    var keyword: String? = null

    val mSourceLiveData = MutableLiveData<Event<SearchDataSource>>()

    override fun create(): DataSource<Long, Article> {
        mDataSource = SearchDataSource(api, keyword)
        mSourceLiveData.postValue(Event(mDataSource))
        return mDataSource
    }
}

class SearchViewModel(val sourceFactory: SearchSourceFactory) : ScopedModel() {

    private val config = PagedList.Config.Builder()
            .setEnablePlaceholders(true)
            .setInitialLoadSizeHint(10)
            .setPageSize(20)
            .setPrefetchDistance(4)
            .build()

    val articles: LiveData<PagedList<Article>>

    init {
        articles = LivePagedListBuilder<Long, Article>(sourceFactory, config).build()
    }

    fun observeDataSource(lifecycleOwner: LifecycleOwner, block: (ResultStatus) -> Unit) {
        sourceFactory.mSourceLiveData.observe(lifecycleOwner, EventObserver {
            it.resultStatus.observe(lifecycleOwner, EventObserver { resultStatus ->
                when (resultStatus) {
                    ResultStatus.Refresh -> refreshing(true)
                    ResultStatus.Empty, ResultStatus.NotEmpty, ResultStatus.Error -> refreshing(false)
                }
                block(resultStatus)
            })
        })
    }

    fun search(query: String?) {
        sourceFactory.keyword = query
        articles.value?.dataSource?.invalidate()
    }

    override fun onNetworkCall() {
        articles.value?.dataSource?.invalidate()
        refreshing(false)
    }

    class Factory(val sourceFactory: SearchSourceFactory) : ViewModelProvider.Factory {

        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return SearchViewModel(sourceFactory) as T
        }
    }
}
