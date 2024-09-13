package network.ermis.core.internal.coroutines

import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * A synchronous data stream that emits values and completes normally.
 */
public class Tube<T> : Flow<T>, FlowCollector<T> {

    private val mutex = Mutex()
    private val collectors = hashSetOf<FlowCollector<T>>()

    /**
     * Adds the given [collector] into [collectors] and suspends until cancellation.
     *
     * This method is **thread-safe** and can be safely invoked from concurrent coroutines without
     * external synchronization.
     */
    override suspend fun collect(collector: FlowCollector<T>) {
        try {
            mutex.withLock {
                collectors.add(collector)
            }
            awaitCancellation()
        } catch (_: Throwable) {
            /* no-op */
        } finally {
            mutex.withLock {
                collectors.remove(collector)
            }
        }
    }

    /**
     * Emits a [value] to the [collectors], suspending until the [value] is fully consumed.
     *
     * This method is **thread-safe** and can be safely invoked from concurrent coroutines without
     * external synchronization.
     */
    override suspend fun emit(value: T) {
        mutex.withLock {
            collectors.forEach {
                try {
                    it.emit(value)
                } catch (_: Throwable) {
                    /* no-op */
                }
            }
        }
    }
}
