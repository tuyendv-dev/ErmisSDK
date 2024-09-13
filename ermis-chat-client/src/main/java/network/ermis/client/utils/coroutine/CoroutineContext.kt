package network.ermis.client.utils.coroutine

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlin.coroutines.CoroutineContext

/**
 * Cancels all children of the [Job] in this context, without touching the state of this job itself
 * with optional cancellation [exclusion] and [cause]. See [Job.cancel].
 * It does not do anything if there is no job in the context or it has no children.
 */
internal fun CoroutineContext.cancelChildrenExcept(exclusion: Job?, cause: CancellationException? = null) {
    this[Job]?.children?.forEach {
        if (exclusion != it) {
            it.cancel(cause)
        }
    }
}
