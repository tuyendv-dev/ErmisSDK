@file:JvmName("FlowExtensions")

package network.ermis.client.utils.extensions

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.flow.Flow

/**
 * Creates a [LiveData] that has values collected from the original [Flow].
 */
@Suppress("NEWER_VERSION_IN_SINCE_KOTLIN")
@SinceKotlin("99999.9")
public fun <T> Flow<T>.asLiveData(): LiveData<T> = this.asLiveData()
