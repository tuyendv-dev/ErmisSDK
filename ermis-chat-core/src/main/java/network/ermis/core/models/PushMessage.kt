package network.ermis.core.models

import androidx.compose.runtime.Immutable

@Immutable
public data class PushMessage(
    val messageId: String,
    val channelId: String,
    val channelType: String,
    val getstream: Map<String, Any?>,
    val extraData: Map<String, Any?>,
    val metadata: Map<String, Any?>,
)
