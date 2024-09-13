package network.ermis.client.api.interceptor

import network.ermis.client.plugin.requests.ApiRequestsAnalyser
import network.ermis.core.internal.StreamHandsOff
import network.ermis.core.models.Constants
import okhttp3.Interceptor
import okhttp3.Response
import okio.Buffer
import java.io.OutputStream

/**
 * Retrofit's [Interceptor] to use [ApiRequestsAnalyser] so all requests are recorded then the user can
 * analyse then latter.
 */
internal class ApiRequestAnalyserInterceptor(private val requestsAnalyser: ApiRequestsAnalyser) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        val buffer = Buffer()
        val stringOutputStream = StringOutputStream()

        chain.request().body?.writeTo(buffer)
        writeRequestBody(stringOutputStream, buffer)

        requestsAnalyser.registerRequest(request.url.toString(), mapOf("body" to stringOutputStream.toString()))

        return chain.proceed(request)
    }

    @StreamHandsOff(
        reason = "Request body shouldn't be written entirely as it might produce OutOfMemory " +
            "exceptions when sending big files." +
            " The output will be limited to ${Constants.MAX_REQUEST_BODY_LENGTH} bytes.",
    )
    private fun writeRequestBody(stringOutputStream: StringOutputStream, buffer: Buffer) {
        buffer.writeTo(stringOutputStream, minOf(buffer.size, Constants.MAX_REQUEST_BODY_LENGTH))
    }
}

private class StringOutputStream : OutputStream() {

    private val stringBuilder = StringBuilder()

    override fun write(b: Int) {
        stringBuilder.append(b.toChar())
    }

    override fun toString(): String = stringBuilder.toString().ifEmpty { "no_body" }
}
