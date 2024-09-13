package network.ermis.offline.extensions

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

internal suspend fun CoroutineScope.launchWithMutex(mutex: Mutex, block: suspend () -> Unit) = launch {
    mutex.withLock { block() }
}
