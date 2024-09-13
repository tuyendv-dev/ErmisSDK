package network.ermis.client.api.interceptor

import okhttp3.Interceptor
import okhttp3.Response

internal class HeadersInterceptor(private val isAnonymous: () -> Boolean) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        // val authType = if (isAnonymous()) "anonymous" else "jwt"
        val request = chain.request()
            .newBuilder()
            .addHeader("Content-Type", "application/json;charset=utf-8")
            // .addHeader("stream-auth-type", authType)
            // .addHeader("X-Stream-Client", ChatClient.buildSdkTrackingHeaders())
            .addHeader("Cache-Control", "no-cache")
            .addHeader("Accept", "application/json")
            // TODO tuyendv zip body https://stackoverflow.com/questions/33889840/retrofit-and-okhttp-gzip-decode
            // .addHeader("Accept", "*/*")
            // .addHeader("Accept-Encoding", "gzip")//, deflate, br")
            // .addHeader("Connection", "keep-alive")
            .build()
        return chain.proceed(request)
    }
}
