class NetworkConnectionInterceptor() : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())
        val cacheControl = response.header("Cache-Control")
        if (cacheControl == null || cacheControl.contains("no-store") || cacheControl.contains("no-cache") ||
                cacheControl.contains("must-revalidate") || cacheControl.contains("max-age=0")) {
            return response.newBuilder()
                    .removeHeader("Pragma")
                    .header("Cache-Control", "public, max-age=" + 5000)
                    .build()
        }
        return response
    }
}

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
