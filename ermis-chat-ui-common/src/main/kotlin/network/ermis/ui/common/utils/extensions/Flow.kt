package network.ermis.ui.common.utils.extensions

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach

/**
 * Calls the [onFirst] lambda when the first element is emitted by the upstream [Flow].
 *
 * @param onFirst The lambda called when the first element is emmited by the upstream [Flow].
 *
 * @return The upstream [Flow] this operator was called on.
 */
public fun <T> Flow<T>.onFirst(onFirst: (T) -> Unit): Flow<T> {
    var wasFirstEmission = false

    return onEach {
        if (wasFirstEmission) return@onEach
        wasFirstEmission = true
        onFirst(it)
    }
}
