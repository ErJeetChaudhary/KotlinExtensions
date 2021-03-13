/**
 * Created by Jitendra on 12:05, 07-06-2019
 */
class ConnectionLiveData(private val mContext: Context) : LiveData<ConnectionModel>() {

    private val networkReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            intent.extras?.let { bundle ->
                val activeNetwork = bundle[ConnectivityManager.EXTRA_NETWORK_INFO] as NetworkInfo?
                activeNetwork?.let {
                    if (it.isConnectedOrConnecting) {
                        when (it.type) {
                            ConnectivityManager.TYPE_WIFI -> postValue(ConnectionModel(ConnectionType.WIFI, true))
                            ConnectivityManager.TYPE_MOBILE -> postValue(ConnectionModel(ConnectionType.MOBILE, true))
                        }
                    }
                }
            }
        }
    }

    override fun onActive() {
        super.onActive()
        val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        mContext.registerReceiver(networkReceiver, filter)
        LocalBroadcastManager.getInstance(mContext).registerReceiver(networkReceiver, filter)
    }

    override fun onInactive() {
        super.onInactive()
        mContext.unregisterReceiver(networkReceiver)
        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(networkReceiver)
    }

    interface ConnectionType {
        companion object {
            const val WIFI = 0x01
            const val MOBILE = 0x02
        }
    }
}

data class ConnectionModel(val type: Int, val isConnected: Boolean = false)

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class LiveNetworkManager(mContext: Context) : LiveData<ConnectionModel>() {

    private val mManager = mContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?

    private val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .build()

    private val mCallbacks = object : ConnectivityManager.NetworkCallback() {

        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            postValue(ConnectionModel(0, true))
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onActive() {
        super.onActive()
        mManager?.requestNetwork(request, mCallbacks)
    }

    override fun onInactive() {
        super.onInactive()
        mManager?.unregisterNetworkCallback(mCallbacks)
    }
}
