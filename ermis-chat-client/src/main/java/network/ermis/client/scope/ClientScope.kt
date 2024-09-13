package network.ermis.client.scope

import network.ermis.core.internal.coroutines.DispatcherProvider
import io.getstream.result.call.SharedCalls
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

/**
 * A client aware implementation of [CoroutineScope].
 */
internal interface ClientScope : CoroutineScope

/**
 * Creates a client aware [CoroutineScope].
 */
internal fun ClientScope(): ClientScope = ClientScopeImpl()

/**
 * Represents SDK root [CoroutineScope].
 */
private class ClientScopeImpl :
    ClientScope,
    CoroutineScope by CoroutineScope(
        SupervisorJob() + DispatcherProvider.IO + SharedCalls(),
    )
