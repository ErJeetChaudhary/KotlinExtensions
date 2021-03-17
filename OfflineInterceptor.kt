import okhttp3.Interceptor
import okhttp3.Response

abstract class OfflineInterceptor : Interceptor {

    abstract fun isNetworkAvailable(): Boolean

    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        if (!isNetworkAvailable()) {
            request = request.newBuilder()
                    .removeHeader("Pragma")
                    .header("Cache-Control", "public, only-if-cached")
                    .build()
            val response = chain.proceed(request)
            Logger.wtf("OfflineInterceptor", "cache response: ${response.cacheResponse()}")
            return response
        }
        return chain.proceed(request)
    }
}
