package network.ermis.client.api.interceptor

import network.ermis.core.errors.ChatErrorCode
import network.ermis.client.errors.ChatRequestError
import network.ermis.client.parser.ChatParser
import network.ermis.client.token.TokenManager
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

internal class TokenAuthInterceptor internal constructor(
    private val tokenManager: TokenManager,
    private val parser: ChatParser,
    private val isAnonymous: () -> Boolean,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        if (isAnonymous()) {
            return chain.proceed(chain.request())
        } else {
            if (!tokenManager.hasTokenProvider()) {
                val description = ChatErrorCode.UNDEFINED_TOKEN.description
                val code = ChatErrorCode.UNDEFINED_TOKEN.code
                throw ChatRequestError(description, code, -1)
            }

            tokenManager.ensureTokenLoaded()

            val request: Request = addTokenHeader(chain.request())
            var response: Response = chain.proceed(request)

            if (!response.isSuccessful) {
                val err = parser.toError(response)
                if (err.serverErrorCode == ChatErrorCode.TOKEN_EXPIRED.code) {
                    tokenManager.expireToken()
                    tokenManager.loadSync()
                    response.close()
                    response = chain.proceed(request)
                } else {
                    throw ChatRequestError(err.message, err.serverErrorCode, err.statusCode, err.cause)
                }
            }
            return response
        }
    }

    private fun addTokenHeader(request: Request): Request = tokenManager.getToken().let { token ->
        try {
            request.newBuilder().header(AUTH_HEADER, token).build()
        } catch (e: IllegalArgumentException) {
            throw ChatRequestError(
                "${ChatErrorCode.INVALID_TOKEN.description}: '$token'",
                ChatErrorCode.INVALID_TOKEN.code,
                -1,
                e,
            )
        }
    }

    companion object {
        const val AUTH_HEADER = "Authorization"
    }
}
