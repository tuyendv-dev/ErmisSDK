package network.ermis.client

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import network.ermis.core.internal.coroutines.DispatcherProvider
import io.getstream.log.taggedLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.atomic.AtomicBoolean

internal class StreamLifecycleObserver(
    private val scope: CoroutineScope,
    private val lifecycle: Lifecycle,
) : DefaultLifecycleObserver {

    private val logger by taggedLogger("Chat:LifecycleObserver")
    private var recurringResumeEvent = false
    private var handlers = setOf<LifecycleHandler>()

    private val isObserving = AtomicBoolean(false)

    suspend fun observe(handler: LifecycleHandler) {
        if (isObserving.compareAndSet(false, true)) {
            recurringResumeEvent = false
            withContext(DispatcherProvider.Main) {
                lifecycle.addObserver(this@StreamLifecycleObserver)
                logger.v { "[observe] subscribed" }
            }
        }
        handlers = handlers + handler
    }

    suspend fun dispose(handler: LifecycleHandler) {
        handlers = handlers - handler
        if (handlers.isEmpty() && isObserving.compareAndSet(true, false)) {
            withContext(DispatcherProvider.Main) {
                lifecycle.removeObserver(this@StreamLifecycleObserver)
                logger.v { "[dispose] unsubscribed" }
            }
        }
    }

    override fun onResume(owner: LifecycleOwner) {
        logger.d { "[onResume] owner: $owner, recurringResumeEvent: $recurringResumeEvent" }
        // ignore event when we just started observing the lifecycle
        if (recurringResumeEvent) {
            scope.launch {
                handlers.forEach { it.resume() }
            }
        }
        recurringResumeEvent = true
    }

    override fun onStop(owner: LifecycleOwner) {
        scope.launch {
            logger.d { "[onStop] owner: $owner" }
            handlers.forEach { it.stopped() }
        }
    }
}

internal interface LifecycleHandler {
    suspend fun resume()
    suspend fun stopped()
}
