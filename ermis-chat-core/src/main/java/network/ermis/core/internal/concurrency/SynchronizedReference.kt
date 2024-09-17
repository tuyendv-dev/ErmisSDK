package network.ermis.core.internal.concurrency

/**
 * An object reference that may be updated thread-safely.
 */
public class SynchronizedReference<T : Any>(
    @Volatile private var value: T? = null,
) {

    /**
     * Provides an existing [T] object reference.
     */
    public fun get(): T? = value

    /**
     * Provides either an existing [T] object or creates a new one using [builder] function.
     *
     * This method is **thread-safe** and can be safely invoked without external synchronization.
     */
    public fun getOrCreate(builder: () -> T): T {
        return value ?: synchronized(this) {
            value ?: builder.invoke().also {
                value = it
            }
        }
    }

    /**
     * Drops an existing [T] object reference to null.
     */
    public fun reset(): Boolean {
        return set(null) != null
    }

    /**
     * Accepts [value] instance of [T] and holds its reference.
     *
     * This method is **thread-safe** and can be safely invoked without external synchronization.
     */
    public fun set(value: T?): T? {
        synchronized(this) {
            val currentValue = this.value
            this.value = value
            return currentValue
        }
    }
}
