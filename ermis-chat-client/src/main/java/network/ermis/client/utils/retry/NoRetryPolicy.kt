
package network.ermis.client.utils.retry

import io.getstream.result.Error
import io.getstream.result.call.retry.RetryPolicy

/**
 * Default retry policy that won't retry any calls.
 */
internal class NoRetryPolicy : RetryPolicy {
    /**
     * Shouldn't retry any calls.
     *
     * @return false
     */
    override fun shouldRetry(attempt: Int, error: Error): Boolean = false

    /**
     * Should never be called as the policy doesn't allow retrying.
     */
    override fun retryTimeout(attempt: Int, error: Error): Int = 0
}
