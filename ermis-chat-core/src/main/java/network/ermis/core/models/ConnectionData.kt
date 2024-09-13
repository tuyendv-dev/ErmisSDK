package network.ermis.core.models

import androidx.compose.runtime.Immutable
import network.ermis.core.models.User

@Immutable
public data class ConnectionData(val user: User, val connectionId: String)
