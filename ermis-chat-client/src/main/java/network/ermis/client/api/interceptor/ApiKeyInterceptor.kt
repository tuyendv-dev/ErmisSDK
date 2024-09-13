package network.ermis.client.api.interceptor

import okhttp3.Interceptor
import okhttp3.Response

internal class ApiKeyInterceptor(private val apiKey: String) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        // logger.i { "ApiKeyInterceptor - userId $userId" }
        val original = chain.request()
        val url = original.url.newBuilder()
            .addQueryParameter(PARAM_API_KEY, apiKey)
            // .addQueryParameter(PARAM_USER_ID, userId)
            .build()
        val request = original.newBuilder()
            .url(url)
            .build()
        return chain.proceed(request)
    }

    companion object {
        private const val PARAM_API_KEY = "api_key"
        private const val PARAM_USER_ID = "user_id"
    }
}
