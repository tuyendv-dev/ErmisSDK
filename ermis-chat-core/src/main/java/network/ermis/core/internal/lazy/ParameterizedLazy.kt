package network.ermis.core.internal.lazy

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

public class ParameterizedLazy<T, R>(
    private val initializer: suspend (T) -> R,
) : suspend (T) -> R {

    private var values = hashMapOf<T, R>()

    private val mutex = Mutex()

    /**
     * Provides either an existing [R] object or creates a new one using [initializer] function.
     */
    override suspend fun invoke(param: T): R {
        return values[param] ?: mutex.withLock {
            values[param] ?: initializer(param).also {
                values[param] = it
            }
        }
    }
}

public fun <T, R> parameterizedLazy(initializer: suspend (T) -> R): ParameterizedLazy<T, R> = ParameterizedLazy(
    initializer,
)
