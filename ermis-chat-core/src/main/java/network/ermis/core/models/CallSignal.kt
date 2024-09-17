package network.ermis.core.models

import androidx.compose.runtime.Immutable

@Immutable
public data class CallSignal(
    val type: String,
    val sdp: String,
)
