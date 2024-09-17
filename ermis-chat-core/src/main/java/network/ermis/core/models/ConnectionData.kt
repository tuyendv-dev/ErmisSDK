package network.ermis.core.models

import androidx.compose.runtime.Immutable

@Immutable
public data class ConnectionData(val user: User, val connectionId: String)
