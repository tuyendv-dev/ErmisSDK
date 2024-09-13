
package network.ermis.ui.utils.extensions

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

internal fun <T, K, R> LiveData<T>.combineWith(
    liveData: LiveData<K>,
    block: (T?, K?) -> R,
): LiveData<R> {
    val result = MediatorLiveData<R>()
    result.addSource(this) {
        result.value = block(this.value, liveData.value)
    }
    result.addSource(liveData) {
        result.value = block(this.value, liveData.value)
    }
    return result
}

/**
 * Adds [Flow] as a source to this [MediatorLiveData].
 */
internal fun <T, S> MediatorLiveData<T>.addFlow(
    context: CoroutineContext = EmptyCoroutineContext,
    source: Flow<S>,
    onChanged: Observer<in S>,
) {
    source.asLiveData(context).also { liveData ->
        addSource(liveData, onChanged)
        context[Job]?.invokeOnCompletion {
            removeSource(liveData)
        }
    }
}
