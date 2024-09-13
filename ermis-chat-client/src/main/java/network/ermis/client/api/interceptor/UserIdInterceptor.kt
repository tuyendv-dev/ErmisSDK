package network.ermis.client.api.interceptor

import network.ermis.client.token.TokenManager
import network.ermis.client.utils.TokenUtils.getUserId
import network.ermis.client.utils.TokenUtils.logger
import okhttp3.Interceptor
import okhttp3.Response

internal class UserIdInterceptor internal constructor(
    private val tokenManager: TokenManager,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        logger.i { "UserIdInterceptor - intercept ${tokenManager.getToken()}" }
        val original = chain.request()
        // if(userId.isEmpty()) return chain.proceed(original)
        if (tokenManager.getToken().isEmpty()) return chain.proceed(original)
        val url = original.url.newBuilder()
            .addQueryParameter(PARAM_USER_ID, getUserId(tokenManager.getToken()))
            .build()
        val request = original.newBuilder()
            .url(url)
            .build()
        return chain.proceed(request)
    }

    companion object {
        private const val PARAM_USER_ID = "user_id"
    }
}
