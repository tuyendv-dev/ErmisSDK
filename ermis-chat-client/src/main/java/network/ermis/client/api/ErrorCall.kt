package network.ermis.client.api

import network.ermis.core.internal.coroutines.DispatcherProvider
import io.getstream.result.Error
import io.getstream.result.Result
import io.getstream.result.call.Call
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class ErrorCall<T : Any>(
    private val scope: CoroutineScope,
    private val e: Error,
) : Call<T> {
    override fun cancel() {
        // Not supported
    }

    override fun execute(): Result<T> {
        return Result.Failure(e)
    }

    override fun enqueue(callback: Call.Callback<T>) {
        scope.launch(DispatcherProvider.Main) {
            callback.onResult(Result.Failure(e))
        }
    }

    override suspend fun await(): Result<T> = withContext(scope.coroutineContext) {
        Result.Failure(e)
    }
}
