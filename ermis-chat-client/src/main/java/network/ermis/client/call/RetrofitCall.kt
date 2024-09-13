package network.ermis.client.call

import network.ermis.core.errors.ChatErrorCode
import network.ermis.client.errors.ChatRequestError
import network.ermis.core.errors.fromChatErrorCode
import network.ermis.client.parser.ChatParser
import network.ermis.core.internal.coroutines.DispatcherProvider
import io.getstream.result.Error
import io.getstream.result.Result
import io.getstream.result.call.Call
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import retrofit2.Response
import retrofit2.awaitResponse

internal class RetrofitCall<T : Any>(
    private val call: retrofit2.Call<T>,
    private val parser: ChatParser,
    scope: CoroutineScope,
) : Call<T> {
    private val callScope = scope + SupervisorJob(scope.coroutineContext.job)

    override fun cancel() {
        call.cancel()
        callScope.coroutineContext.cancelChildren()
    }

    override fun execute(): Result<T> = runBlocking { await() }

    override fun enqueue(callback: Call.Callback<T>) {
        callScope.launch { notifyResult(call.getResult(), callback) }
    }

    override suspend fun await(): Result<T> = Call.runCatching {
        withContext(callScope.coroutineContext) {
            call.getResult()
        }
    }

    private suspend fun notifyResult(result: Result<T>, callback: Call.Callback<T>) =
        withContext(DispatcherProvider.Main) {
            callback.onResult(result)
        }

    private fun Throwable.toFailedResult(): Result<T> = Result.Failure(this.toFailedError())

    private fun Throwable.toFailedError(): Error = when (this) {
        is ChatRequestError -> Error.NetworkError(
            serverErrorCode = streamCode,
            message = message.toString(),
            statusCode = statusCode,
            cause = cause,
        )
        else -> Error.NetworkError.fromChatErrorCode(
            chatErrorCode = ChatErrorCode.NETWORK_FAILED,
            cause = this,
        )
    }

    @Suppress("TooGenericExceptionCaught")
    private suspend fun retrofit2.Call<T>.getResult(): Result<T> = withContext(callScope.coroutineContext) {
        try {
            awaitResponse().getResult()
        } catch (t: Throwable) {
            t.toFailedResult()
        }
    }

    @Suppress("TooGenericExceptionCaught")
    private suspend fun Response<T>.getResult(): Result<T> = withContext(callScope.coroutineContext) {
        if (isSuccessful) {
            try {
                Result.Success(body()!!)
            } catch (t: Throwable) {
                t.toFailedResult()
            }
        } else {
            val errorBody = errorBody()

            if (errorBody != null) {
                Result.Failure(parser.toError(errorBody))
            } else {
                Result.Failure(parser.toError(raw()))
            }
        }
    }
}
